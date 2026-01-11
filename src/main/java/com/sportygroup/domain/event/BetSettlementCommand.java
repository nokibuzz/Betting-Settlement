package com.sportygroup.domain.event;

import com.sportygroup.domain.model.BetResult;

import java.io.Serializable;

public record BetSettlementCommand(
        String betId,
        BetResult computedResult
) implements Serializable {}
