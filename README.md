# Mühle – Nine Men's Morris

Dieses Projekt ist eine Umsetzung des Brettspiels **Mühle** (*Nine Men's Morris*) in Scala 3.

Das Spiel wurde im Rahmen des Software-Engineering-Projekts entwickelt. Es besitzt eine grafische Benutzeroberfläche, eine textbasierte Ansicht im Terminal sowie eine klare Trennung zwischen Spiellogik, Darstellung und Steuerung.

---

## Spiel starten

Voraussetzungen:

* Java 17 oder neuer
* sbt

Im Hauptordner des Projekts:

```powershell
sbt clean test
sbt run
```

Nach `sbt run` öffnet sich die grafische Oberfläche.

---

## Spielablauf

### 1. Setzphase

Zu Beginn besitzt jeder Spieler neun Steine.

* Spieler 1 spielt mit hellen Steinen.
* Spieler 2 spielt mit dunklen Steinen.
* Abwechselnd wird ein Stein auf einen freien grünen Punkt gesetzt.
* Links und rechts neben dem Spielbrett wird angezeigt, wie viele Steine jeder Spieler noch setzen kann.

### 2. Mühle bilden

Eine Mühle besteht aus drei eigenen Steinen in einer erlaubten Reihe.

Wenn ein Spieler eine Mühle bildet:

* bleibt er zunächst am Zug,
* muss er einen gegnerischen Stein auswählen,
* der ausgewählte gegnerische Stein wird vom Brett entfernt.

Steine, die selbst Teil einer gegnerischen Mühle sind, dürfen nur entfernt werden, wenn keine anderen gegnerischen Steine außerhalb einer Mühle vorhanden sind.

### 3. Bewegungsphase

Sobald beide Spieler alle Steine gesetzt haben, beginnt die Bewegungsphase.

So wird ein Stein bewegt:

1. Einen eigenen Stein anklicken.
2. Der ausgewählte Stein wird markiert.
3. Einen freien, direkt verbundenen Nachbarpunkt anklicken.
4. Der Stein wird verschoben und der andere Spieler ist am Zug.

### 4. Springen

Hat ein Spieler nur noch genau drei Steine auf dem Brett, darf er mit seinen Steinen auf jeden freien Punkt springen und ist nicht mehr auf direkte Nachbarfelder beschränkt.

### 5. Spielende

Ein Spieler verliert, wenn:

* er weniger als drei Steine besitzt oder
* er keinen legalen Zug mehr machen kann.

---

## Bedienung der GUI

| Aktion                       | Bedienung                                          |
| ---------------------------- | -------------------------------------------------- |
| Spiel starten                | `Play` → `Spieler 1 gegen Spieler 2`               |
| Anleitung schließen          | `X` oben rechts oder `Spiel starten`               |
| Stein setzen                 | Freien grünen Punkt anklicken                      |
| Stein bewegen                | Eigenen Stein anklicken, danach Zielfeld anklicken |
| Gegnerischen Stein entfernen | Nach einer Mühle gegnerischen Stein anklicken      |
| Letzten Zug zurücknehmen     | Button `Undo`                                      |
| Zurück zum Menü              | Button `Menü`                                      |
| Programm schließen           | Button `Quit`                                      |

---

## Bedienung im Terminal

Neben der GUI gibt es eine textbasierte Ansicht.

### Stein setzen

```text
a1
```

### Stein bewegen

```text
a1 d1
```

Dabei ist:

```text
a1 = Startposition
d1 = Zielposition
```

### Zug zurücknehmen

```text
undo
```

---

## Projektstruktur

```text
src/main/scala
├── app
│   └── MillApp.scala
├── controller
│   ├── GameController.scala
│   ├── GamePhase.scala
│   ├── PlacingPhase.scala
│   ├── MovingPhase.scala
│   ├── RemovingPhase.scala
│   └── command
│       ├── PlaceCommand.scala
│       ├── MoveCommand.scala
│       └── RemoveCommand.scala
├── gui
│   ├── MillGui.scala
│   └── GuiCoordinateMapper.scala
├── model
│   ├── board
│   ├── game
│   └── player
├── tui
│   └── TuiRunner.scala
└── view
    └── BoardView.scala
```

---

## Architektur

Das Projekt ist in mehrere Bereiche aufgeteilt:

* **Model**
  Enthält die Spiellogik, das Brett, die Positionen und die Spieler.

* **Controller**
  Nimmt Eingaben entgegen, führt Spielzüge aus und verwaltet die Spielphasen.

* **GUI / TUI / View**
  Stellen den Spielzustand grafisch oder im Terminal dar.

* **Commands**
  Jeder Spielzug wird als Command gespeichert. Dadurch kann ein Zug über `Undo` rückgängig gemacht werden.

* **Observer**
  Die GUI und die Terminalansicht werden informiert, wenn sich der Spielzustand verändert.

---

## Tests

Die Tests können mit folgendem Befehl ausgeführt werden:

```powershell
sbt clean test
```

Getestet werden unter anderem:

* Erstellen von Spielern und Spielbrettern
* Setzen von Steinen
* Eingabe von Koordinaten
* Wechsel zwischen Spielern
* Bewegungsphase
* Controller-Verhalten
* Undo-Funktion
* Darstellung des Brettes

---

## Bekannte Erweiterungsmöglichkeiten

Mögliche zukünftige Erweiterungen:

* Spielmodus gegen eine KI
* KI gegen KI
* bessere Animationen beim Setzen und Bewegen
* Anzeige bereits gebildeter Mühlen
* Speichern und Laden eines Spielstands
* Anzeige eines Spielverlaufs
* bessere Fehlermeldungen direkt im GUI

---

## Team

Software-Engineering-Projekt
HTWG Konstanz
Manuel Beinlich 
Marin Corluka

