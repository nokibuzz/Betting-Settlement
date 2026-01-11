package com.sportygroup;

import com.sportygroup.domain.event.BetSettlementCommand;
import com.sportygroup.domain.event.EventOutcome;
import com.sportygroup.domain.model.Bet;
import com.sportygroup.domain.model.BetStatus;
import com.sportygroup.repository.BetRepository;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.messaging.Message;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class BetSettlementIntegrationTest {

    // 1. Spin up Kafka inside Docker
    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BetRepository betRepository;

    @MockBean
    private RocketMQTemplate rocketMQTemplate;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @BeforeEach
    void setUp() {
        betRepository.deleteAll();
    }

    @Test
    void shouldProcessEventOutcomeAndTriggerSettlement() {
        // GIVEN: A pending bet exists in the DB
        final String eventId = "TEST_EVENT_001";
        final Bet pendingBet = Bet.builder()
                .userId("user_1")
                .eventId(eventId)
                .eventMarketId("WINNER")
                .predictedWinnerId("TEAM_X")
                .betAmount(new BigDecimal("100.00"))
                .status(BetStatus.PENDING)
                .build();
        betRepository.save(pendingBet);

        // AND: An event outcome payload (Team X won)
        final EventOutcome outcome = new EventOutcome(eventId, "Test Match", "TEAM_X");

        // WHEN: We call the REST API
        restTemplate.postForEntity("/api/v1/event/outcome", outcome, String.class);

        // THEN:
        // 1. Wait for Kafka to consume message and process it (Async)
        final ArgumentCaptor<Message<BetSettlementCommand>> messageCaptor = ArgumentCaptor.forClass(Message.class);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            // Verify RocketMQ producer was called
            verify(rocketMQTemplate).send(eq("bet-settlements"), messageCaptor.capture());
        });

        // 2. Validate the message sent to RocketMQ
        BetSettlementCommand command = messageCaptor.getValue().getPayload();
        assertThat(command.betId()).isEqualTo(pendingBet.getBetId());
        assertThat(command.computedResult().name()).isEqualTo("WIN");
    }
}
