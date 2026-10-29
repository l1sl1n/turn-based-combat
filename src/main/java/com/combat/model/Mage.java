package com.combat.model;

public final class Mage extends Unit {
    public Mage(String name, Team team) {
        super(name, team, 70, 8, 25, 3, 6, DamageType.FIRE);
        setAbility(new FireballAbility());
    }
    @Override public UnitType getType() { return UnitType.MAGE; }
}