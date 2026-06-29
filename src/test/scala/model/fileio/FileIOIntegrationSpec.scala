package model.fileio

import java.io.File
import java.nio.file.Files

import model.board.{BoardComponent, Position}
import model.game.{GameComponent, GameState}
import model.player.{PlayerComponent, PlayerId}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class FileIOIntegrationSpec extends AnyWordSpec with Matchers:

  private def deleteRecursively(file: File): Unit =
    if file.isDirectory then
      Option(file.listFiles()).getOrElse(Array.empty[File]).foreach: child =>
        deleteRecursively(child)

    file.delete()

  private def sampleState: GameState =
    val board0 =
      BoardComponent.create(3)

    val board1 =
      board0.placeStone(Position(0, 0), PlayerId.One).get

    val board2 =
      board1.placeStone(Position(2, 1), PlayerId.One).get

    val board3 =
      board2.placeStone(Position(1, 3), PlayerId.Two).get

    val board4 =
      board3.placeStone(Position(2, 7), PlayerId.Two).get

    GameComponent.create(
      board4,
      PlayerComponent.create(
        PlayerId.One,
        stonesInHand = 7,
        stonesOnBoard = 2
      ),
      PlayerComponent.create(
        PlayerId.Two,
        stonesInHand = 7,
        stonesOnBoard = 2
      ),
      PlayerId.Two
    )

  private def assertSameState(
      expected: GameState,
      actual: GameState
  ): Unit =
    actual.currentPlayer shouldBe expected.currentPlayer
    actual.board.boardSize shouldBe expected.board.boardSize
    actual.board.placedStones shouldBe expected.board.placedStones

    actual.player1.stonesInHand shouldBe expected.player1.stonesInHand
    actual.player1.stonesOnBoard shouldBe expected.player1.stonesOnBoard

    actual.player2.stonesInHand shouldBe expected.player2.stonesInHand
    actual.player2.stonesOnBoard shouldBe expected.player2.stonesOnBoard

  "JsonFileIO" should {

    "save and load all game state information" in {
      val directory =
        Files.createTempDirectory("mill-json-test").toFile

      try
        val file =
          new File(directory, "nested/game.json")

        val original =
          sampleState

        val fileIO =
          new JsonFileIO

        fileIO.save(original, file.getPath)

        file.exists shouldBe true

        val loaded =
          fileIO.load(file.getPath)

        loaded.isSuccess shouldBe true
        assertSameState(original, loaded.get)
      finally
        deleteRecursively(directory)
    }

    "return Failure when the JSON file does not exist" in {
      val directory =
        Files.createTempDirectory("mill-json-missing").toFile

      try
        val fileIO = new JsonFileIO
        fileIO.load(new File(directory, "missing.json").getPath).isFailure shouldBe true
      finally
        deleteRecursively(directory)
    }
  }

  "XmlFileIO" should {

    "save and load all game state information" in {
      val directory =
        Files.createTempDirectory("mill-xml-test").toFile

      try
        val file =
          new File(directory, "nested/game.xml")

        val original =
          sampleState

        val fileIO =
          new XmlFileIO

        fileIO.save(original, file.getPath)

        file.exists shouldBe true

        val loaded =
          fileIO.load(file.getPath)

        loaded.isSuccess shouldBe true
        assertSameState(original, loaded.get)
      finally
        deleteRecursively(directory)
    }

    "return Failure when the XML file does not exist" in {
      val directory =
        Files.createTempDirectory("mill-xml-missing").toFile

      try
        val fileIO = new XmlFileIO
        fileIO.load(new File(directory, "missing.xml").getPath).isFailure shouldBe true
      finally
        deleteRecursively(directory)
    }
  }
