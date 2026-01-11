package com.sportygroup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BetSettlementApplication {

    public static void main(String[] args) {
        SpringApplication.run(BetSettlementApplication.class, args);
    }
}
