# Quality / Stryker patch

This patch contains:

## Production fixes

- `JsonFileIO.scala`: restores `stonesOnBoard` correctly after loading a JSON save.
- `XmlFileIO.scala`: restores `stonesOnBoard` correctly after loading an XML save.
- `GameController.scala`: selects `MovingPhase` immediately when a supplied initial state has no stones in hand. This also removes duplicated phase-selection code.

## New tests

- File save/load round trips for JSON and XML.
- GameController save/load, phase changes, mills, removal, game over, undo.
- GameState placement/move/removal edge cases.
- TUI handling of `undo`, `null` input and observer output.
- Additional phase, message and MillRules cases.

Run after extraction:

    sbt clean test
    sbt stryker

This patch intentionally does not add GUI exclusions beyond the existing Stryker settings.
