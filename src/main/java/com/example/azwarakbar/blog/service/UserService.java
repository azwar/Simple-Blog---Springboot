package com.example.azwarakbar.blog.service;

import com.example.azwarakbar.blog.model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
