package com.example.onitama.components

import android.graphics.Color
import android.util.Log
import com.example.onitama.GameActivity
import com.example.onitama.R

/**
 * A class representing the board of Onitama.
 * This class holds the pieces of each players and interacts with them.
 */
class Board(
    var redMaster: Piece? = null,                       // The piece representing the red player master piece.
    var blueMaster: Piece? = null,                      // The piece representing the blue player master piece.
    var redPieces: ArrayList<Piece> = arrayListOf(),    // The list holding the red player pieces.
    var bluePieces: ArrayList<Piece> = arrayListOf()    // The list holding the blue player pieces.
) {
    // Static variables
    companion object {
        var WIDTH = 5                                   // Indicates the width of the board.
        var HEIGHT = 5                                  // Indicates the height of the board.
        var RED_MASTER_BLOCK = Coordinate(2, 4)   // Indicates on which block is the Red Master Piece spawn.
        var BLUE_MASTER_BLOCK = Coordinate(2, 0)  // Indicates on which block is the Blue Master Piece spawn.

        fun print(board: Board) {
            for (row in 0 until HEIGHT) {
                var rowString = ""
                for (column in 0 until WIDTH) {
                    val blockPiece = board.getPiece(Coordinate(column, row))
                    rowString += if (blockPiece == null) {
                        "| "
                    } else {
                        if (blockPiece.color == PlayerColor.BLUE) {
                            "|O"
                        } else "|X"
                    }
                }
                Log.d("BOARD", rowString)
            }
        }
    }

    /**
     * ### A secondary constructor of the Board object.
     * Performs a deep copy of the argument Board object.
     *
     * @param oldBoard The board to deep copy.
     */
    constructor(oldBoard: Board) : this() {
        redMaster = if (oldBoard.redMaster != null) Piece(oldBoard.redMaster!!) else null
        blueMaster = if (oldBoard.blueMaster != null) Piece(oldBoard.blueMaster!!) else null

        for (oldRedPiece in oldBoard.redPieces)
            redPieces.add(Piece(oldRedPiece))

        for (oldBluePiece in oldBoard.bluePieces)
            bluePieces.add(Piece(oldBluePiece))
    }

    // Methods
    /**
     * Checks the given argument coordinate for a valid position inside the board.
     * Throws an exception if a discrepancy is found.
     *
     * @param coordinate The coordinate that is going to be checked.
     */
    fun checkCoordinate(coordinate: Coordinate) {
        // Checks the X axis
        if (coordinate.x < 0 || coordinate.x >= WIDTH)
            throw Exception("The specified X axis coordinate is not valid! x= ${coordinate.x}")

        // Checks the Y axis
        else if (coordinate.y < 0 || coordinate.y >= HEIGHT)
            throw Exception("The specified Y axis coordinate is not valid! y = ${coordinate.y}")
    }

    /**
     * Initiates or resets the board with the starting pieces of each players.
     * Called whenever a new game is about to commence.
     */
    fun refresh() {
        // Initialize the master pieces.
        redMaster = Piece(PieceType.MASTER, R.drawable.ic_crown_red, PlayerColor.RED, Coordinate(RED_MASTER_BLOCK.x, RED_MASTER_BLOCK.y))
        blueMaster = Piece(PieceType.MASTER, R.drawable.ic_crown_blue, PlayerColor.BLUE, Coordinate(BLUE_MASTER_BLOCK.x, BLUE_MASTER_BLOCK.y))

        // Initialize the pawn pieces.
        redPieces.clear()
        bluePieces.clear()
        for (column in 0 until WIDTH) {
            if (column != RED_MASTER_BLOCK.x)
                redPieces.add(Piece(PieceType.PAWN, R.drawable.ic_pawn_red, PlayerColor.RED, Coordinate(column, RED_MASTER_BLOCK.y)))

            if (column != BLUE_MASTER_BLOCK.x)
                bluePieces.add(Piece(PieceType.PAWN, R.drawable.ic_pawn_blue, PlayerColor.BLUE, Coordinate(column, BLUE_MASTER_BLOCK.y)))
        }
    }

    /**
     * Search for a piece inside the board with the specified coordinate.
     *
     * @param coordinate The coordinate of the block in the board.
     *
     * @return The piece if the coordinate contains a piece, null otherwise.
     */
    fun getPiece(coordinate: Coordinate): Piece? {
        // Check whether the given coordinate is valid
        checkCoordinate(coordinate)

        // Check if the master piece is there.
        if (redMaster
                ?.position
                ?.x == coordinate.x &&
            redMaster
                ?.position
                ?.y == coordinate.y)
            return redMaster

        else if (blueMaster?.position?.x == coordinate.x && blueMaster?.position?.y == coordinate.y)
            return blueMaster

        // Search between red pawn pieces
        var foundPiece: Piece? = null
        var i = 0
        while(i < redPieces.size) {
            var tempPiece = redPieces[i]
            if (tempPiece.position.x == coordinate.x && tempPiece.position.y == coordinate.y) {
                foundPiece = tempPiece
                i = WIDTH // Exit out of the loop
            }

            i++
        }

        // Returns the found piece
        if (foundPiece != null) return foundPiece

        // Continue search in blue pawn pieces
        i = 0
        while(i < bluePieces.size) {
            var tempPiece = bluePieces[i]
            if (tempPiece.position.x == coordinate.x && tempPiece.position.y == coordinate.y) {
                foundPiece = tempPiece
                i = WIDTH // Exit out of the loop
            }

            i++
        }

        // Return result whether found or not
        return foundPiece
    }

    /**
     * Moves a piece within the board to the desired location.
     * If for some reason the target block to move the piece does not have a piece, throws an exception.
     *
     * @param oldPosition The position of the piece that is going to be moved.
     * @param newPosition The position of the target coordinate to move the piece.
     * @return The piece that has been captured if any, null otherwise.
     */
    fun movePiece(oldPosition: Coordinate, newPosition: Coordinate): Piece? {
        // Checks the parameter
        checkCoordinate(oldPosition)
        checkCoordinate(newPosition)

        // Get the piece in the specified coordinate
        var oldPiece: Piece = getPiece(oldPosition) ?: throw Exception("The coordinate does not have a piece!")
        var destinationPiece: Piece? = getPiece(newPosition)

        // Throws an exception if the move captures a friendly piece
        if (destinationPiece?.color == oldPiece.color) throw Exception("Attempting to capture a friendly piece!")

        // Deep copy the destination piece before moving any piece
        var capturedPiece = if (destinationPiece != null) Piece(destinationPiece) else null

        // Remove the captured piece from the list if there is any
        if (destinationPiece?.color == PlayerColor.RED)
            if (destinationPiece != redMaster) {
                redPieces.remove(destinationPiece)
            }
            else redMaster = null
        else if (destinationPiece?.color == PlayerColor.BLUE)
            if (destinationPiece != blueMaster) {
                bluePieces.remove(destinationPiece)
            }
            else blueMaster = null

        // Move the desired piece to the new coordinate
        oldPiece.moveTo(newPosition.x, newPosition.y)

        return capturedPiece
    }
}