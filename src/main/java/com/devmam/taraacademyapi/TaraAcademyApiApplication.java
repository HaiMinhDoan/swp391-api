package com.devmam.taraacademyapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TaraAcademyApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaraAcademyApiApplication.class, args);
    }

}
