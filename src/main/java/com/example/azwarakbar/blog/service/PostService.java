package com.example.azwarakbar.blog.service;

import com.example.azwarakbar.blog.model.Post;
import com.example.azwarakbar.blog.schema.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface PostService {

    Optional<Post> findForId(Long id);
    Page<Post> findByStatusTrue(int page);
    Page<Post> findAllOrderedByDatePageable(int page);
    void delete(Post post);

    Page<Post> findByCategoryName(String category, int page);
    ResponseEntity<MessageResponse> add(Post post);
    void update(Post post);

}
