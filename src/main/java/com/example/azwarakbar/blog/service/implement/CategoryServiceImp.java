package com.example.azwarakbar.blog.service.implement;

import com.example.azwarakbar.blog.exception.ResourceAlreadyExistException;
import com.example.azwarakbar.blog.exception.ResourceNotFoundException;
import com.example.azwarakbar.blog.model.Category;
import com.example.azwarakbar.blog.repository.CategoryRepository;
import com.example.azwarakbar.blog.service.CategoryService;
import com.example.azwarakbar.blog.util.PagedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImp implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImp(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public PagedResponse<Category> getAllCategories(int page) {
        page = subtractPageByOne(page);
        Pageable pageable = PageRequest.of(page, 10);
        Page<Category> categories = categoryRepository.findAll(pageable);
        List<Category> list = categories.getContent();

        return new PagedResponse(list, page, (int) categories.getTotalElements());
    }

    @Override
    public Category getCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return category;
    }

    @Override
    public Optional<Category> findByName(String categoryName) {
        Optional<Category> category =  categoryRepository.findByName(categoryName);
        return category;
    }

    @Override
    public void delete(Long id) {
        categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        categoryRepository.deleteById(id);
    }

    private int subtractPageByOne(int page){
        return (page < 1) ? 0 : page - 1;
    }

    public Optional<Category> update(Long id, Category inputCategory) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        category.setName(inputCategory.getName());
        Category updatedCategory = categoryRepository.save(category);

        return Optional.of(updatedCategory);
    }

    @Override
    public void add(Category category) {
        Optional<Category> existingCategory = findByName(category.getName());

        if (!existingCategory.isEmpty()) {
            throw new ResourceAlreadyExistException("Category", "name", category.getName());
        }

        Category tmpCategory = categoryRepository.save(category);
    }
}
