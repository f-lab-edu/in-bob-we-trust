package com.inbobwetrust.config.multidatasource;

import com.mongodb.*;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

@Configuration
@Profile({"dev", "production"})
public class MongoConfiguration {

  private final CustomMongoProperties customMongoProperties;

  public MongoConfiguration(CustomMongoProperties customMongoProperties) {
    this.customMongoProperties = customMongoProperties;
  }

  @Primary
  @Bean
  public MongoClient reactiveMongoClientSecondary() {
    return MongoClients.create(createMongoClientSettings(customMongoProperties.getSecondary()));
  }

  @Bean
  public MongoClient reactiveMongoClientPrimary() {
    return MongoClients.create(createMongoClientSettings(customMongoProperties.getPrimary()));
  }

  @Primary
  @Bean("mongoTemplateSecondary")
  public ReactiveMongoTemplate reactiveMongoTemplateSecondary() {
    return new ReactiveMongoTemplate(
        reactiveMongoClientSecondary(), customMongoProperties.getSecondary().getDatabase());
  }

  @Bean("mongoTemplatePrimary")
  public ReactiveMongoTemplate reactiveMongoTemplatePrimary() {
    return new ReactiveMongoTemplate(
        reactiveMongoClientPrimary(), customMongoProperties.getPrimary().getDatabase());
  }

  private MongoClientSettings createMongoClientSettings(MongoProperties mongoProperties) {
    ConnectionString ConnectionString = new ConnectionString(mongoProperties.getUri());

    MongoClientSettings mongoClientSettings =
        MongoClientSettings.builder()
            .readConcern(ReadConcern.DEFAULT)
            .writeConcern(WriteConcern.MAJORITY)
            .readPreference(ReadPreference.primary())
            .applyConnectionString(ConnectionString)
            .build();
    return mongoClientSettings;
  }
}
