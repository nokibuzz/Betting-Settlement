package com.sportygroup.config;

import com.sportygroup.domain.model.Bet;
import com.sportygroup.domain.model.BetStatus;
import com.sportygroup.repository.BetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@Slf4j
public class LocalDataIngestion {

    @Bean
    CommandLineRunner initDatabase(BetRepository repository) {
        return args -> {
            // Bet 1: User predicts Team A wins
            repository.save(Bet.builder()
                    .userId("user_1")
                    .eventId("EVT_123")
                    .eventMarketId("MATCH_WINNER")
                    .predictedWinnerId("TEAM_A")
                    .betAmount(new BigDecimal("50.00"))
                    .status(BetStatus.PENDING)
                    .build());

            // Bet 2: User predicts Team B wins
            repository.save(Bet.builder()
                    .userId("user_2")
                    .eventId("EVT_123")
                    .eventMarketId("MATCH_WINNER")
                    .predictedWinnerId("TEAM_B")
                    .betAmount(new BigDecimal("100.00"))
                    .status(BetStatus.PENDING)
                    .build());

            log.info("2 Bets created for Event EVT_123");
        };
    }
}
