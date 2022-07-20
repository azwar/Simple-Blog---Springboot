package com.example.azwarakbar.blog.service;

import com.example.azwarakbar.blog.model.Post;
import com.example.azwarakbar.blog.schema.MessageResponse;
import com.example.azwarakbar.blog.schema.RequestPost;
import com.example.azwarakbar.blog.secure.CurrentUser;
import com.example.azwarakbar.blog.secure.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface PostService {

    Optional<Post> findForId(Long id);
    Page<Post> findByStatusTrue(int page);
    Page<Post> findAllOrderedByDatePageable(int page);
    void delete(Long id);

    Page<Post> findByCategoryName(String category, int page);
    Page<Post> findByUser(String username, int page);
    ResponseEntity<MessageResponse> add(RequestPost post, UserPrincipal currentUser);
    void update(Post post);

}
