package com.cosmo.cats.service;

import com.cosmo.cats.api.domain.category.Category;
import com.cosmo.cats.persistence.entity.CategoryEntity;
import com.cosmo.cats.persistence.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToCategory)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(this::mapToCategory)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
    }

    @Transactional
    public Category createCategory(Category category) {
        CategoryEntity entity = new CategoryEntity();
        entity.setName(category.getName());
        return mapToCategory(categoryRepository.save(entity));
    }

    @Transactional
    public Category updateCategory(Long id, Category category) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
        entity.setName(category.getName());
        return mapToCategory(categoryRepository.save(entity));
    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    private Category mapToCategory(CategoryEntity entity) {
        return Category.builder()
                .id(String.valueOf(entity.getId()))
                .name(entity.getName())
                .build();
    }
}
