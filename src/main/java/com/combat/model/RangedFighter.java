package com.combat.model;

public final class RangedFighter extends Unit {
    public RangedFighter(String name, Team team) {
        super(name, team, 90, 14, 20, 4, 4, DamageType.PHYSICAL);
        setAbility(new MeleeStrikeAbility());
    }
    @Override public UnitType getType() { return UnitType.RANGED; }
}