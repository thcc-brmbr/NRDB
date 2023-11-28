package edu.nuwm.NRDBlabs.configuration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfiguration {

    private final String host;
    private final String port;
    private final String database;

    public MongoConfiguration(@Value("${spring.data.mongodb.host}") String host,
                              @Value("${spring.data.mongodb.port}") String port,
                              @Value("${spring.data.mongodb.database}") String database) {
        this.host = host;
        this.port = port;
        this.database = database;
    }

    @Bean
    public MongoClient mongo() {
        final String connectionString = String.format("mongodb://%s:%s/%s", host, port, database);
        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongo(), database);
    }
}