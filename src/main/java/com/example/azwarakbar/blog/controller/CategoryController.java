package com.example.azwarakbar.blog.controller;

import com.example.azwarakbar.blog.exception.ResourceAlreadyExistException;
import com.example.azwarakbar.blog.exception.ResourceNotFoundException;
import com.example.azwarakbar.blog.exception.UnauthorizedException;
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
import java.util.Optional;

@RestController
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<PagedResponse> getListCategory(@RequestParam(value = "page", required = false) Integer page) {
        if (page == null) {
            page = 0;
        }

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
    public ResponseEntity<MessageResponse> addCategory(@Valid @RequestBody Category category,
                                                @CurrentUser UserPrincipal currentUser) throws ResourceAlreadyExistException {
        categoryService.add(category);
        MessageResponse response = new MessageResponse(true, "Category successfully saved.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> updateCategory(@PathVariable(name = "id") Long id, @Valid @RequestBody Category category) {
        categoryService.update(id, category);
        MessageResponse response = new MessageResponse(true, "Category successfully updated.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> deleteCategory(@PathVariable(name = "id") Long id) throws UnauthorizedException, ResourceNotFoundException {
        categoryService.delete(id);
        MessageResponse response = new MessageResponse(true, "Category successfully deleted.");
        return ResponseEntity.ok(response);
    }
}
