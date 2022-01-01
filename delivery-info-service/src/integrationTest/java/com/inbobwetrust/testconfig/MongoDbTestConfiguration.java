package com.inbobwetrust.testconfig;

import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class MongoDbTestConfiguration {

  private static final String IP = "localhost";
  private static final int PORT = 28017;

  @Primary
  @Bean
  public MongodConfig embeddedMongoConfiguration() throws IOException {
    return MongodConfig.builder()
        .version(Version.LATEST_NIGHTLY)
        .net(new Net(IP, PORT, Network.localhostIsIPv6()))
        .build();
  }
}
