package com.example.azwarakbar.blog.controller;

import com.example.azwarakbar.blog.exception.*;
import com.example.azwarakbar.blog.model.Post;
import com.example.azwarakbar.blog.model.Role;
import com.example.azwarakbar.blog.model.RoleName;
import com.example.azwarakbar.blog.model.User;
import com.example.azwarakbar.blog.repository.RoleRepository;
import com.example.azwarakbar.blog.repository.UserRepository;
import com.example.azwarakbar.blog.schema.*;
import com.example.azwarakbar.blog.secure.CurrentUser;
import com.example.azwarakbar.blog.secure.JwtTokenProvider;
import com.example.azwarakbar.blog.secure.UserPrincipal;
import com.example.azwarakbar.blog.service.PostService;
import com.example.azwarakbar.blog.service.UserService;
import com.example.azwarakbar.blog.util.PagedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private UserRepository userRepository;
    private static final String USER_ROLE_NOT_SET = "User role not set";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/auth/signup")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody RequestSignup signupReq) throws BlogException {
        if (Boolean.TRUE.equals(userRepository.existsByUsername(signupReq.getUsername()))) {
            throw new BlogException(HttpStatus.CONFLICT, "Username is already registered");
        }

        if (Boolean.TRUE.equals(userRepository.existsByEmail(signupReq.getEmail()))) {
            throw new BlogException(HttpStatus.CONFLICT, "Email is already registered");
        }

        String username = signupReq.getUsername().toLowerCase();
        String email = signupReq.getEmail().toLowerCase();
        String password = passwordEncoder.encode(signupReq.getPassword());

        User user = new User(signupReq.getFirstName(), signupReq.getLastName(), username, email, password);

        List<Role> roles = new ArrayList<>();

        if (userRepository.count() == 0) {
            roles.add(roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new BlogException(HttpStatus.INTERNAL_SERVER_ERROR, USER_ROLE_NOT_SET)));
            roles.add(roleRepository.findByName(RoleName.ROLE_ADMIN)
                    .orElseThrow(() -> new BlogException(HttpStatus.INTERNAL_SERVER_ERROR, USER_ROLE_NOT_SET)));
        } else {
            roles.add(roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new BlogException(HttpStatus.INTERNAL_SERVER_ERROR, USER_ROLE_NOT_SET)));
        }

        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users/{userId}")
                .buildAndExpand(savedUser.getId()).toUri();

        MessageResponse response = new MessageResponse(Boolean.TRUE, "User registered successfully");
        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@Valid @RequestBody RequestLogin reqLogin) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(reqLogin.getUsernameOrEmail(), reqLogin.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        UserResponse response = userService.getCurrentUser(currentUser);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> profileUser(@PathVariable String username) {
        UserResponse response = userService.getProfile(username);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{username}/posts")
    public ResponseEntity<PagedResponse<Post>> getPostsCreatedBy(@PathVariable(value = "username") String username,
                                                                 @RequestParam(value = "page", required = false, defaultValue = "0") Integer page) {
        Page<Post> post = postService.findByUser(username, page);
        PagedResponse pagedResponse = new PagedResponse(post.getContent(), page, (int) post.getTotalElements());

        return new ResponseEntity<>(pagedResponse, HttpStatus.OK);
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse> listUser(@RequestParam(value = "page", defaultValue = "0", required = false) int page) {
        Page<User> userPage = userService.findAllByOrderByIdDesc(page);
        PagedResponse response = new PagedResponse(userPage.getContent(), page, (int) userPage.getTotalElements());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable(name = "id") Long id, @CurrentUser UserPrincipal currentUser)
        throws UnauthorizedException {
        userService.delete(id, currentUser);
        MessageResponse response = new MessageResponse(true, "User successfully deleted");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/details/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ObjectResponse> getUserDetails(@PathVariable(name = "id") Long id)
            throws UnauthorizedException {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        ObjectResponse response = new ObjectResponse(true, user);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/add")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> addUser(@Valid @RequestBody RequestUser requestUser) throws BlogException {
        if (Boolean.TRUE.equals(userRepository.existsByUsername(requestUser.getUsername()))) {
            throw new BlogException(HttpStatus.CONFLICT, "Username is already registered");
        }

        if (Boolean.TRUE.equals(userRepository.existsByEmail(requestUser.getEmail()))) {
            throw new BlogException(HttpStatus.CONFLICT, "Email is already registered");
        }

        String username = requestUser.getUsername().toLowerCase();
        String email = requestUser.getEmail().toLowerCase();
        String password = passwordEncoder.encode(requestUser.getPassword());

        User user = new User(requestUser.getFirstName(), requestUser.getLastName(), username, email, password);

        List<Role> roles = new ArrayList<>();

        if (userRepository.count() == 0) {
            roles.add(roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new BlogException(HttpStatus.INTERNAL_SERVER_ERROR, USER_ROLE_NOT_SET)));
            roles.add(roleRepository.findByName(RoleName.ROLE_ADMIN)
                    .orElseThrow(() -> new BlogException(HttpStatus.INTERNAL_SERVER_ERROR, USER_ROLE_NOT_SET)));
        } else {
            roles.add(roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new BlogException(HttpStatus.INTERNAL_SERVER_ERROR, USER_ROLE_NOT_SET)));

            if (requestUser.getIsAdmin()) {
                roles.add(roleRepository.findByName(RoleName.ROLE_ADMIN)
                        .orElseThrow(() -> new BlogException(HttpStatus.INTERNAL_SERVER_ERROR, USER_ROLE_NOT_SET)));
            }
        }

        user.setActive(1);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users/{userId}")
                .buildAndExpand(savedUser.getId()).toUri();

        MessageResponse response = new MessageResponse(Boolean.TRUE, "User added successfully");
        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateUser(@PathVariable(name = "id") Long id,
                                                      @Valid @RequestBody RequestUser requestUser) throws BlogException {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (Boolean.TRUE.equals(userRepository.existsByEmail(requestUser.getEmail()))) {
            if(!user.getEmail().equalsIgnoreCase(requestUser.getEmail())) {
                throw new BadRequestException("Email is already used by other user, update using other email is not allowed");
            }
        }

        String username = requestUser.getUsername().toLowerCase();
        String email = requestUser.getEmail().toLowerCase();
        String inputPassword = requestUser.getPassword();
        String fullName = requestUser.getFirstName() + ' ' + requestUser.getLastName();

        user.setUsername(username);
        user.setEmail(email);
        user.setName(fullName);

        if (inputPassword != null && inputPassword.length() > 0) {
            if (inputPassword.length() < 6) {
                throw new BadRequestException("Password must be at least 6 characters. Let it empty if you don't want to update.");
            } else {
                String password = passwordEncoder.encode(inputPassword);
                user.setPassword(password);
            }
        }

        List<Role> roles = new ArrayList<>();

        roles.add(roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new BlogException(HttpStatus.INTERNAL_SERVER_ERROR, USER_ROLE_NOT_SET)));

        if (requestUser.getIsAdmin()) {
            roles.add(roleRepository.findByName(RoleName.ROLE_ADMIN)
                    .orElseThrow(() -> new BlogException(HttpStatus.INTERNAL_SERVER_ERROR, USER_ROLE_NOT_SET)));
        }

        user.setRoles(roles);

        userRepository.save(user);


        MessageResponse response = new MessageResponse(Boolean.TRUE, "User updated successfully");
        return ResponseEntity.ok(response);
    }
}
