package edu.nuwm.NRDBlabs.service;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import edu.nuwm.NRDBlabs.persistence.entity.Category;
import edu.nuwm.NRDBlabs.persistence.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
@Service
public class CategoryImporter {
    private static final String CATEGORY_CSV_FILE_PATH = "import/Category.csv";

    private final CategoryRepository categoryRepository;

    public CategoryImporter(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @PostConstruct
    public void importData() throws IOException, URISyntaxException {
        final Path filePath = Paths.get(ClassLoader.getSystemResource(CATEGORY_CSV_FILE_PATH).toURI());
        System.out.println(filePath.toString());
        try (Reader reader = Files.newBufferedReader(filePath)) {
            try (CSVReader csvReader = getCsvReader(reader)) {
                final List<String[]> rows = csvReader.readAll();
                System.out.println(csvReader.readAll());
                importInstitutesFromRows(rows);
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

    private void importInstitutesFromRows(final List<String[]> rows) {
        final List<Category> categories = rows.stream()
                .map(row -> row[0])
                .filter(categoryName -> !categoryExists(categoryName))
                .map(Category::new)
                .toList();
        categoryRepository.saveAll(categories);
    }

    private boolean categoryExists(final String categoryName) {
        return categoryRepository.findByName(categoryName) != null;
    }
}
