package com.inbobwetrust;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;

import javax.annotation.PostConstruct;

@SpringBootApplication
@Slf4j
public class Application {

  public static void main(String[] args) {
    BlockHound.install();
    SpringApplication.run(Application.class, args);
  }


  @PostConstruct
  public void init() {
    log.info("CPU: {}", Runtime.getRuntime().availableProcessors());
  }
}
