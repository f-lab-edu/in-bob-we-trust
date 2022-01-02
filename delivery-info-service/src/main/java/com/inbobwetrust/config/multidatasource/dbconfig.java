package com.inbobwetrust.config.multidatasource;

import com.mongodb.*;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@ConfigurationProperties(prefix = "spring.data.mongodb")
@Getter
@Setter
class CustomMongoProperties {
  private MongoProperties secondary;
  private MongoProperties primary;
}

@Configuration
@EnableReactiveMongoRepositories(
    basePackages = "com.inbobwetrust.repository.primary",
    reactiveMongoTemplateRef = "mongoTemplatePrimary")
class PrimaryMongoConfig {}

@Configuration
@EnableReactiveMongoRepositories(
    basePackages = "com.inbobwetrust.repository.secondary",
    reactiveMongoTemplateRef = "mongoTemplateSecondary")
class SecondaryMongoConfig {}

@Configuration
class MongoConfiguration {

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
