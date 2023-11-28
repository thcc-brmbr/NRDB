package edu.nuwm.NRDBlabs.persistence.repository;

import edu.nuwm.NRDBlabs.persistence.entity.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface CategoryRepository extends MongoRepository<Category, String> {
    Category findByName(String name);
}