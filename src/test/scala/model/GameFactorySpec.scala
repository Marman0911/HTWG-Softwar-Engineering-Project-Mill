package model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class GameFactorySpec extends AnyFlatSpec with Matchers:

  "GameFactory.standard" should "create a game state with a standard board" in:
    val state = GameFactory.standard
    state.board.boardSize shouldBe 3
    state.player1.id shouldBe PlayerId.One
    state.player2.id shouldBe PlayerId.Two
    state.currentPlayer shouldBe PlayerId.One

  "GameFactory.small" should "create a game state with a smaller board" in:
    val state = GameFactory.small
    state.board.boardSize shouldBe 2
    state.player1.id shouldBe PlayerId.One
    state.player2.id shouldBe PlayerId.Two
    state.currentPlayer shouldBe PlayerId.One

  "GameFactory.create" should "initialize correct board sizes depending on variant" in:
    val standardState = GameFactory.create(StandardMill)
    standardState.board.boardSize shouldBe 3

    val smallState = GameFactory.create(SmallMill)
    smallState.board.boardSize shouldBe 2