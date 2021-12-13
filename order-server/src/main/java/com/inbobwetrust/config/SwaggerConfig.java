package com.inbobwetrust.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return globalConfigurationApplied(new Docket(DocumentationType.SWAGGER_2))
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    private Docket globalConfigurationApplied(Docket docket) {
        docket = applyGlobalResponses(docket);
        docket = applyGlobalParameter(docket);
        return docket;
    }

    private Docket applyGlobalParameter(Docket docket) {
        return docket.globalOperationParameters(
                List.of(
                        new ParameterBuilder()
                                .name("Content-Type")
                                .description("Content-Type")
                                .parameterType("header")
                                .required(false)
                                .modelRef(new ModelRef("string"))
                                .build()));
    }

    private Docket applyGlobalResponses(Docket docket) {
        final List<ResponseMessage> globalResponses = getGlobalResponses();
        return docket.globalResponseMessage(RequestMethod.GET, globalResponses)
                .globalResponseMessage(RequestMethod.POST, globalResponses)
                .globalResponseMessage(RequestMethod.PUT, globalResponses)
                .globalResponseMessage(RequestMethod.DELETE, globalResponses)
                .globalResponseMessage(RequestMethod.PATCH, globalResponses);
    }

    private List<ResponseMessage> getGlobalResponses() {
        return Arrays.asList(
                new ResponseMessageBuilder()
                        .code(200)
                        .message("OK")
                        .responseModel(new ModelRef("Success"))
                        .build(),
                new ResponseMessageBuilder()
                        .code(400)
                        .message("Bad Request")
                        .responseModel(new ModelRef("Client Error"))
                        .build(),
                new ResponseMessageBuilder()
                        .code(500)
                        .message("Internal Error")
                        .responseModel(new ModelRef("Error"))
                        .build());
    }
}
