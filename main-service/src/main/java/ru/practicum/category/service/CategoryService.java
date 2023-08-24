package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto getCategory(long id);

    List<CategoryDto> getAllCategories(int from, int size);

    CategoryDto saveCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(long id, CategoryDto categoryDto);

    void deleteCategory(long id);
}
