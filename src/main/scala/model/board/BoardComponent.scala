package model.board

object BoardComponent:

//BoardComponent.create() gibt ein Board zurück nicht direkt MillBoard


  def create(boardSize: Int = 3): Board =
    impl.MillBoard(boardSize)