package com.combat.model;

/** Яд: в начале хода носителя наносит фиксированный урон. */
public final class PoisonEffect extends Effect {
    private final int damagePerTurn;

    public PoisonEffect(int damagePerTurn, int duration) {
        super("Poison", EffectType.POISON, duration);
        if (damagePerTurn <= 0) throw new IllegalArgumentException();
        this.damagePerTurn = damagePerTurn;
    }

    public int getDamagePerTurn() { return damagePerTurn; }

    @Override public StackingPolicy getStackingPolicy() { return StackingPolicy.STACK_POWER; }

    @Override
    public void onTurnStart(Unit target, CombatLog log) {
        log.effectTick(target, this);
        target.takeDirectDamage(damagePerTurn, log);
    }
}