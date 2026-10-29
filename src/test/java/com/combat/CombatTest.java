package com.combat;

import com.combat.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CombatTest {

    private CombatLog log;

    @BeforeEach
    void setUp() { log = new CombatLog(); }

    @Test
    void unitStartsWithFullHealth() {
        var u = new MeleeFighter("A", Team.PLAYER);
        assertEquals(u.getMaxHealth(), u.getHealth());
        assertTrue(u.isAlive());
    }

    @Test
    void zeroDurationEffectThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new PoisonEffect(5, 0));
    }

    @Test
    void negativeDamageThrows() {
        var u = new MeleeFighter("T", Team.ENEMY);
        assertThrows(IllegalArgumentException.class,
                () -> u.takeDamage(-1, DamageType.FIRE, log));
    }

    @Test
    void fireVulnerabilityOnMage() {
        var mage = new Mage("M", Team.ENEMY); // 70 HP, magicRes 6
        mage.takeDamage(10, DamageType.FIRE, log);
        // 10 * 1.5 (fire vs mage) = 15, минус 6 magicRes = 9 урона
        assertEquals(70 - 9, mage.getHealth());
    }

    @Test
    void holyResistanceOnMage() {
        var mage = new Mage("M", Team.ENEMY);
        mage.takeDamage(20, DamageType.HOLY, log);
        // 20 * 0.5 = 10, минус 6 = 4
        assertEquals(70 - 4, mage.getHealth());
    }

    @Test
    void defenseReducesPhysical() {
        var melee = new MeleeFighter("T", Team.ENEMY); // defense 8
        melee.takeDamage(20, DamageType.PHYSICAL, log);
        // 20 * 1.0 - 8 = 12
        assertEquals(120 - 12, melee.getHealth());
    }

    @Test
    void shieldAbsorbsDamage() {
        var u = new MeleeFighter("T", Team.ENEMY);
        u.applyEffect(new ShieldEffect(10, 3), log);
        u.takeDamage(20, DamageType.PHYSICAL, log);
        // 20 - 8 (defense) = 12, потом щит 10 → 2 урона
        assertEquals(120 - 2, u.getHealth());
    }

    @Test
    void poisonTicksAtTurnStart() {
        var u = new MeleeFighter("T", Team.ENEMY);
        u.applyEffect(new PoisonEffect(5, 2), log);
        u.tickEffects(log);
        assertEquals(120 - 5, u.getHealth());
        u.tickEffects(log);
        assertEquals(120 - 10, u.getHealth());
    }

    @Test
    void effectExpiresAfterDuration() {
        var u = new MeleeFighter("T", Team.ENEMY);
        u.applyEffect(new PoisonEffect(5, 1), log);
        u.tickEffects(log);
        u.removeExpiredEffects(log);
        assertTrue(u.getEffects().isEmpty());
    }

    @Test
    void poisonStacksOnReapply() {
        var u = new MeleeFighter("T", Team.ENEMY);
        u.applyEffect(new PoisonEffect(5, 3), log);
        u.applyEffect(new PoisonEffect(5, 3), log);
        assertEquals(2, u.getEffects().size()); // STACK_POWER
    }

    @Test
    void buffIncreasesEffectiveAttack() {
        var u = new MeleeFighter("T", Team.PLAYER);
        int before = u.getEffectiveAttack();
        u.applyEffect(new BuffEffect(10, 2), log);
        assertEquals(before + 10, u.getEffectiveAttack());
    }

    @Test
    void stunPreventsAction() {
        var u = new MeleeFighter("T", Team.PLAYER);
        u.applyEffect(new StunEffect(1), log);
        assertTrue(u.isStunned());
    }

    @Test
    void attackingDeadUnitThrows() {
        var caster = new MeleeFighter("C", Team.PLAYER);
        var target = new MeleeFighter("T", Team.ENEMY);
        target.takeDirectDamage(99999, log); // убиваем
        assertThrows(DeadUnitException.class,
                () -> new MeleeStrikeAbility().perform(caster, target, log));
    }

    @Test
    void healingDeadUnitThrows() {
        var healer = new Healer("H", Team.PLAYER);
        var target = new MeleeFighter("T", Team.PLAYER);
        target.takeDirectDamage(99999, log);
        assertThrows(DeadUnitException.class,
                () -> healer.getAbility().perform(healer, target, log));
    }

    @Test
    void healingEnemyThrowsCombatException() {
        var healer = new Healer("H", Team.PLAYER);
        var enemy = new MeleeFighter("E", Team.ENEMY);
        assertThrows(CombatException.class,
                () -> healer.getAbility().perform(healer, enemy, log));
    }

    @Test
    void combatEndsWhenOneTeamDies() {
        var weak = new MeleeFighter("Weak", Team.ENEMY);
        weak.takeDirectDamage(99999, log); // сразу мёртв
        var strong = new MeleeFighter("Strong", Team.PLAYER);
        var engine = new CombatEngine(List.of(strong, weak));
        var result = engine.run();
        assertTrue(result.winner().isPresent());
        assertEquals(Team.PLAYER, result.winner().get());
    }

    @Test
    void recordEqualsAndHashCodeContract() {
        var a = new CombatEvent(1, "test");
        var b = new CombatEvent(1, "test");
        var c = new CombatEvent(2, "other");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }
}