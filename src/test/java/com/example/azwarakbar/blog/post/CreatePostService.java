package com.example.azwarakbar.blog.post;

import com.example.azwarakbar.blog.model.Post;
import com.example.azwarakbar.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreatePostService {

    @Autowired
    PostRepository repository;

    public Post createNewPost(Post post) {
        return repository.save(post);
    }
}
