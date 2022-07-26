package com.example.azwarakbar.blog.post;

import com.example.azwarakbar.blog.exception.ResourceNotFoundException;
import com.example.azwarakbar.blog.model.Post;
import com.example.azwarakbar.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeletePostService {

    @Autowired
    PostRepository repository;

    public void delete(Long id) {
        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        repository.deleteById(id);
    }
}
