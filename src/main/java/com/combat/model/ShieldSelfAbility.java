package com.combat.model;

/** Наложение щита на цель. */
public final class ShieldSelfAbility implements Ability {
    @Override public String getName() { return "Shield"; }
    @Override public DamageType getDamageType() { return DamageType.HOLY; }

    @Override
    public void perform(Unit caster, Unit target, CombatLog log) throws DeadUnitException {
        if (!caster.isAlive()) throw new DeadUnitException(caster.getName() + " is dead");
        if (!target.isAlive()) throw new DeadUnitException(target.getName() + " is dead");
        log.abilityUsed(caster, this, target);
        target.applyEffect(
                new ShieldEffect(Math.max(1, caster.getEffectiveAttack() / 2), 3), log);
    }
}