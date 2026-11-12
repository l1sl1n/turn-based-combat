package com.combat.model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Итоговый результат боя, содержащий журнал событий, историю решений
 * и информацию о победителе.
 */
public record CombatResult(List<CombatEvent> events,
                           List<CombatDecision> decisions,
                           Optional<Team> winner) {
    public CombatResult {
        Objects.requireNonNull(events);
        Objects.requireNonNull(decisions);
        Objects.requireNonNull(winner);
        events = List.copyOf(events);
        decisions = List.copyOf(decisions);
    }
}