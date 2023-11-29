package edu.nuwm.NRDBlabs.api;

import edu.nuwm.NRDBlabs.persistence.entity.Subcategory ;
import edu.nuwm.NRDBlabs.persistence.entity.Category ;
import edu.nuwm.NRDBlabs.persistence.entity.Collection ;
import edu.nuwm.NRDBlabs.persistence.repository.CategoryRepository ;
import edu.nuwm.NRDBlabs.service.SearchService ;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final SearchService searchService;

    public CategoryController(CategoryRepository categoryRepository, SearchService searchService) {
        this.categoryRepository = categoryRepository;
        this.searchService = searchService;
    }

    @GetMapping
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/subcategories")
    public Set<Subcategory> getSubcategories() {
        return categoryRepository.findAll()
                .stream()
                .map(Category::getSubcategories)
                .flatMap(java.util.Collection::stream)
                .collect(Collectors.toSet());
    }

    @GetMapping("/collections")
    public Set<Collection> getCollections() {
        return categoryRepository.findAll()
                .stream()
                .map(Category::getSubcategories)
                .flatMap(java.util.Collection::stream)
                .map(Subcategory::getCollections)
                .flatMap(java.util.Collection::stream)
                .collect(Collectors.toSet());
    }

    @GetMapping("/search")
    public Category getCategory(@RequestParam final String name) {
        return categoryRepository.findByName(name);
    }

    @GetMapping("/search-collection")
    public List<Category> getCategoriesByCollection(@RequestParam final String collectionName) {
        return searchService.findCategoryByCollection(collectionName);
    }
}
