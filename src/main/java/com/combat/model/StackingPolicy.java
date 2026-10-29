package com.combat.model;

/** Правило повторного наложения эффекта. */
public enum StackingPolicy {
    REFRESH,         // заменить старый
    STACK_DURATION,  // сложить длительности
    STACK_POWER,     // обе копии активны
    IGNORE           // оставить старый
}