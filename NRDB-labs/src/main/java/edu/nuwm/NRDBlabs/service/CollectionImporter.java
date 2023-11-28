package edu.nuwm.NRDBlabs.service;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import edu.nuwm.NRDBlabs.persistence.entity.Collection;
import edu.nuwm.NRDBlabs.persistence.entity.Subcategory;
import edu.nuwm.NRDBlabs.persistence.entity.Category;
import edu.nuwm.NRDBlabs.persistence.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@DependsOn("subcategoryImporter")
public class CollectionImporter {

    private static final String SPECIALITIES_CSV_FILE_PATH = "import/Collection.csv";

    private final MongoTemplate mongoTemplate;
    private final CategoryRepository categoryRepository;

    public CollectionImporter(CategoryRepository categoryRepository, MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.categoryRepository = categoryRepository;
    }

    @PostConstruct
    public void importData() throws IOException, URISyntaxException {
        final Path filePath = Paths.get(ClassLoader.getSystemResource(SPECIALITIES_CSV_FILE_PATH).toURI());
        try (Reader reader = Files.newBufferedReader(filePath)) {
            try (CSVReader csvReader = getCsvReader(reader)) {
                final List<String[]> rows = csvReader.readAll();
                importSpecialitiesFromRows(rows);
            }
        }
    }

    private CSVReader getCsvReader(final Reader reader) {
        final CSVParser parser = getParser();
        return new CSVReaderBuilder(reader)
                .withSkipLines(1)
                .withCSVParser(parser)
                .build();
    }

    private CSVParser getParser() {
        return new CSVParserBuilder()
                .withSeparator(';')
                .withIgnoreQuotations(true)
                .build();
    }

    private void importSpecialitiesFromRows(final List<String[]> rows) {
        final Map<String, List<SpecialityImportDto>> subcategorysByCategory = rows
                .stream()
                .map(row -> new SpecialityImportDto(row[0], row[1], row[2]))
                .collect(Collectors.groupingBy(dto -> dto.subcategory));
        subcategorysByCategory.forEach(this::addSpecialitiesToSubcategories);
    }

    private void addSpecialitiesToSubcategories(final String subcategoryName, List<SpecialityImportDto> collectionImportDtos) {
        final Category category = findCategoryBySubcategory(subcategoryName);
        if (category == null) {
            return;
        }

        final Optional<Subcategory> subcategoryOptional = category.getSubcategories().stream()
                .filter(subcategory -> subcategory.getName().equals(subcategoryName))
                .findFirst();
        if (subcategoryOptional.isEmpty()) {
            return;
        }

        addSpecialitiesToSubcategory(subcategoryOptional.get(), collectionImportDtos);
        categoryRepository.save(category);
    }

    private Category findCategoryBySubcategory(final String subcategory) {
        final Query query = Query.query(Criteria.where("subcategories.name").is(subcategory));
        return mongoTemplate.findOne(query, Category.class);
    }

    private void addSpecialitiesToSubcategory(final Subcategory subcategory, final List<SpecialityImportDto> collectionImportDtos) {
        final List<edu.nuwm.NRDBlabs.persistence.entity.Collection> collections = collectionImportDtos
                .stream()
                .map(collection -> new Collection(collection.code, collection.name))
                .toList();
        subcategory.getCollections().addAll(collections);
    }

    private static class SpecialityImportDto {
        String code;
        String name;
        String subcategory;

        public SpecialityImportDto(String code, String name, String subcategory) {
            this.code = code;
            this.name = name;
            this.subcategory = subcategory;
        }
    }
}