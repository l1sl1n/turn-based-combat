package com.combat.model;

/** Усиление: добавляет атаку носителю. */
public final class BuffEffect extends Effect {
    private final int attackBonus;

    public BuffEffect(int attackBonus, int duration) {
        super("Buff", EffectType.BUFF, duration);
        if (attackBonus <= 0) throw new IllegalArgumentException();
        this.attackBonus = attackBonus;
    }

    @Override public StackingPolicy getStackingPolicy() { return StackingPolicy.STACK_POWER; }
    @Override public void onTurnStart(Unit target, CombatLog log) { /* пассивный */ }
    @Override public int getAttackBonus() { return attackBonus; }
}