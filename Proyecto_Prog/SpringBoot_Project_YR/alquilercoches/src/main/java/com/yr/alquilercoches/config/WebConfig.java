package com.yr.alquilercoches.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ruta para acceder a las imagenes
        registry.addResourceHandler("/uploads/**")
        // Ruta donde se encuentran las imagenes
                .addResourceLocations("file:src/main/resources/static/uploads/");
    }
}