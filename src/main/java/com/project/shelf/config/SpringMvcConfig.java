package com.project.shelf.config;


import com.project.shelf.interceptor.LoginInterceptor;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    @Resource
    LoginInterceptor loginInterceptor;

    @Value("${image.upload-dir}")
    private String uploadDir;

    @Value("${image.access-path}")
    private String imgAccessPath;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/test/**",
                        "/api/redis/**",
                        "/api/user/login",
                        "/api/doc/list",
                        "/api/doc/content",
                        "/api/doc/vote",
                        "/api/ebook-snapshot/**",
                        imgAccessPath + "**"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configures a handler for accessing files under the given directory
        // The pattern "/images/upload/**" will map the URL to files located in the specified directory
        registry.addResourceHandler(imgAccessPath + "**")
                .addResourceLocations("file:///" + uploadDir);
    }
}
