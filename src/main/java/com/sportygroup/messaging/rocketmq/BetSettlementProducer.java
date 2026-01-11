package com.sportygroup.messaging.rocketmq;

import com.sportygroup.domain.event.BetSettlementCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import static com.sportygroup.util.Constants.BET_TOPIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class BetSettlementProducer {

    private final RocketMQTemplate rocketMQTemplate;

    public void sendSettlementCommand(final BetSettlementCommand command) {
        log.info("Sending settlement command for Bet ID: {}", command.betId());
        rocketMQTemplate.send(BET_TOPIC, MessageBuilder.withPayload(command).build());
    }
}