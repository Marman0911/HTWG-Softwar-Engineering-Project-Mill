package model.board

//öffentliche Value Object, da andere Klassen wissen müssen Welche stelle am Board

case class Position(ring: Int, slot: Int)