package com.example.onitama.components

/**
 * An enum class representing all available type of a piece.
 * Has a type property which holds the string value of the type.
 *
 */
enum class PieceType(val type: String) {
    PAWN("pawn"),       // Indicates that the piece type is a pawn piece.
    MASTER("master")    // Indicates that the piece type is a master piece.
}