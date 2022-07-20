package com.example.azwarakbar.blog.repository;

import com.example.azwarakbar.blog.model.Post;
import com.example.azwarakbar.blog.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByUserOrderByCreateDateDesc(User user, Pageable pageable);
    Page<Post> findByStatusTrue(Pageable pageable);
    Optional<Post> findById(Long id);
    Page<Post> findByUser(User user, Pageable pageable);
    Page<Post> findByCategoryName(String category, Pageable pageable);
}
