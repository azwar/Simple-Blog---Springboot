package com.example.azwarakbar.blog.controller;

import com.example.azwarakbar.blog.exception.ResourceAlreadyExistException;
import com.example.azwarakbar.blog.model.Category;
import com.example.azwarakbar.blog.schema.MessageResponse;
import com.example.azwarakbar.blog.secure.CurrentUser;
import com.example.azwarakbar.blog.secure.UserPrincipal;
import com.example.azwarakbar.blog.service.CategoryService;
import com.example.azwarakbar.blog.util.PagedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;
    Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping(value = "/", params = { "page"})
    public ResponseEntity<PagedResponse> getListCategory(@RequestParam(value = "page", defaultValue = "0", required = false) int page) {
        PagedResponse<Category> category = categoryService.getAllCategories(page);
        return ResponseEntity.ok(category);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryDetails(@PathVariable(name = "id") Long id) {
        Category category = categoryService.getCategory(id);
        return ResponseEntity.ok(category);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Category> addCategory(@Valid @RequestBody Category category,
                                                @CurrentUser UserPrincipal currentUser) throws ResourceAlreadyExistException {
        categoryService.add(category);

        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable(name = "id") Long id, @Valid @RequestBody Category category) {
        categoryService.update(category);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteCategory(@PathVariable(name = "id") Long id) {
        categoryService.delete(id);
        MessageResponse response = new MessageResponse(true, "Category successfully deleted.");
        return ResponseEntity.ok(response);
    }
}
