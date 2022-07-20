package com.example.azwarakbar.blog.service.implement;

import com.example.azwarakbar.blog.exception.ResourceNotFoundException;
import com.example.azwarakbar.blog.model.Category;
import com.example.azwarakbar.blog.model.Post;
import com.example.azwarakbar.blog.model.User;
import com.example.azwarakbar.blog.repository.CategoryRepository;
import com.example.azwarakbar.blog.repository.PostRepository;
import com.example.azwarakbar.blog.repository.UserRepository;
import com.example.azwarakbar.blog.schema.MessageResponse;
import com.example.azwarakbar.blog.schema.RequestPost;
import com.example.azwarakbar.blog.secure.CurrentUser;
import com.example.azwarakbar.blog.secure.UserPrincipal;
import com.example.azwarakbar.blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.azwarakbar.blog.util.Pager.subtractPageByOne;

@Service
public class PostServiceImp implements PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public PostServiceImp(UserRepository userRepository, PostRepository postRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Optional<Post> findForId(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public Page<Post> findByStatusTrue(int page) {
        return postRepository.findByStatusTrue(PageRequest.of(subtractPageByOne(page), 10));
    }

    @Override
    public Page<Post> findAllOrderedByDatePageable(int page) {
        return null;
    }

    @Override
    public void delete(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        postRepository.delete(post);
    }

    @Override
    public Page<Post> findByCategoryName(String category, int page) {
        return postRepository.findByCategoryName(category, PageRequest.of(subtractPageByOne(page), 10));
    }

    public void update(Post inputPost) {
        Post post = postRepository.findById(inputPost.getId()).orElseThrow(() -> new ResourceNotFoundException("Post", "id", inputPost.getId()));
        Category category = categoryRepository.findById(inputPost.getCategory().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", inputPost.getCategory().getId()));
//        if (post.getUser().getId().equals(currentUser.getId())
//                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
//            post.setTitle(newPostRequest.getTitle());
//            post.setBody(newPostRequest.getBody());
//            post.setCategory(category);
//            return postRepository.save(post);
//        }
        post.setTitle(inputPost.getTitle());
        post.setBody(inputPost.getBody());
        post.setCategory(category);
        postRepository.save(post);

    }

    @Override
    public ResponseEntity<MessageResponse> add(RequestPost reqPost, UserPrincipal currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));
        Category category = categoryRepository.findById(reqPost.getCategory().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", reqPost.getCategory().getId()));

        Post post = new Post();
        post.setTitle(reqPost.getTitle());
        post.setBody(reqPost.getBody());
        post.setUser(user);
        post.setCategory(category);
        post.setStatus(true);
        postRepository.save(post);
        MessageResponse response = new MessageResponse(true, "Blog post has been saved.");
        return ResponseEntity.ok(response);
    }

    @Override
    public Page<Post> findByUser(String username, int page) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        PageRequest pageable = PageRequest.of(subtractPageByOne(page), 10);
        Page<Post> post = postRepository.findByUser(user, pageable);
        return post;
    }

}
