package com.example.azwarakbar.blog.service;

import com.example.azwarakbar.blog.model.User;
import com.example.azwarakbar.blog.schema.UserResponse;
import com.example.azwarakbar.blog.secure.UserPrincipal;

import java.util.Optional;

public interface UserService {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    UserResponse getCurrentUser(UserPrincipal currentUser);

    UserResponse getProfile(String username);
}
