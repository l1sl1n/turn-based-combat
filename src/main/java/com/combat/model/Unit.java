package com.combat.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Базовый класс всех юнитов. Sealed — только четыре архетипа.
 */
public sealed abstract class Unit
        permits MeleeFighter, RangedFighter, Mage, Healer {

    private final String name;
    private final Team team;
    private final int maxHealth;
    private int health;
    private final int initiative;
    private final int baseAttack;
    private final int defense;
    private final int magicResistance;
    private final DamageType primaryDamageType;
    private final List<Effect> effects = new ArrayList<>();
    private Ability ability;

    protected Unit(String name, Team team, int maxHealth, int initiative,
                   int baseAttack, int defense, int magicResistance,
                   DamageType primaryDamageType) {
        this.name = Objects.requireNonNull(name);
        this.team = Objects.requireNonNull(team);
        this.primaryDamageType = Objects.requireNonNull(primaryDamageType);
        if (maxHealth <= 0) throw new IllegalArgumentException("maxHealth must be positive");
        if (initiative < 0) throw new IllegalArgumentException("initiative must be >= 0");
        if (baseAttack < 0) throw new IllegalArgumentException("attack must be >= 0");
        if (defense < 0) throw new IllegalArgumentException("defense must be >= 0");
        if (magicResistance < 0) throw new IllegalArgumentException("magicResistance must be >= 0");
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.initiative = initiative;
        this.baseAttack = baseAttack;
        this.defense = defense;
        this.magicResistance = magicResistance;
    }

    public abstract UnitType getType();

    public final String getName() { return name; }
    public final Team getTeam() { return team; }
    public final int getMaxHealth() { return maxHealth; }
    public final int getHealth() { return health; }
    public final int getInitiative() { return initiative; }
    public final int getDefense() { return defense; }
    public final int getMagicResistance() { return magicResistance; }
    public final DamageType getPrimaryDamageType() { return primaryDamageType; }

    /** Неизменяемый снимок списка эффектов. */
    public final List<Effect> getEffects() { return List.copyOf(effects); }

    public final boolean isAlive() { return health > 0; }

    public final int getEffectiveAttack() {
        int bonus = 0;
        for (Effect e : effects) bonus += e.getAttackBonus();
        return baseAttack + bonus;
    }

    public final Ability getAbility() {
        if (ability == null) throw new IllegalStateException(name + " has no ability");
        return ability;
    }

    protected final void setAbility(Ability ability) {
        this.ability = Objects.requireNonNull(ability);
    }

    /** Наложить эффект с учётом политики наложения. */
    public final void applyEffect(Effect newEffect, CombatLog log) {
        Objects.requireNonNull(newEffect);
        Optional<Effect> existing = effects.stream()
                .filter(e -> e.getType() == newEffect.getType())
                .findFirst();
        if (existing.isEmpty()) {
            effects.add(newEffect);
            log.effectApplied(this, newEffect);
            return;
        }
        Effect old = existing.get();
        switch (newEffect.getStackingPolicy()) {
            case REFRESH -> {
                effects.remove(old);
                effects.add(newEffect);
                log.effectRefreshed(this, old, newEffect);
            }
            case STACK_POWER -> {
                effects.add(newEffect);
                log.effectStacked(this, newEffect);
            }
            case STACK_DURATION -> {
                old.addDuration(newEffect.getRemainingTurns());
                log.effectExtended(this, old);
            }
            case IGNORE -> log.effectIgnored(this, newEffect);
        }
    }

    /** Тикание эффектов: яд наносит урон, длительности уменьшаются. */
    public final void tickEffects(CombatLog log) {
        for (Effect e : List.copyOf(effects)) {
            e.onTurnStart(this, log);
            e.tick();
        }
    }

    /** Удаление истёкших эффектов. */
    public final void removeExpiredEffects(CombatLog log) {
        var it = effects.iterator();
        while (it.hasNext()) {
            Effect e = it.next();
            if (e.isExpired()) {
                it.remove();
                log.effectExpired(this, e);
            }
        }
    }

    public final boolean isStunned() {
        return effects.stream().anyMatch(Effect::preventsAction);
    }

    /** Основной путь урона: уязвимость, затем броня, затем щиты. */
    public final void takeDamage(int rawDamage, DamageType type, CombatLog log) {
        if (rawDamage < 0) throw new IllegalArgumentException("damage must be >= 0");
        double mult = type.multiplierAgainst(getType());
        int scaled = (int) Math.round(rawDamage * mult);
        int afterArmor = switch (type) {
            case PHYSICAL -> Math.max(0, scaled - defense);
            default -> Math.max(0, scaled - magicResistance);
        };
        int finalDamage = afterArmor;
        for (Effect e : effects) finalDamage = e.modifyIncomingDamage(finalDamage);
        health = Math.max(0, health - finalDamage);
        log.damageTaken(this, finalDamage, type, mult);
        if (health == 0) log.unitDied(this);
    }

    /** Прямой урон (яд): игнорирует броню, но проходит через щиты. */
    public final void takeDirectDamage(int rawDamage, CombatLog log) {
        int remaining = rawDamage;
        for (Effect e : effects) remaining = e.modifyIncomingDamage(remaining);
        health = Math.max(0, health - remaining);
        log.damageTaken(this, remaining, DamageType.POISON, 1.0);
        if (health == 0) log.unitDied(this);
    }

    public final void heal(int amount, CombatLog log) throws DeadUnitException {
        if (amount < 0) throw new IllegalArgumentException();
        if (!isAlive()) throw new DeadUnitException(name + " is dead and cannot be healed");
        int healed = Math.min(amount, maxHealth - health);
        health += healed;
        log.healed(this, healed);
    }

    @Override
    public String toString() {
        return name + " the " + getType() + " (" + health + "/" + maxHealth + ")";
    }
}