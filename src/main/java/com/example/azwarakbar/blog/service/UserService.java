package com.example.azwarakbar.blog.service;

import com.example.azwarakbar.blog.model.User;
import com.example.azwarakbar.blog.schema.UserResponse;
import com.example.azwarakbar.blog.secure.UserPrincipal;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface UserService {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    UserResponse getCurrentUser(UserPrincipal currentUser);

    UserResponse getProfile(String username);
    Page<User> findAllByOrderByIdDesc(int page);
    void delete(Long id, UserPrincipal userPrincipal);
}
