package com.combat.model;

/** То, что юнит умеет делать на своём ходу. */
public interface Ability {
    String getName();
    DamageType getDamageType();

    /**
     * Применить способность.
     * @throws DeadUnitException если кастер или цель уже мертвы
     * @throws CombatException   если цель недопустима (например, лечить врага)
     */
    void perform(Unit caster, Unit target, CombatLog log)
            throws DeadUnitException, CombatException;
}