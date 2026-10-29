package com.combat.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Управляет пошаговым боем. */
public class CombatEngine {
    private static final int MAX_TURNS = 1000;

    private final List<Unit> units;
    private final CombatLog log = new CombatLog();
    private final List<CombatDecision> decisions = new ArrayList<>();
    private int currentTurn = 0;

    public CombatEngine(List<Unit> units) {
        this.units = List.copyOf(units);
        if (this.units.isEmpty()) throw new IllegalArgumentException("no units");
    }

    /** Запуск боя с автоматическими решениями. */
    public CombatResult run() { return execute(null); }

    /** Воспроизведение боя по записанным решениям. */
    public CombatResult replay(List<CombatDecision> forced) {
        return execute(forced);
    }

    private CombatResult execute(List<CombatDecision> forced) {
        var order = new ArrayList<>(units);
        order.sort(Comparator.comparingInt(Unit::getInitiative).reversed());

        int decisionIndex = 0;

        while (bothTeamsAlive()) {
            if (currentTurn >= MAX_TURNS) {
                log.error("Max turns reached, ending in draw");
                return new CombatResult(log.getEvents(),
                        List.copyOf(decisions), Optional.empty());
            }
            for (Unit unit : order) {
                if (!unit.isAlive()) continue;
                if (!bothTeamsAlive()) break;

                currentTurn++;
                log.setTurnNumber(currentTurn);
                log.turnStart(unit);

                // 1. Проверяем оглушение ДО тикания длительностей.
                boolean stunned = unit.isStunned();
                // 2. Тикание эффектов (яд наносит урон, длительности -1).
                unit.tickEffects(log);
                // 3. Удаление истёкших.
                unit.removeExpiredEffects(log);

                if (!unit.isAlive()) continue;
                if (stunned) { log.stunned(unit); continue; }

                try {
                    Unit target;
                    Ability ability = unit.getAbility();
                    if (forced != null) {
                        CombatDecision d = forced.get(decisionIndex++);
                        target = findUnit(d.targetName());
                        if (!ability.getName().equals(d.abilityName())) {
                            log.error("Decision mismatch at turn " + currentTurn);
                        }
                    } else {
                        target = pickTarget(unit);
                    }
                    decisions.add(new CombatDecision(currentTurn, unit.getName(),
                            ability.getName(), target.getName()));
                    ability.perform(unit, target, log);
                } catch (DeadUnitException ex) {
                    log.error("Dead unit: " + ex.getMessage());
                } catch (CombatException ex) {
                    log.error("Invalid action: " + ex.getMessage());
                } catch (IndexOutOfBoundsException ex) {
                    log.error("No forced decision for turn " + currentTurn);
                }
            }
        }
        return new CombatResult(log.getEvents(),
                List.copyOf(decisions), determineWinner());
    }

    /** ИИ: лекарь лечит самого раненого союзника, остальные бьют врага с наименьшим HP. */
    private Unit pickTarget(Unit actor) {
        if (actor.getType() == UnitType.HEALER) {
            return units.stream()
                    .filter(u -> u.getTeam() == actor.getTeam() && u.isAlive())
                    .min(Comparator.comparingDouble(
                            u -> (double) u.getHealth() / u.getMaxHealth()))
                    .orElseThrow();
        }
        return units.stream()
                .filter(u -> u.getTeam() != actor.getTeam() && u.isAlive())
                .min(Comparator.comparingInt(Unit::getHealth))
                .orElseThrow();
    }

    private Unit findUnit(String name) {
        return units.stream().filter(u -> u.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown unit: " + name));
    }

    private boolean bothTeamsAlive() {
        boolean p = units.stream().anyMatch(u -> u.getTeam() == Team.PLAYER && u.isAlive());
        boolean e = units.stream().anyMatch(u -> u.getTeam() == Team.ENEMY && u.isAlive());
        return p && e;
    }

    private Optional<Team> determineWinner() {
        boolean p = units.stream().anyMatch(u -> u.getTeam() == Team.PLAYER && u.isAlive());
        boolean e = units.stream().anyMatch(u -> u.getTeam() == Team.ENEMY && u.isAlive());
        if (p && !e) return Optional.of(Team.PLAYER);
        if (e && !p) return Optional.of(Team.ENEMY);
        return Optional.empty();
    }
}