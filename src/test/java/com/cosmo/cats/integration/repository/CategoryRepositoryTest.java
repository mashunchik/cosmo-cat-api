package com.cosmo.cats.integration.repository;

import com.cosmo.cats.integration.BaseIntegrationTest;
import com.cosmo.cats.persistence.entity.CategoryEntity;
import com.cosmo.cats.persistence.repository.CategoryRepository;
import com.cosmo.cats.util.TestDataBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void shouldSaveCategory() {
        // given
        CategoryEntity category = TestDataBuilder.defaultCategory().build();

        // when
        CategoryEntity savedCategory = categoryRepository.save(category);

        // then
        assertThat(savedCategory.getId()).isNotNull();
        assertThat(savedCategory.getName()).isEqualTo(category.getName());
        assertThat(savedCategory.getCreatedAt()).isNotNull();
        assertThat(savedCategory.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldFindCategoryById() {
        // given
        CategoryEntity category = categoryRepository.save(TestDataBuilder.defaultCategory().build());

        // when
        Optional<CategoryEntity> found = categoryRepository.findById(category.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo(category.getName());
    }

    @Test
    void shouldUpdateCategory() {
        // given
        CategoryEntity category = categoryRepository.save(TestDataBuilder.defaultCategory().build());
        String newName = "Updated Category";
        category.setName(newName);

        // when
        CategoryEntity updated = categoryRepository.save(category);

        // then
        assertThat(updated.getName()).isEqualTo(newName);
        assertThat(updated.getId()).isEqualTo(category.getId());
    }

    @Test
    void shouldDeleteCategory() {
        // given
        CategoryEntity category = categoryRepository.save(TestDataBuilder.defaultCategory().build());

        // when
        categoryRepository.deleteById(category.getId());

        // then
        assertThat(categoryRepository.findById(category.getId())).isEmpty();
    }
}
