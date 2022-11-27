package com.example.onitama.AI

import com.example.onitama.GameActivity
import com.example.onitama.components.*
import kotlin.math.abs

/**
 * A class that holds static evaluation functions to determine a player's advantage over the other.
 */
class BoardEvaluator {
    companion object {
        var MAX_PLY = 3 // The maximum ply for the AI to compute

        /**
         * Start evaluating the available moves to determine the best move the player should take next.
         *
         * Uses the Minimax algorithm to evaluate the best move.
         *
         * @param board The board to be used as the rootNode for the evaluator.
         * @param player The player object which usually is the Red Player.
         * @param enemy The player object which is usually the Blue Player.
         * @param storedCard The card that is being stored.
         * @param playerColor The current player turn in the ply.
         * @return A MoveEvaluation class which represents the best move to be taken.
         */
        fun evaluate(board: Board, player: Player, enemy: Player, storedCard: Card, playerColor: String): MoveEvaluation {
            val rootNode = OnitamaNode(Board(board), player.cards, enemy.cards, storedCard) // Create a new node with a duplicate Board

            val bestMove = nextPly(0, rootNode, playerColor, playerColor)

            return bestMove
        }

        /**
         * Recursively goes to the next iteration of the evaluation until the maximum depth of the iteration is achieved or a solution is found.
         *
         * @param depth The depth of the iteration, should be increased each time a new iteration has occurred.
         * @param node The state node of the game in this depth.
         * @param playerColor The player's turn to play the game.
         * @param originalColor The original player's turn that called the evaluation function.
         */
        private fun nextPly(depth: Int, node: OnitamaNode, playerColor: String, originalColor: String): MoveEvaluation {
            // Stop the iteration when a loss or win condition is met
            if (originalColor == PlayerColor.BLUE) {
                if (node.board.redMaster == null ||
                    (node.board.blueMaster?.pos == Board.RED_MASTER_BLOCK)) {
                    return MoveEvaluation(node.previousCardUsedIndex!!, node.previousCardMoveUsedIndex!!, Float.POSITIVE_INFINITY)
                }
                else if (node.board.blueMaster == null ||
                    (node.board.redMaster?.pos == Board.BLUE_MASTER_BLOCK)) {
                    return MoveEvaluation(node.previousCardUsedIndex!!, node.previousCardMoveUsedIndex!!, Float.NEGATIVE_INFINITY)
                }
            }
            else if (originalColor == PlayerColor.RED) {
                if (node.board.blueMaster == null ||
                    (node.board.redMaster?.pos == Board.BLUE_MASTER_BLOCK)) {
                    return MoveEvaluation(node.previousCardUsedIndex!!, node.previousCardMoveUsedIndex!!, Float.POSITIVE_INFINITY)
                }
                else if (node.board.redMaster == null ||
                        node.board.blueMaster?.pos == Board.RED_MASTER_BLOCK) {
                    return MoveEvaluation(node.previousCardUsedIndex!!, node.previousCardMoveUsedIndex!!, Float.NEGATIVE_INFINITY)
                }
            }

            if (depth < MAX_PLY) { // Generate child node and go to the next ply
                var childNodes = generateChildNodes(node, node.redCards, node.blueCards, node.storedCard, playerColor)

                var moveEvaluations = arrayListOf<MoveEvaluation>()
                if (playerColor == PlayerColor.RED) {
                    for (child in childNodes) {
                        moveEvaluations.add(nextPly(depth+1, child, PlayerColor.BLUE, playerColor))
                    }
                }
                else {
                    for (child in childNodes) {
                        moveEvaluations.add(nextPly(depth+1, child, PlayerColor.RED, playerColor))
                    }
                }

                // Search for the best move from the children move nodes
                var bestMove: MoveEvaluation = moveEvaluations[0]
                for (moveIndex in 1 until moveEvaluations.size) {
                    val tempMove = moveEvaluations[moveIndex]
                    if (bestMove.evaluation < tempMove.evaluation) {
                        bestMove = tempMove
                    }
                }

                return bestMove
            }
            else { // Calculate the SBE in the maximum depth of the node
                var evaluation = 0f

                // The closer to the enemy's master piece, the better.
                var tempEvaluation = 0f;
                for (piece in node.board.bluePieces) {
                    tempEvaluation += Board.WIDTH - abs((piece.pos.x - node.board.redMaster!!.pos.x))
                    tempEvaluation += Board.HEIGHT - abs(piece.pos.y - node.board.redMaster!!.pos.y)
                }
                tempEvaluation *= 11
                evaluation += tempEvaluation

                // The closer the enemy's pieces is to your master, the worse.
                tempEvaluation = 0f
                for (piece in node.board.redPieces) {
                    tempEvaluation -= Board.WIDTH - abs(piece.pos.x - node.board.blueMaster!!.pos.x)
                    tempEvaluation -= Board.HEIGHT - abs(piece.pos.y - node.board.blueMaster!!.pos.y)
                }
                tempEvaluation *= 11
                evaluation += tempEvaluation

                // The closer your master to the enemy's temple, the better.
                tempEvaluation = (Board.WIDTH - (Board.RED_MASTER_BLOCK.x - node.board.blueMaster!!.pos.x)) +
                        (Board.HEIGHT - (Board.RED_MASTER_BLOCK.y - node.board.blueMaster!!.pos.y)) * 10f
                evaluation += tempEvaluation

                // The closer the enemy's master is to your temple, the worse.
                tempEvaluation = (Board.WIDTH - (Board.BLUE_MASTER_BLOCK.x - node.board.redMaster!!.pos.x)) +
                        (Board.HEIGHT - (Board.BLUE_MASTER_BLOCK.y - node.board.redMaster!!.pos.y)) * 10f
                evaluation += tempEvaluation

                return MoveEvaluation(node.previousCardUsedIndex!!, node.previousCardMoveUsedIndex!!, evaluation)
            }
        }

        /**
         * Generate the appropriate child nodes from the given argument node, card, and playerColor.
         *
         * @param node The parent node to generate the child from.
         * @param redCards The cards that is being hold by the red player.
         * @param blueCards The cards that is being hold by the blue player.
         * @param storedCard The card that is not being hold by either players.
         * @param playerColor The color of the player making the move.
         */
        private fun generateChildNodes(node: OnitamaNode, redCards: Array<Card>, blueCards: Array<Card>, storedCard: Card, playerColor: String): ArrayList<OnitamaNode> {
            val childNodes = arrayListOf<OnitamaNode>()

            // Use the cards that corresponds to the correct player turn
            val cards = if (playerColor == PlayerColor.RED) redCards else blueCards

            // Generate the child nodes from each card
            for (cardIndex in cards.indices) {
                val card = cards[cardIndex]

                // Generate the child nodes from each possible moves in the card
                for (cardMoveIndex in card.possibleMoves.indices) {
                    val newNode = OnitamaNode(Board(node.board), redCards, blueCards, storedCard)

                    val cardMove = card.possibleMoves[cardMoveIndex] // The move that is going to be taken

                    // Iterate the blocks to find pieces
                    for (i in newNode.board.blocks.indices) {
                        for (j in newNode.board.blocks[i].indices) {
                            val blockPiece = newNode.board.blocks[i][j].piece

                            // Check whether the block has a piece
                            if (blockPiece != null) {
                                // Check whether the piece is the correct player's piece
                                if (blockPiece.color == playerColor) {
                                    // Check whether the move is not out of bounds
                                    if (cardMove.x + j >= 0 && cardMove.x + j < Board.WIDTH &&
                                            cardMove.y + i >= 0 && cardMove.y + i < Board.HEIGHT) {
                                        var destinationBlock = newNode.board.blocks[i][j]

                                        // Check if the destination block has a friendly block
                                        if (destinationBlock.piece != null && destinationBlock.piece?.color != playerColor) {
                                            // Move the piece inside the board to the desired location
                                            newNode.board.movePiece(Coordinate(i, j), cardMove)

                                            // Switch the card after a move is made
                                            newNode.switchCard(cardIndex, playerColor)

                                            newNode.previousCardUsedIndex = cardIndex
                                            newNode.previousCardMoveUsedIndex = cardMoveIndex

                                            // Add the new board to the list of child nodes
                                            childNodes.add(newNode)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return childNodes
        }
    }
}