package com.inbobwetrust.config.multidatasource;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ConfigurationProperties(prefix = "spring.data.mongodb")
@Profile({"dev", "production"})
@Getter
@Setter
public class CustomMongoProperties {
  private MongoProperties secondary;
  private MongoProperties primary;
}
