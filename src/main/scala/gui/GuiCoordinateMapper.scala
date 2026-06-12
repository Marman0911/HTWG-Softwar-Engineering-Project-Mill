/* 
Es wird nicht komplette GUI getestet sondern nur die beim Anklicken 
umgewandelte Rasterposition
 */

package gui

object GuiCoordinateMapper:

  def toPosition(gridX: Int, gridY: Int): String =
    val letter = ('a' + gridX).toChar
    val number = gridY + 1
    s"$letter$number"