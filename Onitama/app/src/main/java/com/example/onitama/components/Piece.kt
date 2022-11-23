package com.example.onitama.components

class Piece(
    var type: String,   // Pawn or Master
    var img: Int,       // The Image Resource of the piece
    var color: String   // The player color
) {
    // Static Variables
    companion object {
        var PAWN = "pawn"       // Indicates that the piece is of type "pawn"
        var MASTER = "master"   // Indicates that the piece is of type "master"
    }
}