package view.gui

import controller.{GameObserver, IController}

import scala.swing.*
import scala.util.{Failure, Success}
import java.awt.{BasicStroke, Color, Dimension, Font, Graphics2D, RenderingHints}
import java.awt.event.{ActionEvent, ActionListener, MouseAdapter, MouseEvent, WindowAdapter, WindowEvent}
import java.util.concurrent.CountDownLatch
import javax.swing.{Timer, WindowConstants}
import com.google.inject.Inject

/**
 * GUI-View für Mühle.
 *
 * Sie verwendet ausschließlich das IController-Interface und erzeugt
 * keinen eigenen Controller. Dadurch bleibt sie mit Guice und dem
 * gemeinsamen Controller von GUI und TUI kompatibel.
 */
final class MillGui @Inject () (private val controller: IController) extends GameObserver:

  private val closed = CountDownLatch(1)

  private enum GuiAction:
    case Place, Move, Remove

  private def bigButton(text: String): Button =
    new Button(text):
      font = new Font("Arial", Font.BOLD, 20)
      preferredSize = new Dimension(320, 50)

  private val playButton = bigButton("Play")
  private val quitButtonMenu = bigButton("Quit")
  private val playerVsPlayerButton = bigButton("Spieler 1 gegen Spieler 2")
  private val playerVsAiButton = bigButton("Gegen KI spielen")
  private val aiVsAiButton = bigButton("KI gegen KI zuschauen")
  private val backToMenuButton = bigButton("Zurück")

  private val gameMenuButton = new Button("Menü")
  private val undoButtonGame = new Button("Undo")
  private val quitButtonGame = new Button("Quit")

  private val closeInstructionsButton = new Button("X"):
    font = new Font("Arial", Font.BOLD, 22)
    preferredSize = new Dimension(46, 38)

  private val startGameButton = bigButton("Spiel starten")

  private val modeMessageLabel = new Label(""):
    font = new Font("Arial", Font.BOLD, 16)
    foreground = Color.RED

  private val messageLabel = new Label("Klicke auf einen grünen Punkt."):
    font = new Font("Arial", Font.BOLD, 16)

  private val boardPanel = new MillBoardPanel
  private val turnPanel = new TurnPanel
  private val playerOneReservePanel = new StoneReservePanel(1)
  private val playerTwoReservePanel = new StoneReservePanel(2)

  private val mainMenuPanel = new BoxPanel(Orientation.Vertical):
    preferredSize = new Dimension(700, 720)
    border = Swing.EmptyBorder(40, 10, 40, 10)

    contents += Swing.VStrut(130)
    contents += new FlowPanel(new Label("Mühle"):
      font = new Font("Arial", Font.BOLD, 48)
    )
    contents += Swing.VStrut(60)
    contents += new FlowPanel(playButton)
    contents += Swing.VStrut(20)
    contents += new FlowPanel(quitButtonMenu)

  private val modeMenuPanel = new BoxPanel(Orientation.Vertical):
    preferredSize = new Dimension(700, 720)
    border = Swing.EmptyBorder(40, 10, 40, 10)

    contents += Swing.VStrut(80)
    contents += new FlowPanel(new Label("Spielmodus wählen"):
      font = new Font("Arial", Font.BOLD, 38)
    )
    contents += Swing.VStrut(50)
    contents += new FlowPanel(playerVsPlayerButton)
    contents += Swing.VStrut(20)
    contents += new FlowPanel(playerVsAiButton)
    contents += Swing.VStrut(20)
    contents += new FlowPanel(aiVsAiButton)
    contents += Swing.VStrut(30)
    contents += new FlowPanel(modeMessageLabel)
    contents += Swing.VStrut(20)
    contents += new FlowPanel(backToMenuButton)

  private val instructionTopPanel = new BorderPanel:
    layout += closeInstructionsButton -> BorderPanel.Position.East

  private def instructionLine(text: String): Label =
    new Label(text):
      font = new Font("Arial", Font.PLAIN, 18)

  private val instructionBody = new BoxPanel(Orientation.Vertical):
    border = Swing.EmptyBorder(20, 45, 40, 45)
    background = new Color(235, 205, 135)

    contents += new FlowPanel(new Label("SPIELANLEITUNG"):
      font = new Font("Arial", Font.BOLD, 34)
    )
    contents += Swing.VStrut(25)
    contents += new FlowPanel(instructionLine("Setze abwechselnd deine Steine auf freie Punkte des Spielbretts."))
    contents += Swing.VStrut(15)
    contents += new FlowPanel(instructionLine("Drei Steine in einer Reihe bilden eine Mühle."))
    contents += Swing.VStrut(15)
    contents += new FlowPanel(instructionLine("Nach einer Mühle darfst du einen gegnerischen Stein entfernen."))
    contents += Swing.VStrut(15)
    contents += new FlowPanel(instructionLine("Nach dem Setzen ziehst du einen eigenen Stein auf einen freien Nachbarpunkt."))
    contents += Swing.VStrut(15)
    contents += new FlowPanel(instructionLine("Bei genau drei Steinen darfst du auf jeden freien Punkt springen."))
    contents += Swing.VStrut(15)
    contents += new FlowPanel(instructionLine("Du gewinnst, wenn der Gegner nur noch zwei Steine hat oder nicht ziehen kann."))
    contents += Swing.VStrut(35)
    contents += new FlowPanel(startGameButton)

  private val instructionPanel = new BorderPanel:
    preferredSize = new Dimension(900, 720)
    background = new Color(20, 70, 20)
    layout += instructionTopPanel -> BorderPanel.Position.North
    layout += instructionBody -> BorderPanel.Position.Center

  private val boardWithReservesPanel = new BoxPanel(Orientation.Horizontal):
    contents += playerOneReservePanel
    contents += Swing.HStrut(12)
    contents += boardPanel
    contents += Swing.HStrut(12)
    contents += playerTwoReservePanel

  private val gamePanel = new BoxPanel(Orientation.Vertical):
    preferredSize = new Dimension(900, 720)
    border = Swing.EmptyBorder(10, 10, 10, 10)

    contents += new FlowPanel(new Label("Mühle"):
      font = new Font("Arial", Font.BOLD, 34)
    )
    contents += new FlowPanel(boardWithReservesPanel)
    contents += new FlowPanel(turnPanel)
    contents += new FlowPanel(messageLabel)
    contents += new FlowPanel(gameMenuButton, undoButtonGame, quitButtonGame)

  private val frame = new MainFrame:
    title = "Mühle"
    contents = mainMenuPanel

  private val blinkTimer = new Timer(600, new ActionListener:
    override def actionPerformed(e: ActionEvent): Unit =
      boardPanel.switchGlow()
  )

  frame.peer.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)

  frame.peer.addWindowListener(new WindowAdapter:
    override def windowClosed(e: WindowEvent): Unit =
      blinkTimer.stop()
      closed.countDown()
  )

  playButton.peer.addActionListener(new ActionListener:
    override def actionPerformed(e: ActionEvent): Unit =
      showPanel(modeMenuPanel)
  )

  quitButtonMenu.peer.addActionListener(new ActionListener:
    override def actionPerformed(e: ActionEvent): Unit =
      frame.peer.dispose()
  )

  backToMenuButton.peer.addActionListener(new ActionListener:
    override def actionPerformed(e: ActionEvent): Unit =
      modeMessageLabel.text = ""
      showPanel(mainMenuPanel)
  )

  playerVsPlayerButton.peer.addActionListener(new ActionListener:
    override def actionPerformed(e: ActionEvent): Unit =
      startPlayerVsPlayer()
  )

  closeInstructionsButton.peer.addActionListener(new ActionListener:
    override def actionPerformed(e: ActionEvent): Unit =
      showGameBoard()
  )

  startGameButton.peer.addActionListener(new ActionListener:
    override def actionPerformed(e: ActionEvent): Unit =
      showGameBoard()
  )

  playerVsAiButton.peer.addActionListener(new ActionListener:
    override def actionPerformed(e: ActionEvent): Unit =
      modeMessageLabel.text = "KI kommt später. Dieser Modus ist vorbereitet."
  )

  aiVsAiButton.peer.addActionListener(new ActionListener:
    override def actionPerformed(e: ActionEvent): Unit =
      modeMessageLabel.text = "KI gegen KI kommt später. Dieser Modus ist vorbereitet."
  )

  gameMenuButton.peer.addActionListener(new ActionListener:
    override def actionPerformed(e: ActionEvent): Unit =
      showPanel(mainMenuPanel)
  )

  undoButtonGame.peer.addActionListener(new ActionListener:
    override def actionPerformed(e: ActionEvent): Unit =
      undoMove()
  )

  quitButtonGame.peer.addActionListener(new ActionListener:
    override def actionPerformed(e: ActionEvent): Unit =
      frame.peer.dispose()
  )

  boardPanel.peer.addMouseListener(new MouseAdapter:
    override def mouseClicked(e: MouseEvent): Unit =
      boardPanel.click(e.getX, e.getY)
  )

  private def currentGuiAction: GuiAction =
    val prompt = controller.currentPrompt.toLowerCase

    if prompt.contains("entfern") ||
        prompt.contains("remove") ||
        prompt.contains("gegnerischen stein") then
      GuiAction.Remove
    else
      val viewModel = controller.boardViewModel

      if viewModel.playerOneStonesInHand > 0 ||
          viewModel.playerTwoStonesInHand > 0 then
        GuiAction.Place
      else
        GuiAction.Move

  private def showPanel(panel: Component): Unit =
    frame.contents = panel
    frame.pack()
    frame.centerOnScreen()
    frame.peer.revalidate()
    frame.peer.repaint()

  private def startPlayerVsPlayer(): Unit =
    refresh()
    showPanel(instructionPanel)

  private def showGameBoard(): Unit =
    messageLabel.text = "Klicke auf einen grünen Punkt."
    refresh()
    showPanel(gamePanel)

  private def playMove(input: String): Boolean =
    val actionBefore = currentGuiAction

    val wasSuccessful =
      controller.handleInput(input) match
        case Failure(error) =>
          messageLabel.text = error.getMessage
          false

        case Success(_) =>
          if controller.isGameOver then
            messageLabel.text = "Spiel vorbei."

          else if currentGuiAction == GuiAction.Remove then
            messageLabel.text = "Mühle gebildet. Wähle einen gegnerischen Stein."

          else
            actionBefore match
              case GuiAction.Place =>
                messageLabel.text = "Stein gesetzt."
              case GuiAction.Move =>
                messageLabel.text = "Stein bewegt."
              case GuiAction.Remove =>
                messageLabel.text = "Gegnerischer Stein entfernt."

          true

    refresh()
    wasSuccessful

  private def undoMove(): Unit =
    controller.undo() match
      case Failure(error) =>
        messageLabel.text = error.getMessage
      case Success(_) =>
        messageLabel.text = "Zug rückgängig gemacht."

    refresh()

  private def refresh(): Unit =
    boardPanel.repaint()
    turnPanel.repaint()
    playerOneReservePanel.repaint()
    playerTwoReservePanel.repaint()

  override def update(): Unit =
    Swing.onEDT:
      refresh()

  def start(): Unit =
    controller.addObserver(this)
    refresh()

    Swing.onEDT:
      blinkTimer.start()
      showPanel(mainMenuPanel)
      frame.open()

    closed.await()

  private class StoneReservePanel(playerNumber: Int) extends Panel:

    preferredSize = new Dimension(120, 460)
    background = new Color(20, 70, 20)

    private def stonesInHand: Int =
      val viewModel = controller.boardViewModel

      if playerNumber == 1 then
        viewModel.playerOneStonesInHand
      else
        viewModel.playerTwoStonesInHand

    override def paintComponent(g: Graphics2D): Unit =
      super.paintComponent(g)

      g.setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON
      )

      g.setColor(new Color(15, 65, 15))
      g.fillRoundRect(8, 8, size.width - 16, size.height - 16, 20, 20)

      g.setColor(Color.WHITE)
      g.setFont(new Font("Arial", Font.BOLD, 16))

      val playerText =
        if playerNumber == 1 then "Spieler 1" else "Spieler 2"

      g.drawString(playerText, 20, 38)

      g.setFont(new Font("Arial", Font.PLAIN, 15))
      g.drawString(s"Rest: $stonesInHand", 28, 65)

      val stoneSize = 36

      (0 until stonesInHand).foreach: index =>
        val column = index % 2
        val row = index / 2
        val x = 22 + column * 48
        val y = 95 + row * 55

        if playerNumber == 1 then
          g.setColor(new Color(240, 230, 180))
        else
          g.setColor(new Color(20, 20, 20))

        g.fillOval(x, y, stoneSize, stoneSize)

        g.setColor(new Color(190, 190, 190))
        g.setStroke(new BasicStroke(2))
        g.drawOval(x, y, stoneSize, stoneSize)

  private class MillBoardPanel extends Panel:

    preferredSize = new Dimension(560, 460)
    background = new Color(20, 70, 20)

    private var glowOn = true

    // In der Bewegungsphase merkt sich die GUI den zuerst angeklickten Stein.
    private var selectedPoint: Option[(Int, Int)] = None

    private val points = Seq(
      (0, 0), (3, 0), (6, 0),
      (1, 1), (3, 1), (5, 1),
      (2, 2), (3, 2), (4, 2),
      (0, 3), (1, 3), (2, 3), (4, 3), (5, 3), (6, 3),
      (2, 4), (3, 4), (4, 4),
      (1, 5), (3, 5), (5, 5),
      (0, 6), (3, 6), (6, 6)
    )

    def switchGlow(): Unit =
      glowOn = !glowOn
      repaint()

    def click(mouseX: Int, mouseY: Int): Unit =
      pointAt(mouseX, mouseY) match
        case None =>
          messageLabel.text = "Bitte auf einen Punkt des Spielbretts klicken."

        case Some(point) =>
          currentGuiAction match
            case GuiAction.Place =>
              handlePlacingClick(point)
            case GuiAction.Remove =>
              handleRemovingClick(point)
            case GuiAction.Move =>
              handleMovingClick(point)

    private def handlePlacingClick(point: (Int, Int)): Unit =
      if occupiedPoints.contains(point) then
        messageLabel.text = "Bitte auf einen freien grünen Punkt klicken."
      else
        playMove(positionText(point))

    private def handleRemovingClick(point: (Int, Int)): Unit =
      selectedPoint = None

      playerAt(point) match
        case Some(playerNumber)
            if playerNumber != controller.boardViewModel.nextPlayerNumber =>
          playMove(positionText(point))

        case _ =>
          messageLabel.text = "Wähle einen gegnerischen Stein zum Entfernen."

      repaint()

    private def handleMovingClick(point: (Int, Int)): Unit =
      playerAt(point) match
        case Some(playerNumber)
            if playerNumber == controller.boardViewModel.nextPlayerNumber =>
          if selectedPoint.contains(point) then
            selectedPoint = None
            messageLabel.text = "Auswahl aufgehoben."
          else
            selectedPoint = Some(point)
            messageLabel.text = "Stein ausgewählt. Klicke nun auf ein freies Nachbarfeld."

          repaint()

        case Some(_) =>
          messageLabel.text = "Bitte wähle einen eigenen Stein aus."

        case None =>
          selectedPoint match
            case None =>
              messageLabel.text = "Wähle zuerst einen eigenen Stein aus."

            case Some(from) =>
              val moveInput = s"${positionText(from)} ${positionText(point)}"

              if playMove(moveInput) then
                selectedPoint = None

              repaint()

    private def positionText(point: (Int, Int)): String =
      GuiCoordinateMapper.toPosition(point._1, point._2)

    private def playerAt(point: (Int, Int)): Option[Int] =
      controller.boardViewModel.stones
        .find(stone => (stone.col / 5, stone.row / 2) == point)
        .map(_.playerNumber)

    private def occupiedPoints: Set[(Int, Int)] =
      controller.boardViewModel.stones
        .map(stone => (stone.col / 5, stone.row / 2))
        .toSet

    private def freePoints: Seq[(Int, Int)] =
      points.filterNot(occupiedPoints.contains)

    private def pointAt(mouseX: Int, mouseY: Int): Option[(Int, Int)] =
      points.find: point =>
        val dx = mouseX - pixelX(point._1)
        val dy = mouseY - pixelY(point._2)
        dx * dx + dy * dy <= 25 * 25

    private def boardLength: Int = 320

    private def startX: Int =
      (size.width - boardLength) / 2

    private def startY: Int =
      (size.height - boardLength) / 2

    private def pixelX(gridX: Int): Int =
      startX + gridX * boardLength / 6

    private def pixelY(gridY: Int): Int =
      startY + gridY * boardLength / 6

    override def paintComponent(g: Graphics2D): Unit =
      super.paintComponent(g)

      g.setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON
      )

      drawWood(g)
      drawLines(g)
      drawFreePoints(g)
      drawStones(g)

    private def drawWood(g: Graphics2D): Unit =
      g.setColor(new Color(196, 145, 82))
      g.fillRect(startX - 45, startY - 45, boardLength + 90, boardLength + 90)

      g.setColor(new Color(235, 205, 135))
      g.fillRect(startX - 25, startY - 25, boardLength + 50, boardLength + 50)

      g.setColor(new Color(110, 70, 30))
      g.setStroke(new BasicStroke(5))
      g.drawRect(startX - 45, startY - 45, boardLength + 90, boardLength + 90)

    private def drawLines(g: Graphics2D): Unit =
      g.setColor(new Color(40, 40, 40))
      g.setStroke(new BasicStroke(5))

      def line(from: (Int, Int), to: (Int, Int)): Unit =
        g.drawLine(pixelX(from._1), pixelY(from._2), pixelX(to._1), pixelY(to._2))

      val lines = Seq(
        ((0, 0), (3, 0)), ((3, 0), (6, 0)),
        ((6, 0), (6, 3)), ((6, 3), (6, 6)),
        ((6, 6), (3, 6)), ((3, 6), (0, 6)),
        ((0, 6), (0, 3)), ((0, 3), (0, 0)),

        ((1, 1), (3, 1)), ((3, 1), (5, 1)),
        ((5, 1), (5, 3)), ((5, 3), (5, 5)),
        ((5, 5), (3, 5)), ((3, 5), (1, 5)),
        ((1, 5), (1, 3)), ((1, 3), (1, 1)),

        ((2, 2), (3, 2)), ((3, 2), (4, 2)),
        ((4, 2), (4, 3)), ((4, 3), (4, 4)),
        ((4, 4), (3, 4)), ((3, 4), (2, 4)),
        ((2, 4), (2, 3)), ((2, 3), (2, 2)),

        ((3, 0), (3, 1)), ((3, 1), (3, 2)),
        ((6, 3), (5, 3)), ((5, 3), (4, 3)),
        ((3, 6), (3, 5)), ((3, 5), (3, 4)),
        ((0, 3), (1, 3)), ((1, 3), (2, 3))
      )

      lines.foreach(line)

    private def drawFreePoints(g: Graphics2D): Unit =
      freePoints.foreach: point =>
        val x = pixelX(point._1)
        val y = pixelY(point._2)

        if glowOn then
          g.setColor(new Color(80, 255, 80, 130))
          g.fillOval(x - 16, y - 16, 32, 32)

        g.setColor(new Color(0, 120, 20))
        g.fillOval(x - 9, y - 9, 18, 18)

        g.setColor(new Color(0, 80, 0))
        g.setStroke(new BasicStroke(2))
        g.drawOval(x - 9, y - 9, 18, 18)

    private def drawStones(g: Graphics2D): Unit =
      controller.boardViewModel.stones.foreach: stone =>
        val gridX = stone.col / 5
        val gridY = stone.row / 2
        val x = pixelX(gridX)
        val y = pixelY(gridY)
        val stoneSize = 38

        if stone.playerNumber == 1 then
          g.setColor(new Color(240, 230, 180))
        else
          g.setColor(new Color(20, 20, 20))

        g.fillOval(x - stoneSize / 2, y - stoneSize / 2, stoneSize, stoneSize)

        g.setColor(new Color(60, 60, 60))
        g.setStroke(new BasicStroke(2))
        g.drawOval(x - stoneSize / 2, y - stoneSize / 2, stoneSize, stoneSize)

        if selectedPoint.contains((gridX, gridY)) then
          g.setColor(new Color(220, 40, 40))
          g.setStroke(new BasicStroke(4))
          g.drawOval(
            x - stoneSize / 2 - 4,
            y - stoneSize / 2 - 4,
            stoneSize + 8,
            stoneSize + 8
          )

  private class TurnPanel extends Panel:

    preferredSize = new Dimension(560, 80)
    background = new Color(20, 70, 20)

    override def paintComponent(g: Graphics2D): Unit =
      super.paintComponent(g)

      g.setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON
      )

      g.setColor(new Color(15, 65, 15))
      g.fillRoundRect(70, 8, size.width - 140, 64, 20, 20)

      g.setColor(Color.WHITE)
      g.setFont(new Font("Arial", Font.BOLD, 24))

      val player = controller.boardViewModel.nextPlayerNumber
      g.drawString(s"Am Zug: Spieler $player", 130, 50)

      val tokenX = size.width - 150
      val tokenY = 40
      val tokenSize = 40

      if player == 1 then
        g.setColor(new Color(240, 230, 180))
      else
        g.setColor(Color.BLACK)

      g.fillOval(tokenX - tokenSize / 2, tokenY - tokenSize / 2, tokenSize, tokenSize)

      g.setColor(Color.WHITE)
      g.setStroke(new BasicStroke(2))
      g.drawOval(tokenX - tokenSize / 2, tokenY - tokenSize / 2, tokenSize, tokenSize)
