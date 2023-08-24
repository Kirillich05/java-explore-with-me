package ru.practicum.category.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repo;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto getCategory(long id) {
        var category = findOrThrow(id);
        return categoryMapper.fromModelToCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories(int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        return repo.findAll(page).stream()
                .map(categoryMapper::fromModelToCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryDto saveCategory(NewCategoryDto newCategoryDto) {
        if (repo.existsByName(newCategoryDto.getName())) {
            throw new ConflictException("Category is existed");
        }
        var category = categoryMapper.fromNewCategoryDtoToModel(newCategoryDto);
        var savedCategory = repo.save(category);
        return categoryMapper.fromModelToCategoryDto(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(long id, CategoryDto categoryDto) {
        Category category = findOrThrow(id);

        Optional<Category> namedCategory = repo.checkName(categoryDto.getName());
        if (category.getName().equals(categoryDto.getName())) {
            return categoryMapper.fromModelToCategoryDto(category);
        }
        if (namedCategory.isPresent()) {
            throw new ConflictException("Category is existed");
        }
        category.setName(categoryDto.getName());
        var updatedCategory = repo.save(category);
        return categoryMapper.fromModelToCategoryDto(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(long id) {
        findOrThrow(id);
        if (eventRepository.existsByCategoryId(id)) {
            throw new ConflictException("Category by " + id + " id links with events");
        }

        repo.deleteById(id);
    }

    private Category findOrThrow(long id) {
        return repo
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundException("Category by id " + id + " was not found")
                );
    }
}
