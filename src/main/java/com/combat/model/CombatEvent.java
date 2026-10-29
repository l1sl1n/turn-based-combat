package com.combat.model;

import java.util.Objects;

/** Одна запись в журнале боя. Record автоматически даёт equals/hashCode. */
public record CombatEvent(int turnNumber, String text) {
    public CombatEvent {
        if (turnNumber <= 0) throw new IllegalArgumentException();
        Objects.requireNonNull(text);
    }
}