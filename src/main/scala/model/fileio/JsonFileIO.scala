package model.fileio

import model.fileio.FileIOInterface
import model.game.{GameState, GameComponent}
import model.board.{Board, Position, BoardComponent}
import model.player.{Player, PlayerComponent, PlayerId}
import scala.util.{Try, Success, Failure}
import java.io.{File, PrintWriter}
import play.api.libs.json.*

class JsonFileIO extends FileIOInterface:

  override def save(state: GameState, filePath: String): Unit =
    val file = new File(filePath)
    if (file.getParentFile != null) file.getParentFile.mkdirs()
    
    val pw = new PrintWriter(file)
    
    val json = Json.obj(
      "currentPlayer" -> state.currentPlayer.toString,
      "stonesInHand1" -> state.player1.stonesInHand,
      "stonesInHand2" -> state.player2.stonesInHand,
      "boardSize" -> state.board.boardSize,
      "stones" -> JsArray(
        state.board.placedStones.map((pos, playerId) =>
          val (r, c) = state.board.posCoords(pos)
          Json.obj(
            "row" -> r,
            "col" -> c,
            "player" -> playerId.toString
          )
        ).toSeq
      )
    )

    pw.write(Json.prettyPrint(json))
    pw.close()
    println(s"Erfolgreich als JSON gespeichert unter: $filePath")

  override def load(filePath: String): Try[GameState] =
    Try:
      val source = scala.io.Source.fromFile(filePath)
      val jsonString = source.getLines().mkString
      source.close()
      
      val json = Json.parse(jsonString)
      val currentPlayerStr = (json \ "currentPlayer").as[String]
      val currentPlayer = if currentPlayerStr == "One" then PlayerId.One else PlayerId.Two
      
      val stonesHand1 = (json \ "stonesInHand1").as[Int]
      val stonesHand2 = (json \ "stonesInHand2").as[Int]
      val bSize = (json \ "boardSize").as[Int]
      
      var baseBoard = BoardComponent.create(bSize)
      val stonesArray = (json \ "stones").as[JsArray].value
      
      var count1 = 0
      var count2 = 0
      
      for (stoneJson <- stonesArray) do
        val r = (stoneJson \ "row").as[Int]
        val c = (stoneJson \ "col").as[Int]
        val pStr = (stoneJson \ "player").as[String]
        val pId = if pStr == "One" then PlayerId.One else PlayerId.Two
        
        if pId == PlayerId.One then count1 += 1 else count2 += 1
        
        val matchedPos = baseBoard.allPositions.find(p => baseBoard.posCoords(p) == (r, c))
        matchedPos match
          case Some(pos) => baseBoard = baseBoard.placeStone(pos, pId).getOrElse(baseBoard)
          case None =>
          
      val p1 = PlayerComponent.create(
        PlayerId.One,
        stonesInHand = stonesHand1,
        stonesOnBoard = count1
      )
      val p2 = PlayerComponent.create(
        PlayerId.Two,
        stonesInHand = stonesHand2,
        stonesOnBoard = count2
      )
      
      GameComponent.create(baseBoard, p1, p2, currentPlayer)