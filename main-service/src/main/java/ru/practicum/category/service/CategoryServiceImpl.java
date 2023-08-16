package ru.practicum.category.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repo;
    private final ModelMapper mapper;

    @Override
    public CategoryDto getCategory(long id) {
        var category = findOrThrow(id);
        return convertToDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories(int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        return repo.findAll(page).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryDto saveCategory(NewCategoryDto newCategoryDto) {
        var category = convertNewCategoryDtoToModel(newCategoryDto);
        var savedCategory = repo.save(category);
        return convertToDto(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(long id, CategoryDto categoryDto) {
        var category = findOrThrow(id);

//        if (categoryDto.getName().equals(category.getName())) {
//            throw new ConflictException("Category is existed");
//        }

        category.setName(categoryDto.getName());
        var updatedCategory = repo.save(category);
        return convertToDto(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(long id) {
        findOrThrow(id);
        repo.deleteById(id);
    }

    private CategoryDto convertToDto(Category category) {
        return mapper.map(category, CategoryDto.class);
    }

    private Category convertCategoryDtoToModel(CategoryDto dto) {
        return mapper.map(dto, Category.class);
    }

    private Category convertNewCategoryDtoToModel(NewCategoryDto dto) {
        return mapper.map(dto, Category.class);
    }

    private Category findOrThrow(long id) {
        return repo
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundException("Category by id " + id + " was not found")
                );
    }
}
