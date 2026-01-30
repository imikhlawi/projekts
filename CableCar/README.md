# CableCar (SoPra Projekt 2)

Spiel-Projekt mit **KI (MCTS)** und **Netzwerk** – entwickelt im Rahmen des **Software-Praktikums (SoPra)** der TU Dortmund. Kotlin, **Gradle**, **BGW-GUI**, **BGW-Net** (Client/Common), Korau (Audio), NTF.

## Ausführung

```bash
./gradlew run
# Windows: gradlew.bat run
```

## Projektstruktur (Kurz)

- `src/main/kotlin/` – `Main.kt`, `entity/` (Game, GameField, Player, Tile, …), `service/` (GameService, NetworkService, …), `ai/` (MCTS, AiActionService), `view/` (Szenen, CCApplication)
- `src/test/` – Tests für Entity und Service
- Gradle: Application-Plugin, mainClass = `MainKt`; BGW-Net, Korau, NTF-Abhängigkeiten

## Wichtige Links (Uni)

* Aktuelle Informationen zu diesem SoPra: https://sopra.cs.tu-dortmund.de/wiki/sopra/22d/start
* Beispielprojekt Kartenspiel War: https://sopra-gitlab.cs.tu-dortmund.de/internal/bgw-war
* Weitere Links: https://sopra.cs.tu-dortmund.de/wiki/infos/links/
