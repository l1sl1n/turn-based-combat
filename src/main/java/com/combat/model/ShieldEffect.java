package com.combat.model;

/** Щит: поглощает часть входящего урона. */
public final class ShieldEffect extends Effect {
    private final int absorption;

    public ShieldEffect(int absorption, int duration) {
        super("Shield", EffectType.SHIELD, duration);
        if (absorption <= 0) throw new IllegalArgumentException();
        this.absorption = absorption;
    }

    @Override public StackingPolicy getStackingPolicy() { return StackingPolicy.STACK_POWER; }

    @Override public void onTurnStart(Unit target, CombatLog log) { /* пассивный */ }

    @Override
    public int modifyIncomingDamage(int damage) {
        return Math.max(0, damage - absorption);
    }
}