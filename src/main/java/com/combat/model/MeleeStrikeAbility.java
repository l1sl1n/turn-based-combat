package com.combat.model;

/** Обычный физический удар. */
public final class MeleeStrikeAbility implements Ability {
    @Override public String getName() { return "Slash"; }
    @Override public DamageType getDamageType() { return DamageType.PHYSICAL; }

    @Override
    public void perform(Unit caster, Unit target, CombatLog log) throws DeadUnitException {
        if (!caster.isAlive()) throw new DeadUnitException(caster.getName() + " is dead");
        if (!target.isAlive()) throw new DeadUnitException(target.getName() + " is dead");
        log.abilityUsed(caster, this, target);
        target.takeDamage(caster.getEffectiveAttack(), DamageType.PHYSICAL, log);
    }
}