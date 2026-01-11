package com.sportygroup.repository;

import com.sportygroup.domain.model.Bet;
import com.sportygroup.domain.model.BetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BetRepository extends JpaRepository<Bet, String> {

    List<Bet> findByEventIdAndStatus(String eventId, BetStatus status);
}
