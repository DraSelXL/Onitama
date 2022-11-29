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
    board: Board? = null
) {
    companion object {
        var WIDTH = 5           // Indicates the width of the board
        var HEIGHT = 5          // Indicates the height of the board
        var RED_MASTER_BLOCK = Coordinate(4, 2)     // Indicates on which block is the Red Master Piece spawn
        var BLUE_MASTER_BLOCK = Coordinate(0, 2)    // Indicates on which block is the Blue Master Piece spawn
    }

    /**
     * A two dimensional array that holds the values of the board.
     * Each element holds a Block class that can be accessed to see what's inside the Block.
     *
     * By default it has a width of 5 elements and a height of 5 elements.
     */
    var blocks: Array<Array<Block>> = Array(HEIGHT) {
        y -> Array(WIDTH) {
            x -> Block(Coordinate(y, x))
        }
    }

    /** The list of red pieces on the board. */
    var redMaster: Piece? = null
    /** The list of red pawn pieces on the board */
    var redPieces: ArrayList<Piece> = arrayListOf()

    /** The list of blue pieces on the board. */
    var blueMaster: Piece? = null
    /** The list of blue pawn pieces on the board */
    var bluePieces: ArrayList<Piece> = arrayListOf()

    init {
        if (board != null) { // Check whether a board argument is supplied to perform a deep copy
            // Deep copy the argument's blocks values
            for (row in board.blocks.indices) {
                for (column in board.blocks[row].indices) {
                    blocks[row][column].occupier = board.blocks[row][column].occupier
                    blocks[row][column].pos = board.blocks[row][column].pos.copy()
                    blocks[row][column].piece = board.blocks[row][column].piece?.copy()
                }
            }

            // Deep copy the argument's pieces values
            if (board.redMaster != null) {
                redMaster = Piece(board.redMaster!!.type, board.redMaster!!.img, board.redMaster!!.color)
                redMaster!!.pos = Coordinate(board.redMaster!!.pos.y, board.redMaster!!.pos.x)
            }
            for (i in board.redPieces.indices) {
                val oldPiece = board.redPieces[i]
                val newPiece = Piece(oldPiece.type, oldPiece.img, oldPiece.color)
                newPiece.pos = Coordinate(oldPiece.pos.y, oldPiece.pos.x)
                redPieces.add(newPiece)
            }

            if (board.blueMaster != null) {
                blueMaster = Piece(board.blueMaster!!.type, board.blueMaster!!.img, board.blueMaster!!.color)
                blueMaster!!.pos = Coordinate(board.blueMaster!!.pos.y, board.blueMaster!!.pos.x)
            }
            for (i in board.bluePieces.indices) {
                val oldPiece = board.bluePieces[i]
                val newPiece = Piece(oldPiece.type, oldPiece.img, oldPiece.color)
                newPiece.pos = Coordinate(oldPiece.pos.y, oldPiece.pos.x)
                bluePieces.add(newPiece)
            }
        }
        else {
            refresh()
        }
    }

    /**
     * Initiates or resets the board with the starting pieces of each players.
     * Called whenever a new game is about to commence.
     */
    fun refresh() {
        blueMaster = null
        redMaster = null

        bluePieces.clear()
        redPieces.clear()

        for (row in 0 until Board.HEIGHT) {
            for (column in 0 until Board.WIDTH) {
                if (row == Board.BLUE_MASTER_BLOCK.y) { // Is the current row the enemy's?

                    if (column != Board.BLUE_MASTER_BLOCK.x) { // Is the current column not the master's column?
                        blocks[row][column].occupier = Block.OCCUPY_ENEMY

                        // Create new piece
                        var piece = Piece(Piece.PAWN, R.drawable.ic_pawn_blue, PlayerColor.BLUE)
                        piece.pos = Coordinate(row, column)
                        bluePieces.add(piece)
                        blocks[row][column].piece = piece
                    }
                    else { // Set the column with a master piece
                        blocks[row][column].occupier = Block.OCCUPY_ENEMY

                        // Create new piece
                        var piece = Piece(Piece.MASTER, R.drawable.ic_crown_blue, PlayerColor.BLUE)
                        piece.pos = Coordinate(row, column)
                        blocks[row][column].piece = piece
                        blueMaster = piece
                    }
                }
                else if (row == Board.RED_MASTER_BLOCK.y) { // Is the current row the player's row?
                    if (column != Board.RED_MASTER_BLOCK.x) { // Is the current row not the master's column?
                        blocks[row][column].occupier = Block.OCCUPY_PLAYER

                        // Create new piece
                        var piece = Piece(Piece.PAWN, R.drawable.ic_pawn_red, PlayerColor.RED)
                        piece.pos = Coordinate(row, column)
                        redPieces.add(piece)
                        blocks[row][column].piece = piece
                    }
                    else { // Set the column with a master piece
                        blocks[row][column].occupier = Block.OCCUPY_PLAYER

                        // Create new piece
                        var piece = Piece(Piece.MASTER, R.drawable.ic_crown_red, PlayerColor.RED)
                        piece.pos = Coordinate(row, column)
                        blocks[row][column].piece = piece
                        redMaster = piece
                    }
                }
                else { // Sets the current block as an empty block
                    blocks[row][column].occupier = Block.OCCUPY_NONE
                    blocks[row][column].piece = null
                }
            }
        }
    }

    /**
     * Moves a piece within the board to the desired location.
     * If for some reason the target block to move the piece does not have a piece, throws an error.
     *
     * @param oldPosition The position of the piece that is going to be moved.
     * @param newPosition The position of the target coordinate to move the piece.
     * @return The piece that has been eaten.
     */
    fun movePiece(oldPosition: Coordinate, newPosition: Coordinate): Piece? {
        var targetBlock = blocks[newPosition.y][newPosition.x]
        var oldBlock = blocks[oldPosition.y][oldPosition.x]

        var targetPiece = targetBlock.piece

        if (targetPiece != null) {
            // Perform a deep copy of the gonna be eaten Piece
            targetPiece = Piece(targetPiece.type, targetPiece.img, targetPiece.color)
            targetPiece.pos = newPosition

            if (targetPiece.color == PlayerColor.RED) {
                if (targetPiece.type == Piece.PAWN) {
                    redPieces.remove(targetPiece)
                }
                else redMaster = null
            }
            else {
                if (targetPiece.type == Piece.PAWN) {
                    bluePieces.remove(targetPiece)
                }
                else blueMaster = null
            }
        }

        if (oldBlock.piece == null) {
            throw Exception("No piece in the block to move.")
        }

        // Set the block attributes
        blocks[newPosition.y][newPosition.x] = Block(Coordinate(newPosition.y, newPosition.x), oldBlock.occupier, oldBlock.piece)
        blocks[newPosition.y][newPosition.x].piece!!.pos = Coordinate(newPosition.y, newPosition.x)

        // Reset the old block
        blocks[oldPosition.y][oldPosition.x].piece = null
        blocks[oldPosition.y][oldPosition.x].occupier = Block.OCCUPY_NONE

        // Change the piece position attribute
        if (targetPiece?.color == PlayerColor.RED) {
            var redPiece = redPieces.find { piece -> piece.pos.x == oldBlock.pos.x && piece.pos.y == oldBlock.pos.y }

            if (redPiece != null) redPieces[redPieces.indexOf(redPiece)].pos = Coordinate(newPosition.y, newPosition.x)
        }
        else if (targetPiece?.color == PlayerColor.RED) {
            var bluePiece = bluePieces.find { piece -> piece.pos.x == oldBlock.pos.x && piece.pos.y == oldBlock.pos.y }

            if (bluePiece != null) bluePieces[bluePieces.indexOf(targetPiece)].pos = Coordinate(newPosition.y, newPosition.x)
        }

        return targetPiece
    }
}