package com.combat.model;

import java.util.Map;

/**
 * Тип урона. Каждая константа несёт таблицу множителей урона
 * для каждого архетипа цели: > 1 — уязвимость, < 1 — сопротивление.
 */
public enum DamageType {
    PHYSICAL(Map.of(UnitType.MELEE, 1.0, UnitType.RANGED, 1.0,
            UnitType.MAGE, 1.25, UnitType.HEALER, 1.0)),
    FIRE(Map.of(UnitType.MELEE, 1.0, UnitType.RANGED, 1.0,
            UnitType.MAGE, 1.5, UnitType.HEALER, 1.0)),
    ICE(Map.of(UnitType.MELEE, 1.0, UnitType.RANGED, 1.5,
            UnitType.MAGE, 1.0, UnitType.HEALER, 1.0)),
    POISON(Map.of(UnitType.MELEE, 1.0, UnitType.RANGED, 1.0,
            UnitType.MAGE, 1.0, UnitType.HEALER, 1.5)),
    HOLY(Map.of(UnitType.MELEE, 1.0, UnitType.RANGED, 1.0,
            UnitType.MAGE, 0.5, UnitType.HEALER, 1.0)),
    SHADOW(Map.of(UnitType.MELEE, 1.0, UnitType.RANGED, 1.0,
            UnitType.MAGE, 1.0, UnitType.HEALER, 2.0));

    private final Map<UnitType, Double> multiplierByTarget;

    DamageType(Map<UnitType, Double> multiplierByTarget) {
        this.multiplierByTarget = Map.copyOf(multiplierByTarget);
    }

    /**
     * Возвращает множитель урона против указанного архетипа цели.
     * @param type архетип цели (например, MAGE или MELEE)
     * @return коэффициент урона (1.5 — уязвимость, 0.5 — сопротивление)
     */
    public double multiplierAgainst(UnitType type) {
        return multiplierByTarget.getOrDefault(type, 1.0);
    }

    /** Неизменяемая таблица уязвимостей. */
    public Map<UnitType, Double> getMultiplierByTarget() {
        return multiplierByTarget;
    }
}