package com.paoperez.categoryservice;

import java.util.Collection;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;

public class CategoryMapperImpl implements CategoryMapper {
    private final ModelMapper mapper;

    CategoryMapperImpl(final ModelMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Category categoryDtoToCategory(CategoryDTO categoryDto) {
        return mapper.map(categoryDto, Category.class);
    }

    @Override
    public CategoryDTO categoryToCategoryDto(Category category) {
        return mapper.map(category, CategoryDTO.class);
    }

    @Override
    public Collection<CategoryDTO> categoriesToCategoryDTOs(Collection<Category> categories) {
        return mapCollection(categories, CategoryDTO.class);
    }

    private <S, T> Collection<T> mapCollection(Collection<S> source, Class<T> targetClass) {
        return source.stream().map(element -> mapper.map(element, targetClass))
                .collect(Collectors.toList());
    }
}
