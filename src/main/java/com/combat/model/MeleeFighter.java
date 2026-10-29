package com.combat.model;

public final class MeleeFighter extends Unit {
    public MeleeFighter(String name, Team team) {
        super(name, team, 120, 10, 18, 8, 3, DamageType.PHYSICAL);
        setAbility(new MeleeStrikeAbility());
    }
    @Override public UnitType getType() { return UnitType.MELEE; }
}