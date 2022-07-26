package com.example.azwarakbar.blog.post;

import com.example.azwarakbar.blog.model.Post;
import com.example.azwarakbar.blog.repository.PostRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CreatePostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CreatePostService createPostService;

    @Test
    public void whenSavePost_shouldReturnPost() {
        Post post = new Post();
        post.setTitle("Title blog post");
        post.setTitle("This is body blog post");

        when(postRepository.save(ArgumentMatchers.any(Post.class))).thenReturn(post);

        Post created = createPostService.createNewPost(post);

        assertThat(created.getTitle()).isSameAs(post.getTitle());
        verify(postRepository).save(post);
    }
}
