package com.combat.model;

/**
 * Интерфейс, описывающий способность, которую может применить юнит.
 */
public interface Ability {
    String getName();
    DamageType getDamageType();

    /**
     * Применяет способность к указанной цели.
     * @param caster тот, кто применяет способность
     * @param target цель способности
     * @param log журнал для записи события
     * @throws DeadUnitException если кастер или цель уже мертвы
     * @throws CombatException если цель недопустима (например, лечить врага)
     */
    void perform(Unit caster, Unit target, CombatLog log)
            throws DeadUnitException, CombatException;
}