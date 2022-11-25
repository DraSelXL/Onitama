package com.example.onitama.AI

import com.example.onitama.GameActivity
import com.example.onitama.components.*

/**
 * A class that holds an evaluation function to determine a player's advantage.
 */
class BoardEvaluator {
    companion object {
        var MAX_PLY = 2 // The maximum ply for the AI to compute
        var SBE_VALUES = arrayListOf<Float>()
        var AI_COLOR = PlayerColor.BLUE

        fun evaluate(board: Board, player: Player, enemy: Player, playerColor: String, storedCard: Card, ply: Int = -1)  {
            nextPly(board, player, enemy, playerColor, storedCard, ply + 1)
        }

        private fun nextPly(board: Board, player: Player, enemy: Player, playerColor: String, storedCard: Card, ply: Int) {
            if (ply == MAX_PLY) {
                for (i in board.blocks.indices) {
                    for (j in board.blocks[i].indices) {
                        // If there is a piece in the block, start the SBE
                        var blockPiece = board.blocks[i][j].piece

                        // Different SBE for different piece type
                        if (blockPiece?.type == Piece.PAWN) {

                        }
                        else if (blockPiece?.type == Piece.MASTER) {

                        }
                    }
                }
            }
            else {
                var tempPlayer = player.copy()
                var tempEnemy = enemy.copy()

                // Check every block for a piece
                for (i in board.blocks.indices) {
                    for (j in board.blocks[i].indices) {
                        if (board.blocks[i][j].piece?.color == playerColor) {

                            // If there is a piece in the block, move the piece
                            var blockPiece = board.blocks[i][j].piece
                            if (blockPiece != null) {
                                // If the piece is not the player's piece, go to the next block
                                if (blockPiece.color != playerColor) {
                                    continue
                                }

                                // Make a move until the last Ply
                                if (blockPiece.color == player.color) {
                                    // Check every move from the player's card
                                    for (cardIndex in player.cards.indices) {
                                        val card = player.cards[cardIndex]

                                        for (newPosition in card.possibleMoves) {
                                            // Check if the next move is a win condition or loss condition
                                            var targetBlockPiece = board.getBlockPiece(newPosition)
                                            if (targetBlockPiece != null && targetBlockPiece.type == Piece.MASTER && targetBlockPiece.color != AI_COLOR) {
                                                SBE_VALUES.add(Float.POSITIVE_INFINITY)
                                                return
                                            }
                                            else if (targetBlockPiece != null && targetBlockPiece.type == Piece.MASTER && targetBlockPiece.color == AI_COLOR) {
                                                SBE_VALUES.add(Float.NEGATIVE_INFINITY)
                                                return
                                            }

                                            var tempBoard = Board(board)
                                            tempBoard.movePiece(Coordinate(i, j), newPosition)

                                            // Switch player card
                                            var temp = tempPlayer.cards[cardIndex].copy()
                                            tempPlayer.cards[cardIndex] = storedCard!!.copy()
                                            var tempStoredCard = temp

                                            // Go to next ply from the enemy perspective
                                            nextPly(tempBoard, tempPlayer, tempEnemy, tempEnemy.color, tempStoredCard, ply + 1)
                                        }
                                    }
                                }
                                else if (blockPiece.color == enemy.color) {
                                    // Check every move from the enemy's card
                                    for (cardIndex in enemy.cards.indices) {
                                        for (newPosition in enemy.cards[cardIndex].possibleMoves) {
                                            var tempBoard = Board(board)
                                            tempBoard.movePiece(Coordinate(i, j), newPosition)

                                            // Switch player card
                                            var temp = tempPlayer.cards[cardIndex].copy()
                                            tempPlayer.cards[cardIndex] = storedCard!!.copy()
                                            var tempStoredCard = temp

                                            // Go to next ply from the player perspective
                                            nextPly(tempBoard, tempPlayer, tempEnemy, tempPlayer.color, tempStoredCard, ply + 1)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}