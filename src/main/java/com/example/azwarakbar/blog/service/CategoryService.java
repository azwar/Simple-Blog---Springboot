package com.example.azwarakbar.blog.service;

import com.example.azwarakbar.blog.model.Category;
import com.example.azwarakbar.blog.util.PagedResponse;

import java.util.Optional;

public interface CategoryService {
    PagedResponse<Category> getAllCategories(int page);

    Category getCategory(Long id);

    Optional<Category> findByName(String categoryName);
    void delete(Long id);
    void add(Category category);
    Optional<Category> update(Long id, Category category);

}
