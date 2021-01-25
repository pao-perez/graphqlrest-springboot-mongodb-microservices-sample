package com.paoperez.categoryservice;

import java.util.Collection;

public interface CategoryMapper {
    Collection<CategoryDTO> categoriesToCategoryDTOs(Collection<Category> categories);

    CategoryDTO categoryToCategoryDto(Category category);

    Category categoryDtoToCategory(CategoryDTO categoryDto);
}
