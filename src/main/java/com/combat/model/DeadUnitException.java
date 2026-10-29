package com.combat.model;

/** Проверяемое исключение: действие над мёртвым юнитом. */
public class DeadUnitException extends CombatException {
    public DeadUnitException(String message) { super(message); }
}