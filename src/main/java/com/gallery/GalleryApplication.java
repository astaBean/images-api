package com.gallery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SpringBootApplication
public class GalleryApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(GalleryApplication.class, args);
    }

    @ExceptionHandler
    void handleIllegalArgumentException(IllegalArgumentException exception, HttpServletResponse response) throws IOException {
        logger.error(String.format("Exception happened with message [%s]", exception.getMessage()), exception);
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(GalleryApplication.class);
    }

}
