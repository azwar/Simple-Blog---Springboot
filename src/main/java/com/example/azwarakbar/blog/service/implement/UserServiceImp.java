package com.example.azwarakbar.blog.service.implement;

import com.example.azwarakbar.blog.exception.ResourceNotFoundException;
import com.example.azwarakbar.blog.exception.UnauthorizedException;
import com.example.azwarakbar.blog.model.Post;
import com.example.azwarakbar.blog.model.Role;
import com.example.azwarakbar.blog.model.User;
import com.example.azwarakbar.blog.repository.RoleRepository;
import com.example.azwarakbar.blog.repository.UserRepository;
import com.example.azwarakbar.blog.schema.MessageResponse;
import com.example.azwarakbar.blog.schema.UserResponse;
import com.example.azwarakbar.blog.secure.UserPrincipal;
import com.example.azwarakbar.blog.service.PostService;
import com.example.azwarakbar.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static com.example.azwarakbar.blog.util.Pager.subtractPageByOne;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserResponse getCurrentUser(UserPrincipal currentUser) {
        return new UserResponse(currentUser.getId(), currentUser.getUsername(), currentUser.getName(), currentUser.getEmail());
    }

    @Override
    public UserResponse getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return new UserResponse(user.getId(), user.getUsername(), user.getName(), "");
    }

    @Override
    public Page<User> findAllByOrderByIdDesc(int page) {
        PageRequest pagable = PageRequest.of(subtractPageByOne(page), 10);
        return userRepository.findAllByOrderByIdDesc(pagable);
    }

    @Override
    public void delete(Long id, UserPrincipal currentUser) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (user.getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("No no you can not delete yourself");
        }

        Collection<Role> userRoles = user.getRoles();
        Collection<Post> userPosts = user.getPosts();

        for (Role role: userRoles) {
            roleRepository.delete(role);
        }

        for (Post post: userPosts) {
            postService.delete(post.getId());
        }

        user.setRoles(new ArrayList<>());
        userRepository.delete(user);
    }
}
