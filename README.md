# Turn-Based Combat (Задача 17)

Модель пошагового боя: юниты, способности, эффекты.

## Модель
- `Unit` — sealed-иерархия из 4 архетипов (MeleeFighter, RangedFighter, Mage, Healer).
- `Effect` — sealed-иерархия (Poison, Shield, Stun, Buff) с политиками наложения.
- `Ability` — интерфейс с 5 реализациями.
- `DamageType` — перечисление с таблицей уязвимостей/сопротивлений.
- `CombatEngine` — движок, поддерживает автоматический бой и воспроизведение.

## Запуск
mvn compile exec:java -Dexec.mainClass=com.combat.demo.Demo

Или: mvn package && java -jar target/turn-based-combat-1.0.0.jar

## Тесты
mvn test