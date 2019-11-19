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
class CategoryController {
    private final CategoryService categoryService;

    CategoryController(final CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping()
    ResponseEntity<Collection<Category>> getAllCategories() {
        return new ResponseEntity<>(categoryService.getAllCategories(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<Category> getCategory(final @PathVariable @NotBlank String id) throws CategoryNotFoundException {
        return new ResponseEntity<>(categoryService.getCategory(id), HttpStatus.OK);
    }

    @PostMapping()
    ResponseEntity<Category> createCategory(final @RequestBody @Valid Category category)
            throws CategoryAlreadyExistsException {
        Category createdCategory = categoryService.createCategory(category);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdCategory.getId()).toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);

        return new ResponseEntity<>(createdCategory, headers, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> updateCategory(final @PathVariable @NotBlank String id,
            final @RequestBody @Valid Category category)
            throws CategoryNotFoundException, CategoryAlreadyExistsException {
        categoryService.updateCategory(id, category);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteCategory(final @PathVariable @NotBlank String id) throws CategoryNotFoundException {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
