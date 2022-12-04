package com.example.onitama.components

/**
 * An object to hold the values of an Onitama piece.
 *
 * @constructor Sets the type, image resource id, color, and position of the piece.
 *
 * @param type The type of the piece.
 * @param image The image resource id of the piece.
 * @param color The color of the piece.
 * @param position The position of the piece on the board.
 */
class Piece(
    val type: PieceType,
    val image: Int,
    val color: PlayerColor,
    val position: Coordinate
) {
    /**
     * ### A secondary constructor of the Piece object.
     * Performs a deep copy of the argument Piece object.
     *
     * @param oldPiece The piece which is going to be deep copied.
     */
    constructor(oldPiece: Piece) : this(oldPiece.type, oldPiece.image, oldPiece.color, Coordinate(oldPiece.position.x, oldPiece.position.y))

    // Methods
    /**
     * Moves this piece to the desired coordinate.
     *
     * @param newX The new X axis position of this piece.
     * @param newY The new Y axis position of this piece.
     *
     * @return This piece after being moved.
     */
    fun moveTo(newX: Int, newY: Int): Piece {
        this.position.x = newX
        this.position.y = newY

        return this
    }
}