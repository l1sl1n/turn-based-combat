package com.combat.model;

public final class Healer extends Unit {
    public Healer(String name, Team team) {
        super(name, team, 85, 6, 12, 5, 5, DamageType.HOLY);
        setAbility(new HealAbility());
    }
    @Override public UnitType getType() { return UnitType.HEALER; }
}