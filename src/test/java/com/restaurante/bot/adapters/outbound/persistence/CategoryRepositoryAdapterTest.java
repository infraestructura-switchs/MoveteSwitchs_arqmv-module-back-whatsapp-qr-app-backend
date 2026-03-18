package com.restaurante.bot.adapters.outbound.persistence;

import com.restaurante.bot.model.Category;
import com.restaurante.bot.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryRepositoryAdapterTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryRepositoryAdapter adapter;

    @Test
    void findAll_delegates() {
        Category c1 = new Category();
        Category c2 = new Category();
        List<Category> list = Arrays.asList(c1, c2);

        when(categoryRepository.findAll()).thenReturn(list);

        List<Category> result = adapter.findAll();

        assertEquals(2, result.size());
        verify(categoryRepository).findAll();
    }

    @Test
    void findById_and_save_and_delete() {
        Category c = new Category();

        when(categoryRepository.findById(5L)).thenReturn(Optional.of(c));
        when(categoryRepository.save(c)).thenReturn(c);

        Optional<Category> found = adapter.findById(5L);
        assertTrue(found.isPresent());

        Category saved = adapter.save(c);
        assertEquals(c, saved);

        adapter.deleteById(5L);
        verify(categoryRepository).deleteById(5L);
    }

    @Test
    void existsByIdAndName_delegates() {
        when(categoryRepository.existsByNameAndCompanyId("X", 1L)).thenReturn(true);

        boolean exists = adapter.existsByNameAndCompanyId("X", 1L);

        assertTrue(exists);
        verify(categoryRepository).existsByNameAndCompanyId("X", 1L);
    }
}
