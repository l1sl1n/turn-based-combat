package com.combat.demo;

import com.combat.model.*;
import java.util.List;

public class Demo {

    private static List<Unit> createUnits() {
        return List.of(
                new MeleeFighter("Conan", Team.PLAYER),
                new MeleeFighter("Brutus", Team.PLAYER),
                new RangedFighter("Legolas", Team.PLAYER),
                new RangedFighter("Robin", Team.PLAYER),
                new Mage("Merlin", Team.PLAYER),
                new Healer("Asclepius", Team.PLAYER),

                new MeleeFighter("Grom", Team.ENEMY),
                new MeleeFighter("Thrall", Team.ENEMY),
                new RangedFighter("Sylvanas", Team.ENEMY),
                new Mage("Gul'dan", Team.ENEMY),
                new Mage("Jaina", Team.ENEMY),
                new Healer("Uther", Team.ENEMY)
        );
    }

    public static void main(String[] args) {
        var units = createUnits();

        System.out.println("=== Units in this battle ===");
        units.forEach(u -> System.out.println("  " + u
                + " (initiative " + u.getInitiative() + ")"));

        System.out.println("\n=== Running combat ===");
        var engine = new CombatEngine(units);
        var result = engine.run();
        result.events().forEach(e ->
                System.out.println("[Turn " + e.turnNumber() + "] " + e.text()));

        System.out.println("\n=== Outcome ===");
        result.winner().ifPresentOrElse(
                w -> System.out.println("Winner: " + w),
                () -> System.out.println("Draw"));
        System.out.println("Total events: " + result.events().size());
        System.out.println("Total decisions: " + result.decisions().size());

        // Демонстрация проверяемого исключения
        System.out.println("\n=== Exception demo: heal a dead unit ===");
        try {
            var victim = new MeleeFighter("Victim", Team.ENEMY);
            victim.takeDirectDamage(99999, new CombatLog()); // убиваем
            var healer = new Healer("Healer", Team.PLAYER);
            healer.getAbility().perform(healer, victim, new CombatLog());
        } catch (DeadUnitException e) {
            System.out.println("Caught DeadUnitException: " + e.getMessage());
        } catch (CombatException e) {
            System.out.println("Caught CombatException: " + e.getMessage());
        }

        // Демонстрация equals/hashCode на record
        System.out.println("\n=== equals/hashCode on records ===");
        var ev1 = new CombatEvent(1, "test");
        var ev2 = new CombatEvent(1, "test");
        var ev3 = new CombatEvent(2, "other");
        System.out.println("ev1.equals(ev2) = " + ev1.equals(ev2));
        System.out.println("ev1.equals(ev3) = " + ev1.equals(ev3));
        System.out.println("ev1.hashCode()==ev2.hashCode() = "
                + (ev1.hashCode() == ev2.hashCode()));

        // Повышенный уровень: воспроизведение боя
        System.out.println("\n=== Replay (same decisions, same result) ===");
        var engine2 = new CombatEngine(createUnits());
        var replay = engine2.replay(result.decisions());
        System.out.println("Replay outcome: " + replay.winner());
        System.out.println("Decisions match: "
                + replay.decisions().equals(result.decisions()));
    }
}