package model.fileio

import model.fileio.FileIOInterface
import model.game.{GameState, GameComponent}
import model.board.{Board, Position, BoardComponent}
import model.player.{Player, PlayerComponent, PlayerId}
import scala.util.{Try, Success, Failure}
import java.io.{File, PrintWriter}

class XmlFileIO extends FileIOInterface:

  override def save(state: GameState, filePath: String): Unit =
    val file = new File(filePath)
    if (file.getParentFile != null) file.getParentFile.mkdirs()
    
    val pw = new PrintWriter(file)
    
    val xml =
      <game>
        <currentPlayer>{state.currentPlayer.toString}</currentPlayer>
        <stonesInHand1>{state.player1.stonesInHand}</stonesInHand1>
        <stonesInHand2>{state.player2.stonesInHand}</stonesInHand2>
        <boardSize>{state.board.boardSize}</boardSize>
        <board>
          {state.board.placedStones.map((pos, playerId) => 
            val (r, c) = state.board.posCoords(pos)
            <stone>
              <row>{r}</row>
              <col>{c}</col>
              <player>{playerId.toString}</player>
            </stone>
          )}
        </board>
      </game>

    pw.write(xml.toString)
    pw.close()
    println(s"Erfolgreich als XML gespeichert unter: $filePath")

  override def load(filePath: String): Try[GameState] =
    Try:
      val fileXML = scala.xml.XML.loadFile(filePath)
      
      val currentPlayerStr = (fileXML \ "currentPlayer").text
      val currentPlayer = if currentPlayerStr == "One" then PlayerId.One else PlayerId.Two
      
      val stonesHand1 = (fileXML \ "stonesInHand1").text.toInt
      val stonesHand2 = (fileXML \ "stonesInHand2").text.toInt
      val bSize = (fileXML \ "boardSize").text.toInt
      
      var baseBoard = BoardComponent.create(bSize)
      val stonesXml = (fileXML \ "board" \ "stone")
      
      var count1 = 0
      var count2 = 0
      
      for (stone <- stonesXml) do
        val r = (stone \ "row").text.toInt
        val c = (stone \ "col").text.toInt
        val pStr = (stone \ "player").text
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