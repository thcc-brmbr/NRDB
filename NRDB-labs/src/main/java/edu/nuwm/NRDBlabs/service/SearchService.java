package edu.nuwm.NRDBlabs.service;

import edu.nuwm.NRDBlabs.persistence.entity.Category;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    private final MongoTemplate mongoTemplate;

    public SearchService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Category> findCategoryByCollection(final String collectionName) {
        final Criteria criteria = Criteria.where("subcategories.collections.name")
                .regex(String.format(".*%s.*", collectionName), "i");
        final Query query = Query.query(criteria);
        return mongoTemplate.find(query, Category.class);
    }
}