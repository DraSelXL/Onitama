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
            x -> Block(0, Coordinate(y, x))
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
        if (board != null) {
            blocks = board.blocks.clone()

            redMaster = board.redMaster
            redPieces = board.redPieces

            blueMaster = board.blueMaster
            bluePieces = board.bluePieces
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
                    blocks[row][column].status = 1

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
                    blocks[row][column].status = 1

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
                    blocks[row][column].status = 0
                    blocks[row][column].occupier = Block.OCCUPY_NONE
                    blocks[row][column].piece = null
                }
            }
        }
    }

    /**
     * Moves a piece within the board to the desired location.
     * If for some reason the target block to move the piece does not have a piece, throws an error.
     */
    fun movePiece(oldPosition: Coordinate, newPosition: Coordinate): Piece? {
        var targetBlock = blocks[newPosition.y][newPosition.x]
        var oldBlock = blocks[oldPosition.y][oldPosition.x]

        var targetPiece = blocks[newPosition.y][newPosition.x].piece?.copy()

        if (targetPiece != null) {
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

        // Set the block attributes
        blocks[newPosition.y][newPosition.x] = Block(oldBlock.status, newPosition, oldBlock.occupier, oldBlock.piece)
        blocks[newPosition.y][newPosition.x].piece?.pos = newPosition

        // Reset the old block
        blocks[oldPosition.y][oldPosition.x].status = 0
        blocks[oldPosition.y][oldPosition.x].piece = null
        blocks[oldPosition.y][oldPosition.x].occupier = Block.OCCUPY_NONE

        return targetPiece
    }
}