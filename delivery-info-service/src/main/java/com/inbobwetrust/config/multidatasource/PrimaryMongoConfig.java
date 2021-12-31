package com.inbobwetrust.config.multidatasource;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(
  basePackages = "com.inbobwetrust.repository.primary",
  reactiveMongoTemplateRef = "mongoTemplatePrimary")
public class PrimaryMongoConfig {
}
