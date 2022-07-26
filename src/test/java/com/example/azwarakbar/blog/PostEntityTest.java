package com.example.azwarakbar.blog;

import com.example.azwarakbar.blog.model.Category;
import com.example.azwarakbar.blog.model.Post;
import com.example.azwarakbar.blog.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class PostEntityTest {

    private User author;
    private Category category;
    private Post post;

    @BeforeEach
    public void setup() {
        category = new Category();
        category.setName("Tech");

        author = new User();
        author.setName("admin");

        post = new Post();
        post.setTitle("Title first post");
        post.setBody("Body of second post");
    }

    @Test
    void addPostWithCategoryAndUser() {
        post.setCategory(category);
        post.setUser(author);

        Assertions.assertEquals(post.getTitle(), "Title first post");
        Assertions.assertEquals(post.getCategory().getName(), "Tech");
        Assertions.assertEquals(post.getUser().getName(), "admin");
    }

    @Test
    void updatePostWithCategoryAndUser() {
        post.setCategory(category);
        post.setUser(author);

        category.setName("Art");
        author.setName("user1");
        post.setTitle("Title post update");

        Assertions.assertEquals(post.getTitle(), "Title post update");
        Assertions.assertEquals(post.getCategory().getName(), "Art");
        Assertions.assertEquals(post.getUser().getName(), "user1");
    }
}
