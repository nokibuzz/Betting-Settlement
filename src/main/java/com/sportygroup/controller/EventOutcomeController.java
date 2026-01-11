package com.sportygroup.controller;

import com.sportygroup.domain.event.EventOutcome;
import com.sportygroup.messaging.kafka.EventOutcomeProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/event")
@RequiredArgsConstructor
public class EventOutcomeController {

    private final EventOutcomeProducer producer;

    @PostMapping("/outcome")
    public ResponseEntity<String> publishOutcome(@RequestBody EventOutcome outcome) {
        producer.publishOutcome(outcome);
        return ResponseEntity.accepted().body("Event outcome received and processing started.");
    }
}
