package com.combat.model;

import java.util.Objects;

/** Записанное решение: какой юнит применил какую способность к какой цели. */
public record CombatDecision(int turnNumber, String actorName,
                             String abilityName, String targetName) {
    public CombatDecision {
        if (turnNumber <= 0) throw new IllegalArgumentException();
        Objects.requireNonNull(actorName);
        Objects.requireNonNull(abilityName);
        Objects.requireNonNull(targetName);
    }
}