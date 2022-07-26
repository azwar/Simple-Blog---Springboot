package com.example.azwarakbar.blog.post;

import com.example.azwarakbar.blog.model.Post;
import com.example.azwarakbar.blog.repository.PostRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeletePostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private DeletePostService deletePostService;

    @Test
    public void whenGivenId_shouldDeletePost_ifFound() {
        Post post = new Post();
        post.setTitle("Title blog post");
        post.setTitle("This is body blog post");
        post.setId(1L);

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        deletePostService.delete(post.getId());
        verify(postRepository).deleteById(post.getId());
    }

    @Test(expected = RuntimeException.class)
    public void should_throw_exception_when_post_doesnt_exist() {
        Post post = new Post();
        post.setId(89L);
        post.setTitle("Another blog title");

        given(postRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));
        deletePostService.delete(post.getId());
    }
}
