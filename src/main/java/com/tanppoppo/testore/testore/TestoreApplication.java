package com.tanppoppo.testore.testore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TestoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestoreApplication.class, args);
    }

}
