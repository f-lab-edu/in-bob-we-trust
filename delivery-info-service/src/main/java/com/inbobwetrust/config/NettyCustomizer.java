package com.inbobwetrust.config;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.HttpHandler;
import reactor.netty.http.server.HttpServer;
import reactor.netty.resources.LoopResources;
import reactor.netty.tcp.TcpServer;

import java.util.Collections;

@Configuration
@Slf4j
public class NettyCustomizer {

  @Bean
  public NettyReactiveWebServerFactory nettyReactiveWebServerFactory() {
    var webServerFactory = new NettyReactiveWebServerFactory();
    webServerFactory.addServerCustomizers(new EventLoopNettyCustomizer());
    return webServerFactory;
  }

  private static class EventLoopNettyCustomizer implements NettyServerCustomizer {
    @Override
    public HttpServer apply(HttpServer httpServer) {
      int nthreads = 1;
      log.info("configured........ {}", nthreads);
      EventLoopGroup eventLoopGroup = new NioEventLoopGroup(nthreads);
      eventLoopGroup.register(new NioServerSocketChannel());
      return httpServer.runOn(eventLoopGroup);
    }
  }
}
