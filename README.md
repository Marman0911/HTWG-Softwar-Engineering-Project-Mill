# Mühle – Nine Men's Morris

Dieses Projekt ist eine Umsetzung des Brettspiels **Mühle (Nine Men's Morris)** in **Scala 3**.

Das Spiel wurde im Rahmen des Software-Engineering-Projekts an der HTWG Konstanz entwickelt. Es besitzt eine grafische Benutzeroberfläche, eine textbasierte Terminalansicht sowie eine klare Trennung zwischen Modell, Steuerung und Darstellung.

---

## Funktionen

* Grafische Benutzeroberfläche zum Spielen von Mühle
* Textbasierte Darstellung im Terminal
* Setzphase, Bewegungsphase und Sprungphase
* Erkennen von Mühlen
* Entfernen gegnerischer Steine nach einer gebildeten Mühle
* Undo-Funktion für den letzten Zug
* Speichern und Laden von Spielständen
* Speicherung als JSON oder XML
* Unit-Tests und Code-Coverage
* Docker-Unterstützung

---

## Voraussetzungen

Für das lokale Starten werden benötigt:

* Java 17 oder neuer
* sbt
* Scala 3 wird automatisch über sbt verwaltet

---

## Spiel starten

Im Hauptordner des Projekts:

```bash
sbt clean test
sbt run
```

Nach `sbt run` startet die grafische Benutzeroberfläche. Parallel dazu wird auch eine textbasierte Ansicht im Terminal ausgegeben.

---

## Spielregeln

### 1. Setzphase

Zu Beginn besitzt jeder Spieler neun Steine.

* Spieler 1 spielt mit hellen Steinen.
* Spieler 2 spielt mit dunklen Steinen.
* Die Spieler setzen abwechselnd einen Stein auf ein freies Feld.
* Neben dem Spielbrett wird angezeigt, wie viele Steine jeder Spieler noch setzen kann.

---

### 2. Mühle bilden

Eine Mühle besteht aus drei eigenen Steinen in einer erlaubten Reihe.

Wenn ein Spieler eine Mühle bildet:

1. bleibt der Spieler zunächst am Zug,
2. muss einen gegnerischen Stein auswählen,
3. der ausgewählte gegnerische Stein wird vom Brett entfernt.

Steine, die selbst Teil einer gegnerischen Mühle sind, dürfen nur entfernt werden, wenn kein anderer gegnerischer Stein außerhalb einer Mühle vorhanden ist.

---

### 3. Bewegungsphase

Sobald beide Spieler alle neun Steine gesetzt haben, beginnt die Bewegungsphase.

Ablauf:

1. Einen eigenen Stein auswählen.
2. Einen freien, direkt verbundenen Nachbarpunkt auswählen.
3. Der Stein wird verschoben.
4. Anschließend ist der andere Spieler am Zug.

---

### 4. Springen

Hat ein Spieler nur noch genau drei Steine auf dem Brett, darf dieser Spieler springen.

Das bedeutet:

* Der Stein muss nicht mehr auf ein direkt benachbartes Feld bewegt werden.
* Er darf auf jeden freien Punkt des Spielbretts gesetzt werden.

---

### 5. Spielende

Ein Spieler verliert, wenn:

* er weniger als drei Steine besitzt oder
* er keinen legalen Zug mehr ausführen kann.

---

## Bedienung der grafischen Oberfläche

| Aktion                       | Bedienung                                                |
| ---------------------------- | -------------------------------------------------------- |
| Neues Spiel starten          | Im Menü ein Spiel auswählen                              |
| Stein setzen                 | Freien Punkt anklicken                                   |
| Stein bewegen                | Eigenen Stein anklicken, danach Zielfeld anklicken       |
| Gegnerischen Stein entfernen | Nach einer gebildeten Mühle gegnerischen Stein anklicken |
| Zug zurücknehmen             | Button `Undo`                                            |
| Spiel speichern              | Menüpunkt zum Speichern auswählen                        |
| Spiel laden                  | Menüpunkt zum Laden auswählen                            |
| Zurück zum Menü              | Button `Menü`                                            |
| Programm schließen           | Button `Quit`                                            |

---

## Bedienung im Terminal

Neben der GUI läuft eine textbasierte Ansicht im Terminal.

### Stein setzen

```text
a1
```

### Stein bewegen

```text
a1 d1
```

Dabei gilt:

```text
a1 = Startposition
d1 = Zielposition
```

### Zug zurücknehmen

```text
undo
```

---

## Speichern und Laden

Spielstände können in zwei Formaten gespeichert werden:

* JSON
* XML

Die Logik dafür befindet sich im Bereich:

```text
src/main/scala/model/fileio
```

Dort werden die Schnittstelle sowie die JSON- und XML-Implementierungen bereitgestellt.

---

## Projektstruktur

```text
src
├── main
│   └── scala
│       ├── app
│       │   └── MillApp.scala
│       │
│       ├── controller
│       │   ├── command
│       │   │   ├── GameCommand.scala
│       │   │   ├── PlaceCommand.scala
│       │   │   ├── MoveCommand.scala
│       │   │   └── RemoveCommand.scala
│       │   ├── BoardViewMapper.scala
│       │   ├── BoardViewModel.scala
│       │   ├── GameController.scala
│       │   ├── GameMessages.scala
│       │   ├── GameModule.scala
│       │   ├── GamePhase.scala
│       │   ├── IController.scala
│       │   ├── PlacingPhase.scala
│       │   ├── MovingPhase.scala
│       │   └── RemovingPhase.scala
│       │
│       ├── model
│       │   ├── board
│       │   │   ├── impl
│       │   │   │   └── MillBoard.scala
│       │   │   ├── Board.scala
│       │   │   ├── BoardComponent.scala
│       │   │   └── Position.scala
│       │   │
│       │   ├── fileio
│       │   │   ├── FileIOInterface.scala
│       │   │   ├── JsonFileIO.scala
│       │   │   └── XmlFileIO.scala
│       │   │
│       │   ├── game
│       │   │   ├── impl
│       │   │   │   └── GameStateImpl.scala
│       │   │   ├── GameComponent.scala
│       │   │   ├── GameState.scala
│       │   │   ├── GameVariant.scala
│       │   │   ├── MillRules.scala
│       │   │   └── MoveType.scala
│       │   │
│       │   └── player
│       │       ├── impl
│       │       │   └── PlayerImpl.scala
│       │       ├── Player.scala
│       │       ├── PlayerComponent.scala
│       │       └── PlayerId.scala
│       │
│       ├── view
│       │   ├── gui
│       │   │   ├── GuiCoordinateMapper.scala
│       │   │   └── MillGui.scala
│       │   ├── tui
│       │   ├── BoardView.scala
│       │   └── StoneSymbolStrategy.scala
│       │
│       └── MillModel.worksheet.sc
│
└── test
```

---

## Architektur

Das Projekt folgt einer klaren Trennung von Verantwortlichkeiten.

### Model

Der Model-Bereich enthält die eigentliche Spiellogik und die Daten des Spiels.

Beispiele:

* Spielbrett und Positionen
* Spieler und Spielsteine
* Spielzustand
* Mühle-Erkennung
* Bewegungsregeln
* Speichern und Laden

Zu finden unter:

```text
src/main/scala/model
```

---

### Controller

Der Controller verarbeitet Eingaben, führt Spielzüge aus und steuert die verschiedenen Spielphasen.

Beispiele:

* Setzphase
* Bewegungsphase
* Entfernen eines gegnerischen Steins
* Undo
* Wechsel zwischen Spielern
* Speichern und Laden

Zu finden unter:

```text
src/main/scala/controller
```

---

### View

Die View stellt den Spielzustand für die Benutzer dar.

Es gibt zwei Ansichten:

* GUI für die grafische Bedienung
* TUI für die Ausgabe und Eingabe im Terminal

Zu finden unter:

```text
src/main/scala/view
```

---

### Command Pattern

Jeder Spielzug wird als eigenes Command gespeichert.

Beispiele:

```text
PlaceCommand
MoveCommand
RemoveCommand
```

Dadurch kann der letzte Spielzug mit der Undo-Funktion wieder rückgängig gemacht werden.

Zu finden unter:

```text
src/main/scala/controller/command
```

---

### Observer Pattern

Die grafische Oberfläche und die Terminalansicht werden informiert, sobald sich der Spielzustand verändert.

Dadurch wird das Spielbrett nach jedem Zug automatisch aktualisiert.

---

### Dependency Injection

Die Verbindung zwischen Controller und Anwendung wird über Guice hergestellt.

Die Konfiguration befindet sich in:

```text
src/main/scala/controller/GameModule.scala
```

---

## Worksheet

Für die frühe Modellierung des Spiels gibt es ein Worksheet:

```text
src/main/scala/MillModel.worksheet.sc
```

Dort können grundlegende Datenstrukturen wie Positionen, Spieler und Spielsteine zunächst isoliert ausprobiert werden.

Das Worksheet unterstützt ein schrittweises Vorgehen:

```text
Datenmodell erstellen
→ direkt ausprobieren
→ Zugriff prüfen
→ verbessern
→ später in das eigentliche Modell übernehmen
```

---

## Tests

Alle Unit-Tests können mit folgendem Befehl ausgeführt werden:

```bash
sbt clean test
```

Getestet werden unter anderem:

* Erstellen von Spielern und Spielbrettern
* Setzen von Steinen
* Bewegen von Steinen
* Entfernen gegnerischer Steine
* Erkennen von Mühlen
* Wechsel zwischen Spielphasen
* Undo-Funktion
* Speichern und Laden mit JSON und XML
* Verhalten des Controllers
* Terminal- und Brettdarstellung

---

## Code Coverage

Normale Code-Coverage kann mit folgendem Befehl erstellt werden:

```bash
sbt coverage test coverageReport
```

Der HTML-Bericht liegt anschließend normalerweise unter:

```text
target/scala-3.8.3/scoverage-report/index.html
```

---

## Mutation Testing

Für Mutation Testing wird Stryker verwendet:

```bash
sbt stryker
```

Der Bericht wird im `target`-Ordner erstellt.

---

## Docker

Ein Dockerfile und eine `.dockerignore`-Datei sind im Projekt enthalten.

Docker-Image bauen:

```bash
docker build -t mill:v1 .
```

Container starten:

```bash
docker run --rm -it mill:v1
```

Für die grafische Oberfläche ist unter Windows zusätzlich ein X-Server wie VcXsrv oder XLaunch notwendig.

---

## Mögliche Erweiterungen

* Spielmodus gegen eine KI
* KI gegen KI
* Anzeige eines Spielverlaufs
* Animationen beim Setzen und Bewegen
* Hervorheben gebildeter Mühlen
* Verbesserte grafische Hinweise für mögliche Züge
* Weitere Spielvarianten

---

## Team

Software-Engineering-Projekt
HTWG Konstanz

* Manuel Beinlich
* Marin Corluka

