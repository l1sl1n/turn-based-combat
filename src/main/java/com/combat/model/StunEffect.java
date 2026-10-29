package com.combat.model;

/** Оглушение: носитель пропускает ход. */
public final class StunEffect extends Effect {
    public StunEffect(int duration) {
        super("Stun", EffectType.STUN, duration);
    }

    @Override public StackingPolicy getStackingPolicy() { return StackingPolicy.REFRESH; }
    @Override public void onTurnStart(Unit target, CombatLog log) { /* пассивный */ }
    @Override public boolean preventsAction() { return true; }
}