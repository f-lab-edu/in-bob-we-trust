package com.inbobwetrust.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

  final List<ResponseMessage> globalResponses = getGlobalResponses();

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .globalResponseMessage(RequestMethod.GET, globalResponses)
        .globalResponseMessage(RequestMethod.POST, globalResponses)
        .globalResponseMessage(RequestMethod.PUT, globalResponses)
        .globalResponseMessage(RequestMethod.DELETE, globalResponses)
        .globalResponseMessage(RequestMethod.PATCH, globalResponses)
        .consumes(this.consumesContentTypes())
        .produces(this.producesContentTypes())
        .select()
        .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
        .paths(PathSelectors.any())
        .build();
  }

  private Set<String> consumesContentTypes() {
    Set<String> consumes = new HashSet<>();
    consumes.add(MediaType.APPLICATION_JSON_VALUE);
    return consumes;
  }

  private Set<String> producesContentTypes() {
    Set<String> produces = new HashSet<>();
    produces.add(MediaType.APPLICATION_JSON_VALUE);
    return produces;
  }

  private List<ResponseMessage> getGlobalResponses() {
    return Arrays.asList(
        new ResponseMessageBuilder().code(400).message("Bad Request").build(),
        new ResponseMessageBuilder().code(500).message("Server Error").build());
  }
}
