package edu.nuwm.NRDBlabs.persistence.repository;

import edu.nuwm.NRDBlabs.persistence.entity.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ItemRepository extends MongoRepository<Item, String> {
}