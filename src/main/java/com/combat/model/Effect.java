package com.combat.model;

/**
 * Базовый класс эффектов. Sealed — только четыре перечисленных
 * подкласса могут его наследовать.
 */
public sealed abstract class Effect
        permits PoisonEffect, ShieldEffect, StunEffect, BuffEffect {

    private final String name;
    private final EffectType type;
    private int remainingTurns;

    protected Effect(String name, EffectType type, int duration) {
        if (duration <= 0) throw new IllegalArgumentException("duration must be positive");
        this.name = name;
        this.type = type;
        this.remainingTurns = duration;
    }

    public final String getName() { return name; }
    public final EffectType getType() { return type; }
    public final int getRemainingTurns() { return remainingTurns; }

    public abstract StackingPolicy getStackingPolicy();

    /** Вызывается в начале хода носителя. */
    public abstract void onTurnStart(Unit target, CombatLog log);

    /** Хук для уменьшения входящего урона. По умолчанию ничего не делает. */
    public int modifyIncomingDamage(int damage) { return damage; }

    /** true — если эффект запрещает действовать (оглушение). */
    public boolean preventsAction() { return false; }

    /** Бонус к атаке от этого эффекта. */
    public int getAttackBonus() { return 0; }

    final void tick() { remainingTurns--; }

    public final boolean isExpired() { return remainingTurns <= 0; }

    final void addDuration(int extra) {
        if (extra <= 0) throw new IllegalArgumentException();
        remainingTurns += extra;
    }
}