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
        var MASTER_BLOCK = 2    // Indicates on which column is the Master Piece spawn
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

    init {
        refresh()

        if (board != null) {
            blocks = board.blocks.clone()
        }
    }

    /**
     * Initiates or resets the board with the starting pieces of each players.
     * Should be called whenever a new game is about to commence.
     */
    fun refresh() {
        for (row in 0 until Board.HEIGHT) {
            for (column in 0 until Board.WIDTH) {
                if (row == 0) { // Is the current row the enemy's?
                    blocks[row][column].status = 1

                    if (column != Board.MASTER_BLOCK) { // Is the current column not the master's column?
                        blocks[row][column].occupier = Block.OCCUPY_ENEMY
                        blocks[row][column].piece = Piece(Piece.PAWN, R.drawable.ic_pawn_blue, PlayerColor.BLUE)
                    }
                    else { // Set the column with a master piece
                        blocks[row][column].occupier = Block.OCCUPY_ENEMY
                        blocks[row][column].piece = Piece(Piece.MASTER, R.drawable.ic_crown_blue, PlayerColor.BLUE)
                    }
                }
                else if (row == 4) { // Is the current row the player's row?
                    blocks[row][column].status = 1

                    if (column != 2) { // Is the current row not the master's column?
                        blocks[row][column].occupier = Block.OCCUPY_PLAYER
                        blocks[row][column].piece = Piece(Piece.PAWN, R.drawable.ic_pawn_red, PlayerColor.RED)
                    }
                    else { // Set the column with a master piece
                        blocks[row][column].occupier = Block.OCCUPY_PLAYER
                        blocks[row][column].piece = Piece(Piece.MASTER, R.drawable.ic_crown_red, PlayerColor.RED)
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

        var targetPiece = blocks[targetBlock.pos.y][targetBlock.pos.x].piece?.copy()

        // Set the block attributes
        blocks[newPosition.y][newPosition.x] = Block(oldBlock.status, newPosition, oldBlock.occupier, oldBlock.piece)
        Log.d("PIECE", "movePiece: " + blocks[newPosition.y][newPosition.x].piece.toString())

        // Reset the old block
        blocks[oldPosition.y][oldPosition.x].status = 0
        blocks[oldPosition.y][oldPosition.x].piece = null
        blocks[oldPosition.y][oldPosition.x].occupier = Block.OCCUPY_NONE

        return targetPiece
    }

    /**
     * Get the piece from the target block in the board.
     *
     * @param position The position of the block in the board to get the piece.
     */
    fun getBlockPiece(position: Coordinate): Piece? {
        return blocks[position.y][position.x].piece
    }
}