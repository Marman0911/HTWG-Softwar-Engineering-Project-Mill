@main def millBC(): Unit =
  val board = MillBoard(2)
  println("Welcome to Mill")
  println(board.square)
  println(board.hCoords.mkString("    ")) //makeString

/*
Every Square has 8 connection points
the paths to the center are always the same length
the border of square_n+1 is 2x the size
->playing field is 2^(n+1)
the print has to be from top to bottom 
for refference

+--------------+--------------+
|              |              |
|    +---------+---------+    |
|    |         |         |    |
|    |    +----+----+    |    |
|    |    |         |    |    |
+----+----+         +----+----+...
|    |    |         |    |    |
|    |    +----+----+    |    |
|    |         |         |    |
|    +---------+---------+    |
|              |              |
+--------------+--------------+
*/

case class Counter(value: Int):
  def tick: (Int, Counter) = (value, Counter(value - 1))

case class MillBoard(boardSize: Int = 3):
  //primitiv
  val eol = sys.props("line.separator") //end_of_line
  val intersection = "+"
  val step = "-"
  val hPath = step * 4
  val vPath = "|"
  val buffer = " "
  val nml = buffer * 4 //no_mans_land

  //building square
  //1. road with 3 crossings
  def crossroad(segments: Int): String = intersection + hPath * segments + step * (segments - 1)

  //2. road with only vPaths
  def longRode(segments: Int): String = vPath + nml * segments + buffer * (segments-1)

  //?
  def topLine: String    = crossroad(boardSize) * 2 + intersection + eol
  def middleLine: String = longRode(boardSize * 2) + eol

  //3. mixed
  def mixedRoad(innerSize: Int): String = vPath + nml * boardSize + crossroad(innerSize) + nml * boardSize + vPath + eol

  //completion
  def square: String = 
    topLine * 2

  //coords
  def hCoords: Array[Char] = ('a' to 'z').toArray.take(boardSize * 2 + 1) //https://www.javathinking.com/blog/better-way-to-generate-array-of-all-letters-in-the-alphabet/