package com.example.onitama.components

class Piece(
    var type:String, //pawn or king
    var img:Int
) {
    // Static Variables
    companion object {
        var PAWN = "pawn"       // Indicates that the piece is of type "pawn"
        var MASTER = "master"   // Indicates that the piece is of type "master"
    }
}