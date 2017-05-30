package org.apidoclet.server.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class ApiDocletMVCConfigurer extends WebMvcConfigurerAdapter {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    CacheControl cacheControl = CacheControl.maxAge(5, TimeUnit.HOURS);
    registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/")
        .setCacheControl(cacheControl);
    registry.addResourceHandler("/books/**").addResourceLocations("classpath:/books/")
        .setCacheControl(cacheControl);
  }
  

}
