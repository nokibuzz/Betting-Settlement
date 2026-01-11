package com.sportygroup.messaging.rocketmq;

import com.sportygroup.domain.event.BetSettlementCommand;
import com.sportygroup.domain.model.Bet;
import com.sportygroup.domain.model.BetStatus;
import com.sportygroup.repository.BetRepository;
import com.sportygroup.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@RocketMQMessageListener(topic = Constants.BET_TOPIC, consumerGroup = Constants.BET_GROUP_TOPIC)
public class BetSettlementConsumer implements RocketMQListener<BetSettlementCommand> {

    private final BetRepository betRepository;

    @Override
    @Transactional
    public void onMessage(final BetSettlementCommand command) {
        log.info("Processing Settlement for Bet ID: {}", command.betId());

        betRepository.findById(command.betId()).ifPresentOrElse(bet -> settleBet(bet, command),
                () -> log.error("Bet not found for ID: {}", command.betId()));
    }

    private void settleBet(final Bet bet, final BetSettlementCommand command) {
        if (bet.getStatus() == BetStatus.SETTLED) {
            log.warn("Bet {} is already settled. Skipping.", bet.getBetId());
            return;
        }

        bet.setResult(command.computedResult());
        bet.setStatus(BetStatus.SETTLED);

        betRepository.save(bet);

        log.info("Bet {} settled as {}. Amount: {}", bet.getBetId(), command.computedResult(), bet.getBetAmount());
    }
}
