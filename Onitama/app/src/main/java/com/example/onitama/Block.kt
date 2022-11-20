package com.example.onitama

class Block(
    //0 for empty
    var status:Int,
    var pos:Coordinate,
    var occupier:String = "none", //enemy or player
    var piece:Piece? = null,
) {
}