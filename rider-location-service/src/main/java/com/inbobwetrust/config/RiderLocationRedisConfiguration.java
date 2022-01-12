package com.inbobwetrust.config;

import com.inbobwetrust.domain.RiderLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RiderLocationRedisConfiguration {

  @Autowired ReactiveRedisConnectionFactory factory;

  @Bean
  public ReactiveRedisTemplate<String, RiderLocation> reactiveRedisTemplate(
      ReactiveRedisConnectionFactory factory) {
    Jackson2JsonRedisSerializer<RiderLocation> serializer =
        new Jackson2JsonRedisSerializer<>(RiderLocation.class);

    RedisSerializationContext.RedisSerializationContextBuilder<String, RiderLocation> builder =
        RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

    RedisSerializationContext<String, RiderLocation> context = builder.value(serializer).build();
    return new ReactiveRedisTemplate<>(factory, context);
  }
}
