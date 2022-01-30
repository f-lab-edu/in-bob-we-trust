package com.inbobwetrust.config;

import io.netty.channel.nio.NioEventLoopGroup;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class NettyCustomizer {

  @Bean
  public NioEventLoopGroup nioEventLoopGroup() {
    return new NioEventLoopGroup(1);
  }

  @Bean
  public NettyReactiveWebServerFactory factory(NioEventLoopGroup eventLoopGroup) {
    NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory();
    factory.setServerCustomizers(
        Collections.singletonList(httpServer -> httpServer.runOn(eventLoopGroup)));
    return factory;
  }
}
