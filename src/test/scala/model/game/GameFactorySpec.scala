package model.game

import model.game.GameComponent
import model.game.SmallMill
import model.game.StandardMill
import model.player.PlayerId
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class GameFactorySpec extends AnyFlatSpec with Matchers:

  "GameComponent.standard" should "create a game state with a standard board" in:
    val state = GameComponent.standard
    state.board.boardSize shouldBe 3
    state.player1.id shouldBe PlayerId.One
    state.player2.id shouldBe PlayerId.Two
    state.currentPlayer shouldBe PlayerId.One

  "GameComponent.small" should "create a game state with a smaller board" in:
    val state = GameComponent.small
    state.board.boardSize shouldBe 2
    state.player1.id shouldBe PlayerId.One
    state.player2.id shouldBe PlayerId.Two
    state.currentPlayer shouldBe PlayerId.One

  "GameComponent.create" should "initialize correct board sizes depending on variant" in:
    val standardState = GameComponent.create(StandardMill)
    standardState.board.boardSize shouldBe 3

    val smallState = GameComponent.create(SmallMill)
    smallState.board.boardSize shouldBe 2
