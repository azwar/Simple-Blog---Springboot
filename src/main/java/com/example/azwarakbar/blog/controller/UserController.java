package com.example.azwarakbar.blog.controller;

import com.example.azwarakbar.blog.exception.BlogException;
import com.example.azwarakbar.blog.model.Role;
import com.example.azwarakbar.blog.model.RoleName;
import com.example.azwarakbar.blog.model.User;
import com.example.azwarakbar.blog.repository.RoleRepository;
import com.example.azwarakbar.blog.repository.UserRepository;
import com.example.azwarakbar.blog.schema.JwtAuthenticationResponse;
import com.example.azwarakbar.blog.schema.MessageResponse;
import com.example.azwarakbar.blog.schema.RequestLogin;
import com.example.azwarakbar.blog.schema.RequestSignup;
import com.example.azwarakbar.blog.secure.JwtTokenProvider;
import com.example.azwarakbar.blog.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
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
    private Logger logger = LoggerFactory.getLogger(UserController.class);


    @PostMapping("/auth/signup")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody RequestSignup signupReq) {
        if (Boolean.TRUE.equals(userRepository.existsByUsername(signupReq.getUsername()))) {
            throw new BlogException(HttpStatus.BAD_REQUEST, "Username is already registered");
        }

        if (Boolean.TRUE.equals(userRepository.existsByEmail(signupReq.getEmail()))) {
            throw new BlogException(HttpStatus.BAD_REQUEST, "Email is already registered");
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

//    @PutMapping("/{id}")
//    public ResponseEntity<Category> updateCategory(@PathVariable(name = "id") Long id, @Valid @RequestBody Category category) {
//        categoryService.update(category);
//        return new ResponseEntity<>(category, HttpStatus.OK);
//    }

    @PostMapping("/auth/login")
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@Valid @RequestBody RequestLogin reqLogin) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(reqLogin.getUsernameOrEmail(), reqLogin.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }
}
