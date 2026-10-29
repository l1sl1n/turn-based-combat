package com.combat.model;

/** Огненный шар: магический урон с повышенным коэффициентом. */
public final class FireballAbility implements Ability {
    @Override public String getName() { return "Fireball"; }
    @Override public DamageType getDamageType() { return DamageType.FIRE; }

    @Override
    public void perform(Unit caster, Unit target, CombatLog log) throws DeadUnitException {
        if (!caster.isAlive()) throw new DeadUnitException(caster.getName() + " is dead");
        if (!target.isAlive()) throw new DeadUnitException(target.getName() + " is dead");
        log.abilityUsed(caster, this, target);
        int damage = (int) Math.round(caster.getEffectiveAttack() * 1.5);
        target.takeDamage(damage, DamageType.FIRE, log);
    }
}