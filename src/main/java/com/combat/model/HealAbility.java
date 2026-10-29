package com.combat.model;

/** Лечение союзника. */
public final class HealAbility implements Ability {
    @Override public String getName() { return "Heal"; }
    @Override public DamageType getDamageType() { return DamageType.HOLY; }

    @Override
    public void perform(Unit caster, Unit target, CombatLog log)
            throws DeadUnitException, CombatException {
        if (!caster.isAlive()) throw new DeadUnitException(caster.getName() + " is dead");
        if (!target.isAlive()) throw new DeadUnitException(target.getName() + " is dead");
        if (caster.getTeam() != target.getTeam()) {
            throw new CombatException("Heal can only target allies");
        }
        log.abilityUsed(caster, this, target);
        target.heal(caster.getEffectiveAttack() * 2, log);
    }
}