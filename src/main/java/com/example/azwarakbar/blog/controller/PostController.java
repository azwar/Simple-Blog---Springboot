package com.example.azwarakbar.blog.controller;

import com.example.azwarakbar.blog.exception.ResourceNotFoundException;
import com.example.azwarakbar.blog.exception.UnauthorizedException;
import com.example.azwarakbar.blog.model.Post;
import com.example.azwarakbar.blog.schema.MessageResponse;
import com.example.azwarakbar.blog.schema.ObjectResponse;
import com.example.azwarakbar.blog.schema.RequestPost;
import com.example.azwarakbar.blog.secure.CurrentUser;
import com.example.azwarakbar.blog.secure.UserPrincipal;
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
    public ResponseEntity<PagedResponse> getPostList(@PathVariable(required = false) Optional<String> category,
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
    public ResponseEntity<MessageResponse> addPost(@Valid @RequestBody RequestPost post, @CurrentUser UserPrincipal currentUser) throws UnauthorizedException {
        ResponseEntity<MessageResponse> result = postService.add(post, currentUser);

        return result;
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> updatePost(@PathVariable(name = "id") Long id, @Valid @RequestBody RequestPost post, @CurrentUser UserPrincipal currentUser) {
        postService.update(id, post, currentUser);

        return ResponseEntity.ok(new MessageResponse(true, "Post successfully updated"));
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deletePost(@PathVariable(name = "id") Long id) {
        postService.delete(id);

        return new ResponseEntity< >(new MessageResponse(true, "Post has been deleted"), HttpStatus.OK);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PagedResponse> listAllPost(@RequestParam(value = "page", defaultValue = "0", required = false) int page) {
        Page<Post> pagedPost = postService.findAllByOrderByCreateDateDesc(page);
        PagedResponse response = new PagedResponse(pagedPost.getContent(), page, ((int) pagedPost.getTotalElements()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<ObjectResponse> getPostDetails(@PathVariable(name = "id") Long id) {
        Post post = postService.findForId(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        ObjectResponse objectResponse = new ObjectResponse(true, post);
        return new ResponseEntity< >(objectResponse, HttpStatus.OK);
    }
}
