package com.combat.model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Итог боя. Record — неизменяем. */
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