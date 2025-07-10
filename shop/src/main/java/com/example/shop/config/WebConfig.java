package com.example.shop.config;

import java.io.File;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @SuppressWarnings("null")
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/inicio", "/");
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/nosotros").setViewName("nosotros");
        registry.addViewController("/zapatillas").setViewName("zapatillas");
        registry.addViewController("/error404").setViewName("error404");
        registry.addViewController("/acceso-denegado").setViewName("acceso-denegado");
        registry.addViewController("/productos").setViewName("productos");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/register").setViewName("register");
        registry.addViewController("/contactanos").setViewName("contactanos");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/imagenes/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + File.separator + "shop"
                        + File.separator + "imagenes" + File.separator);
    }
}