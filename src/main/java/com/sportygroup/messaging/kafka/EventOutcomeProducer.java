package com.sportygroup.messaging.kafka;

import com.sportygroup.domain.event.EventOutcome;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.sportygroup.util.Constants.OUTCOME_TOPIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventOutcomeProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOutcome(final EventOutcome outcome) {
        log.info("Publishing Event Outcome: {}. Winner: {}", outcome.eventName(), outcome.winnerId());
        // Event ID is the key to ensure ordering (bets for same event go to same partition)
        kafkaTemplate.send(OUTCOME_TOPIC, outcome.eventId(), outcome);
    }
}
