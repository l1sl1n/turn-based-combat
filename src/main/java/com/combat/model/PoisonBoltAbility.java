package com.combat.model;

/** Ядовитый снаряд: слабый урон + наложение яда. */
public final class PoisonBoltAbility implements Ability {
    @Override public String getName() { return "Poison Bolt"; }
    @Override public DamageType getDamageType() { return DamageType.POISON; }

    @Override
    public void perform(Unit caster, Unit target, CombatLog log) throws DeadUnitException {
        if (!caster.isAlive()) throw new DeadUnitException(caster.getName() + " is dead");
        if (!target.isAlive()) throw new DeadUnitException(target.getName() + " is dead");
        log.abilityUsed(caster, this, target);
        int damage = (int) Math.round(caster.getEffectiveAttack() * 0.6);
        target.takeDamage(damage, DamageType.POISON, log);
        if (target.isAlive()) {
            target.applyEffect(
                    new PoisonEffect(Math.max(1, caster.getEffectiveAttack() / 3), 3), log);
        }
    }
}