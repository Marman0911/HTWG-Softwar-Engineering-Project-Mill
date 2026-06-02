# HTWG-Software-Engineering-Project: Mill

2026-04-17 - Abgabe 4 Plan:

Code: @Marin
Test: @Manuel 

[![Coverage Status](https://coveralls.io/repos/github/Marman0911/HTWG-Softwar-Engineering-Project-Mill/badge.svg?branch=main)](https://coveralls.io/github/Marman0911/HTWG-Softwar-Engineering-Project-Mill?branch=main)
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
