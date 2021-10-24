package com.paoperez.categoryservice;

import java.net.URI;
import java.util.Collection;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Validated
@RestController
@RequestMapping("/categories")
public class CategoryController {
  private final CategoryService categoryService;
  private final CategoryMapper categoryMapper;

  public CategoryController(final CategoryService categoryService,
      final CategoryMapper categoryMapper) {
    this.categoryService = categoryService;
    this.categoryMapper = categoryMapper;
  }

  @GetMapping()
  public ResponseEntity<CategoriesDTO> getAllCategories() {
    Collection<CategoryDTO> categories =
        this.categoryMapper.categoriesToCategoryDTOs(categoryService.getAllCategories());
    CategoriesDTO categoriesDTO = CategoriesDTO.builder().data(categories).build();
    return new ResponseEntity<>(categoriesDTO, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoryDTO> getCategory(final @PathVariable @NotBlank String id)
      throws CategoryNotFoundException {
    CategoryDTO categoryDTO = categoryMapper.categoryToCategoryDto(categoryService.getCategory(id));
    return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
  }

  @PostMapping()
  public ResponseEntity<String> createCategory(
      final @RequestBody @Valid CategoryDTO categoryRequest) throws CategoryAlreadyExistsException {
    Category category = categoryMapper.categoryDtoToCategory(categoryRequest);
    String id = categoryService.createCategory(category);
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(location);

    return new ResponseEntity<>(id, headers, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> updateCategory(final @PathVariable @NotBlank String id,
      final @RequestBody @Valid CategoryDTO categoryRequest)
      throws CategoryNotFoundException, CategoryAlreadyExistsException, CategoryMismatchException {
    Category category = categoryMapper.categoryDtoToCategory(categoryRequest);
    categoryService.updateCategory(id, category);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(final @PathVariable @NotBlank String id)
      throws CategoryNotFoundException {
    categoryService.deleteCategory(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
