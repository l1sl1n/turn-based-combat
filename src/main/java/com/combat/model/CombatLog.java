package com.combat.model;

import java.util.ArrayList;
import java.util.List;

/** Журнал боя. Инкапсулирует изменяемый список событий. */
public class CombatLog {
    private final List<CombatEvent> events = new ArrayList<>();
    private int turnNumber = 1;

    public void setTurnNumber(int n) { turnNumber = n; }

    /** Неизменяемая копия событий. */
    public List<CombatEvent> getEvents() { return List.copyOf(events); }

    public void print() {
        for (CombatEvent e : events) {
            System.out.println("[Turn " + e.turnNumber() + "] " + e.text());
        }
    }

    void turnStart(Unit u) {
        add("Turn starts for " + u.getName() + " (" + u.getHealth() + " HP)");
    }
    void abilityUsed(Unit caster, Ability ability, Unit target) {
        add(caster.getName() + " uses " + ability.getName() + " on " + target.getName());
    }
    void damageTaken(Unit u, int amount, DamageType type, double mult) {
        String note = mult != 1.0 ? String.format(" (x%.2f)", mult) : "";
        add("  " + u.getName() + " takes " + amount + " " + type
                + " damage" + note + ", now " + u.getHealth() + " HP");
    }
    void healed(Unit u, int amount) {
        add("  " + u.getName() + " healed for " + amount
                + ", now " + u.getHealth() + " HP");
    }
    void effectApplied(Unit u, Effect e) {
        add("  " + e.getName() + " applied to " + u.getName()
                + " for " + e.getRemainingTurns() + " turns");
    }
    void effectStacked(Unit u, Effect e) {
        add("  " + e.getName() + " stacks on " + u.getName());
    }
    void effectRefreshed(Unit u, Effect old, Effect nu) {
        add("  " + old.getName() + " refreshed on " + u.getName()
                + " to " + nu.getRemainingTurns() + " turns");
    }
    void effectExtended(Unit u, Effect e) {
        add("  " + e.getName() + " on " + u.getName()
                + " extended to " + e.getRemainingTurns() + " turns");
    }
    void effectIgnored(Unit u, Effect e) {
        add("  " + e.getName() + " ignored on " + u.getName());
    }
    void effectTick(Unit u, Effect e) {
        add("  " + e.getName() + " ticks on " + u.getName());
    }
    void effectExpired(Unit u, Effect e) {
        add("  " + e.getName() + " expired on " + u.getName());
    }
    void stunned(Unit u) {
        add("  " + u.getName() + " is stunned and skips the turn");
    }
    void unitDied(Unit u) {
        add("  *** " + u.getName() + " died! ***");
    }
    void error(String message) { add("  ! " + message); }

    private void add(String text) { events.add(new CombatEvent(turnNumber, text)); }
}