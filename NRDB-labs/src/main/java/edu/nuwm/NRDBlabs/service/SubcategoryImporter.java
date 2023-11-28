package edu.nuwm.NRDBlabs.service;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import edu.nuwm.NRDBlabs.persistence.entity.Subcategory;
import edu.nuwm.NRDBlabs.persistence.entity.Category;
import edu.nuwm.NRDBlabs.persistence.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
@DependsOn("categoryImporter")
public class SubcategoryImporter {

    private static final String CHAIRS_CSV_FILE_PATH = "import/Subcategory.csv";

    private final CategoryRepository categoryRepository;

    public SubcategoryImporter(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @PostConstruct
    public void importData() throws IOException, URISyntaxException {
        final Path filePath = Paths.get(ClassLoader.getSystemResource(CHAIRS_CSV_FILE_PATH).toURI());
        try (Reader reader = Files.newBufferedReader(filePath)) {
            try (CSVReader csvReader = getCsvReader(reader)) {
                final List<String[]> rows = csvReader.readAll();
                importSubcategoriesFromRows(rows);
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

    private void importSubcategoriesFromRows(final List<String[]> rows) {
        final Map<String, List<SubcategoryImportDto>> chairsByInstitute = rows
                .stream()
                .map(row -> new SubcategoryImportDto(row[0], row[1], row[2], row[3]))
                .collect(Collectors.groupingBy(dto -> dto.category));
        chairsByInstitute.forEach(this::addSubcategoriesToInstitute);
    }

    private void addSubcategoriesToInstitute(final String instituteName, final List<SubcategoryImportDto> chairImportDtos) {
        final Category category = categoryRepository.findByName(instituteName);
        if (category == null) {
            return;
        }

        final List<Subcategory> subcategories = chairImportDtos
                .stream()
                .map(subcategory -> new Subcategory(subcategory.name, subcategory.code, subcategory.access))
                .toList();

        category.getSubcategories().addAll(subcategories);
        categoryRepository.save(category);
    }

    private static class SubcategoryImportDto {
        String name;
        String code;
        String access;
        String category;

        public SubcategoryImportDto(String name, String code, String access, String category) {
            this.name = name;
            this.code = code;
            this.access = access;
            this.category = category;
        }
    }
}
