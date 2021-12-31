package com.inbobwetrust.config.multidatasource;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(
  basePackages = "com.inbobwetrust.repository.secondary",
  reactiveMongoTemplateRef = "mongoTemplateSecondary")
public class SecondaryMongoConfig {
}
