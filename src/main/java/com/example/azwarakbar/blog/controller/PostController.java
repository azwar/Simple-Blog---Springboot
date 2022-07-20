package com.example.azwarakbar.blog.controller;

import com.example.azwarakbar.blog.exception.UnauthorizedException;
import com.example.azwarakbar.blog.model.Post;
import com.example.azwarakbar.blog.schema.MessageResponse;
import com.example.azwarakbar.blog.service.PostService;
import com.example.azwarakbar.blog.util.PagedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/post")
public class PostController {
    private final PostService postService;
    Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping(value = {"/{category}", "/"}, params = { "page"})
    public ResponseEntity<PagedResponse> getPostCategory(@PathVariable(required = false) Optional<String> category,
                                   @RequestParam(value = "page", defaultValue = "0", required = false) int page) {
        Page<Post> pPost;

        if (category.isEmpty()) {
            pPost = postService.findByStatusTrue(page);
        } else {
            pPost = postService.findByCategoryName(category.get(), page);
        }

        PagedResponse response = new PagedResponse(pPost.getContent(), page, ((int) pPost.getTotalElements()));
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> addPost(@Valid @RequestBody Post post) throws UnauthorizedException {
        ResponseEntity<MessageResponse> result = postService.add(post);

        return result;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable(name = "id") Long id, @Valid @RequestBody Post post) {
        postService.update(post);

        return new ResponseEntity<>(post, HttpStatus.OK);
    }

}
