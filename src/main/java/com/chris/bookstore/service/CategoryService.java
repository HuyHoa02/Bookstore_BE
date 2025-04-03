package com.chris.bookstore.service;

import com.chris.bookstore.dto.request.CategoryRequest;
import com.chris.bookstore.dto.response.CategoryResponse;
import com.chris.bookstore.entity.Category;
import com.chris.bookstore.enums.ErrorCode;
import com.chris.bookstore.exception.AppException;
import com.chris.bookstore.repository.BookRepository;
import com.chris.bookstore.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(
            BookRepository bookRepository,
            CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponse handleCreateCategory(CategoryRequest request) {
        Optional<Category> existingCategory = this.categoryRepository.findByName(request.getName());
        if (existingCategory.isPresent()) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        categoryRepository.save(category);

        return new CategoryResponse(category);
    }

    public List<CategoryResponse> getAllCategories(){
        return this.categoryRepository.findAll().stream().map(CategoryResponse::new).toList();
    }

    public CategoryResponse handleUpdateCategory(CategoryRequest request, Long id){
        Optional<Category> existingCategory = this.categoryRepository.findById(id);
        if (existingCategory.isEmpty()) {
            throw new AppException(ErrorCode.CATEGORY_NOT_EXISTED);
        }

        existingCategory.get().setName(request.getName());
        existingCategory.get().setDescription(request.getDescription());
        this.categoryRepository.save(existingCategory.get());

        return new CategoryResponse(existingCategory.get());
    }

    public void handleDeleteCategory(Long id){
        Optional<Category> existingCategory = this.categoryRepository.findById(id);
        if (existingCategory.isEmpty()) {
            throw new AppException(ErrorCode.CATEGORY_NOT_EXISTED);
        }
        this.categoryRepository.deleteById(id);
    }
}