package com.combat.model;

/** Проверяемое исключение: нарушение правил боя. */
public class CombatException extends Exception {
    public CombatException(String message) { super(message); }
}