package com.inbobwetrust.database;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class MongodbContainerInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  public static GenericContainer<?> mongodb =
      new GenericContainer<>(DockerImageName.parse("mongo:latest"));

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    mongodb.start();
    TestPropertyValues.of(
            "spring.data.mongodb.port=" + mongodb.getMappedPort(27017),
            "spring.data.mongodb.host=" + mongodb.getContainerIpAddress())
        .applyTo(applicationContext);
  }
}
