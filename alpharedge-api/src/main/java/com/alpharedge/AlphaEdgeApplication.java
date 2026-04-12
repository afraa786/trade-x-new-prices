package com.alpharedge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AlphaEdgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlphaEdgeApplication.class, args);
    }
}
