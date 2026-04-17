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

