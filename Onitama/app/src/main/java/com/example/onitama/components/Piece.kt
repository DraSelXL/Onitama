package com.example.onitama.components

data class Piece(
    var type:String, //pawn or king
    var img:Int,
    var color: String
) {
    // Static Variables
    companion object {
        var PAWN = "pawn"       // Indicates that the piece is of type "pawn"
        var MASTER = "master"   // Indicates that the piece is of type "master"
    }

    var pos = Coordinate(-1, -1)
}