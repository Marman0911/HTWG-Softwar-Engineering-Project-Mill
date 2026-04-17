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

0+--------------+--------------+
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
  //hallo hier manuel
  @main def millBC(): Unit =
  for n <- 1 to 3 do
    val board = MillBoard(n)
    println(s"=== Board Size $n ===")
    println(board.render)
    println()

case class MillBoard(boardSize: Int = 3):
  private val eol          = sys.props("line.separator")
  private val intersection = "+"
  private val step         = "-"
  private val hPath        = step * 4   // "----"
  private val vPath        = "|"
  private val nml          = " " * 4    // "    " (no man's land)

  // "+" + n×"----" + (n-1)×"-"  →  Länge = 5n Zeichen
  // z.B. n=1: "+----", n=2: "+---------", n=3: "+--------------"
  private def crossroad(n: Int): String =
    intersection + hPath * n + step * (n - 1)

  // Zeile 2k – waagrechte Linie auf Tiefe k
  // k äußere Rahmen  +  crossroad für verbleibende Segmente (×2)  +  Mittel-"+"
  private def horizRow(k: Int): String =
    (vPath + nml) * k +
    crossroad(boardSize - k) * 2 + intersection +
    (nml + vPath) * k

  // Zeile 2k+1 – senkrechte Zeile auf Tiefe k < boardSize-1
  // Hat eine Mittel-"|", weil äußere Quadrate dort hindurchgehen
  private def vertRowOuter(k: Int): String =
    val inner = " " * (5 * (boardSize - k) - 1) // Abstand zum Mittelpipe
    (vPath + nml) * k + vPath + inner + vPath + inner + vPath + (nml + vPath) * k

  // Zeile 2(boardSize-1)+1 – innerstes Quadrat, KEIN Mittel-"|"
  // Die Mitte ist leer (kein Spielfeld-Mittelpunkt bei Mühle)
  private def vertRowInner: String =
    val gap = " " * 9 // immer 9, entspricht dem Innenraum des innersten Quadrats
    (vPath + nml) * (boardSize - 1) + vPath + gap + vPath + (nml + vPath) * (boardSize - 1)

  // Mittelzeile – linker Arm + Lücke + rechter Arm (kein Mittel-"+")
  // z.B. n=3: "+----+----+         +----+----+"
  private def middleRow: String =
    intersection + (hPath + intersection) * (boardSize - 1) +
    " " * 9 +
    (intersection + hPath) * (boardSize - 1) + intersection

  def rows: Seq[String] =
    val topHalf: Seq[String] =
      (0 until boardSize).flatMap: k =>
        val h = horizRow(k)
        val v = if k < boardSize - 1 then vertRowOuter(k) else vertRowInner
        Seq(h, v)
    // Obere Hälfte  +  Mitte  +  untere Hälfte (= obere gespiegelt)
    topHalf ++ Seq(middleRow) ++ topHalf.reverse

  def render: String = rows.mkString(eol)