# HTWG-Software-Engineering-Project: Mill

[![Scala CI](https://github.com/Marman0911/HTWG-Softwar-Engineering-Project-Mill/actions/workflows/scala.yml/badge.svg?branch=main)](https://github.com/Marman0911/HTWG-Softwar-Engineering-Project-Mill/actions/workflows/scala.yml)
[![Coverage Status](https://coveralls.io/repos/github/Marman0911/HTWG-Softwar-Engineering-Project-Mill/badge.svg?branch=main&service=github)](https://coveralls.io/github/Marman0911/HTWG-Softwar-Engineering-Project-Mill?branch=main)

Nine Men's Morris (Mühle) als Scala-Projekt im Rahmen des Software-Engineering-Kurses an der HTWG Konstanz.

---

## Was das Programm macht

Zwei Spieler spielen abwechselnd Mühle in der Konsole (TUI). Jeder Spieler gibt eine Koordinate ein (z.B. `a1`), um einen Stein auf dem Spielfeld zu platzieren. Das Spiel endet, wenn ein Spieler weniger als 3 Steine hat.

### Spielstart

```
sbt run
```

### Koordinatensystem

Das Spielfeld wird mit Buchstaben (Spalten `a`–`g`) und Zahlen (Zeilen `1`–`7`) adressiert:

```
1 +--------------+--------------+
  |              |              |
2 |    +---------+---------+    |
  |    |         |         |    |
3 |    |    +----+----+    |    |
  |    |    |         |    |    |
  +----+----+         +----+----+
  |    |    |         |    |    |
5 |    |    +----+----+    |    |
  |    |         |         |    |
6 |    +---------+---------+    |
  |              |              |
7 +--------------+--------------+
  a    b    c    d    e    f    g
```

---

## Architektur – MVC

Das Projekt folgt strikt der MVC-Architektur nach den Vorlesungsfolien:

```
TUI / GUI (View)
   │  registriert sich als Observer
   │  ruft controller.handleInput(input) auf
   ▼
GameController (Controller = Observable)
   │  ändert den State im Model
   │  sendet parameterloses update()-Signal an Observer
   ▼
GameState / MillBoard / Player (Model)
   │  hält alle persistenten Daten
   │  enthält nur lokale Logik auf eigenen Daten
```

**Datenfluss nach einer Eingabe:**
1. View → `controller.handleInput("a1")`
2. Controller → `state.placeStone(pos)` (Model)
3. Controller → `notifyObservers()` (leeres Signal)
4. View → `controller.boardViewModel` (Pull, kein Model-Typ)
5. View rendert `BoardViewModel` (reines Controller-DTO)

### Schichten und ihre Pakete

| Paket | Inhalt |
|---|---|
| `model` | `GameState`, `MillBoard`, `Player`, `PlayerId`, `PlayerFactory` |
| `controller` | `GameController`, `Observable`, `GameObserver`, `BoardViewMapper`, `BoardViewModel`, `GameMessages` |
| `view` | `BoardView`, `StoneSymbolStrategy` |
| `tui` | `TuiRunner`, `@main millGame` |

### Designentscheidungen

- **Controller ist Observable** – die View registriert sich, der Controller sendet nur ein Signal ohne Daten
- **View macht Pull** – `BoardView.update()` ruft `controller.boardViewModel` auf, kein Model-Import in der View
- **BoardViewModel als DTO** – entkoppelt View vom Model, enthält nur primitive Typen und Int-Werte
- **StoneSymbolStrategy** – Strategy Pattern, Darstellung der Steine austauschbar (Zahlen `1`/`2` oder Buchstaben `X`/`O`)
- **TuiRunner** – testbare Klasse, `@main` ist nur der Einstiegspunkt

---

## Tests

```
sbt test          # Unit Tests
sbt coverageReport  # Testabdeckung
sbt stryker       # Mutationstests
```

---

## TODOs

### KI-Gegner (Einzelspielermodus)

- [ ] `PlayerType`-Enum einführen (`Human`, `AI`)
- [ ] `AIStrategy`-Trait definieren mit `selectMove(state): Position`
- [ ] Implementierungen:
  - [ ] `RandomStrategy` – wählt zufälligen freien Platz
  - [ ] `GreedyStrategy` – bevorzugt Mühlen-bildende Züge
  - [ ] `MinimaxStrategy` – Minimax mit Alpha-Beta-Pruning für optimales Spiel
- [ ] Controller bekommt optionalen `AIStrategy`-Parameter, übernimmt Zug automatisch wenn KI am Zug ist

### GUI

- [ ] Neue View-Implementierung `GuiView extends GameObserver` neben TUI
- [ ] Ideen zur Umsetzung:
  - [ ] **ScalaFX** – nativer Scala-Wrapper für JavaFX, einfache Einbindung
  - [ ] **Swing** – klassisch, leichtgewichtig, gut dokumentiert
  - [ ] **Web-UI** – Scala.js Frontend, Controller bleibt identisch
- [ ] Spielfeld als klickbares Board, Steine per Mausklick setzen
- [ ] Animationen für Steinplatzierung und Mühlenbildung
- [ ] Spieler-Namen eingeben am Start
- [ ] Anzeige der verbleibenden Steine pro Spieler


---

## Was noch fehlt

### 1. Kern-Datenstrukturen

**`MillBoard` erweitern:**
- Aktuell rendert das Board nur die leere Struktur – es kennt keine **Spielfelder (Nodes/Positions)** mit Koordinaten
- Benötigt wird: eine Repräsentation der **24 Schnittpunkte** (z.B. als `Map[Position, Option[PlayerId]]`)
- Logik für: Stein setzen, Stein bewegen, Stein entfernen
- Erkennung einer **Mühle** (3 Steine in einer Reihe)

**Neue Datei `GameState.scala` (empfohlen):**
- Kapselt den gesamten Spielzustand: aktuelles Board, beide Spieler, aktive Phase, aktueller Spieler
- Enthält die Spielphasen: `Placing` → `Moving` → `Flying`
- Unterscheidung ob gerade eine Mühle geschlossen wurde (→ Entfernen-Phase)

---

### 2. Spiellogik

Entweder in `GameState` oder eigene Datei `GameLogic.scala`:
- Validierung: Ist ein Zug erlaubt? (Nur benachbarte Felder beim Bewegen)
- Mühlen-Erkennung nach jedem Zug
- Gewinnbedingung prüfen (`hasLost`)
- Zustandsübergänge (nächste Phase/nächster Spieler)

Die **Nachbarschafts-Beziehungen** der 24 Positionen müssen kodiert werden (als Graph/Map).

---

### 3. Text-UI (Main.scala überarbeiten)

- Game-Loop: Spielzustand anzeigen → Eingabe lesen → verarbeiten → wiederholen
- Board-Rendering mit Steinpositionen (z.B. `W`/`B` statt `+`)
- Eingabeformat definieren (z.B. `"a1"` für Position oder `"a1 b2"` für Zug)
- Fehlermeldungen bei ungültigen Eingaben

---

### Empfohlene Dateistruktur

| Datei | Inhalt |
|---|---|
| board.scala | `MillBoard` + Positionen + Rendering mit Steinen |
| player.scala | `Player`, `PlayerId` (bereits gut) |
| `GameState.scala` | Spielzustand, Phasen, Spiellogik |
| Main.scala | Game-Loop, Text-UI |
