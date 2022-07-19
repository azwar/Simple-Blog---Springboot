package com.example.azwarakbar.blog.service;

import com.example.azwarakbar.blog.model.Post;
import com.example.azwarakbar.blog.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PostService {

    Optional<Post> findForId(Long id);
    Page<Post> findByStatusTrue(int page);
    Page<Post> findAllOrderedByDatePageable(int page);
    void delete(Post post);

    Page<Post> findByCategoryName(String category, int page);
    Optional<Post> add(Post post);
    Optional<Post> update(Post post);

}
