package com.feelmycode.parabole.repository;

import com.feelmycode.parabole.domain.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {

    EventParticipant findByUserIdAndEventId(Long userId, Long eventId);
    List<EventParticipant> findAllByEventId(Long eventId);
}
