package com.sportygroup.domain.event;

import java.io.Serializable;

public record EventOutcome(
        String eventId,
        String eventName,
        String winnerId
) implements Serializable {}
