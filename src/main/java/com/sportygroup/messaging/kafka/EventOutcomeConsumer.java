package com.sportygroup.messaging.kafka;

import com.sportygroup.domain.event.BetSettlementCommand;
import com.sportygroup.domain.event.EventOutcome;
import com.sportygroup.domain.model.Bet;
import com.sportygroup.domain.model.BetResult;
import com.sportygroup.domain.model.BetStatus;
import com.sportygroup.messaging.rocketmq.BetSettlementProducer;
import com.sportygroup.repository.BetRepository;
import com.sportygroup.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventOutcomeConsumer {

    private final BetRepository betRepository;
    private final BetSettlementProducer rocketMqProducer;

    @KafkaListener(topics = Constants.OUTCOME_TOPIC, groupId = Constants.OUTCOME_GROUP_TOPIC)
    @Transactional(readOnly = true)
    public void handleEventOutcome(final EventOutcome eventOutcome) {
        log.info("Received BetOutcome for Event: {}", eventOutcome.eventId());
        final List<Bet> eligibleBets = betRepository.findByEventIdAndStatus(eventOutcome.eventId(), BetStatus.PENDING);

        log.debug("found {} bets to settle.", eligibleBets.size());
        eligibleBets.forEach(bet -> processBet(bet, eventOutcome));
    }

    private void processBet(final Bet bet, final EventOutcome outcome) {
        boolean isWin = bet.getPredictedWinnerId().equalsIgnoreCase(outcome.winnerId());

        final BetResult result = isWin ? BetResult.WIN : BetResult.LOSS;

        final BetSettlementCommand command = new BetSettlementCommand(bet.getBetId(), result);
        rocketMqProducer.sendSettlementCommand(command);
    }
}
