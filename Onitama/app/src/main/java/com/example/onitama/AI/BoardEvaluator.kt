package com.example.onitama.AI

import android.util.Log
import com.example.onitama.components.*
import java.lang.Math.abs
import kotlin.math.pow

/**
 * A class that holds static evaluation functions to determine a player's advantage over the other.
 */
class BoardEvaluator {
    companion object {
        val MAX_PLY = 3

        /**
         * Begin to predict all possible positions and calculate whether it is advantageous or not according to the AI.
         * ### Uses the Minimax, and Alpha-Beta Pruning algorithm.
         *
         * @param board The current state of the Onitama board.
         * @param redCards The cards that the current red player has.
         * @param blueCards The cards that the current blue player has.
         * @param storedCard The card that is not being hold by both players.
         * @param originalColor The player color that is calling the function. (The Max state of the Minimax algorithm)
         *
         * @return A MoveEvaluation object containing the best move and the best evaluation result.
         */
        fun evaluate(board: Board, redCards: Array<Card>, blueCards: Array<Card>, storedCard: Card, originalColor: PlayerColor): MoveEvaluation {
            var alpha = Double.NEGATIVE_INFINITY
            var beta = Double.POSITIVE_INFINITY

            val rootNode = OnitamaNode(board, redCards, blueCards, storedCard)
            val bestMove = nextPly(0, rootNode, originalColor, originalColor, alpha, beta)

            return bestMove
        }

        /**
         * Recursively called until a certain depth has been achieved. When a maximum depth has been achieved, calculated the SBE for the current state of the board.
         *
         * @param depth The depth the recursion is currently on.
         * @param node The node containing the current state of the game in the AI.
         * @param currentColor The color of the player stating whose turn to move in this depth.
         * @param originalColor The color of the player stating who first called the `evaluate()` function.
         * @param alpha The alpha value of the node to determine pruning, must be less than `beta`, otherwise prune.
         * @param beta The beta value of the node to determine pruning, must be more than `alpha`, otherwise prune.
         *
         * @return A MoveEvaluation object with the highest evaluation score.
         */
        fun nextPly(depth: Int, node: OnitamaNode, currentColor: PlayerColor, originalColor: PlayerColor, alpha: Double, beta: Double): MoveEvaluation {
            var winEval = checkWinLossCondition(depth, node, currentColor, originalColor, alpha, beta)
            if (winEval != null) {
                return winEval
            }

            // Checks whether the depth has reach the maximum depth allowed
            if (depth < MAX_PLY) {
                // Recursively generate children nodes and go to the next depth
                val childrenNodes = generateChildren(node, currentColor)

                // Prepare a list to contain all valid evaluations
                val evaluations = arrayListOf<MoveEvaluation>()

                // Iterate between all available child nodes and retrieve the evaluation values
                var currentAlpha = alpha
                var currentBeta = beta
                for (childNode in childrenNodes) {
                    if (currentColor == PlayerColor.RED) { // Player (Minimizing)
                        // Recurse to the next depth of the ply and get the beta value
                        val moveEvaluation = nextPly(depth+1, childNode, PlayerColor.BLUE, originalColor, currentAlpha, currentBeta)

                        Log.d("ALPHA-BETA", "Alpha: ${moveEvaluation.alpha}, Beta: ${moveEvaluation.beta}")

                        // Compare which beta value is smaller
                        currentBeta = if (moveEvaluation.evaluation < currentBeta) moveEvaluation.evaluation else currentBeta

                        // Attach the Alpha-Beta value to the evaluations
                        moveEvaluation.alpha = currentAlpha
                        moveEvaluation.beta = currentBeta

                        // Add the evaluation to the list of evaluations
                        evaluations.add(moveEvaluation)
                    }
                    else if (currentColor == PlayerColor.BLUE) { // AI (Maximizing)
                        // Recurse to the next depth of the ply and get the alpha value
                        val moveEvaluation = nextPly(depth+1, childNode, PlayerColor.RED, originalColor,currentAlpha, currentBeta)

                        Log.d("ALPHA-BETA", "Alpha: ${moveEvaluation.alpha}, Beta: ${moveEvaluation.beta}")

                        // Compare which beta value is smaller
                        currentAlpha = if (moveEvaluation.evaluation > currentAlpha) moveEvaluation.evaluation else currentAlpha

                        // Attach the Alpha-Beta value to the evaluations
                        moveEvaluation.alpha = currentAlpha
                        moveEvaluation.beta = currentBeta

                        // Add the evaluation to the list of evaluations
                        evaluations.add(moveEvaluation)
                    }

                    if (currentAlpha >= currentBeta) {
                        Log.d("ALPHA-BETA", "nextPly: PRUNE")
                        break
                    }
                }

                var bestEval = evaluations[0]
                for (evaluation in evaluations) {
                    if (bestEval.evaluation < evaluation.evaluation)
                        bestEval = evaluation
                }

                if (node.originPosition != null && node.cardUsedIndex != null && node.moveUsedIndex != null ) {
                    return MoveEvaluation(node.originPosition!!, node.cardUsedIndex!!, node.moveUsedIndex!!, bestEval.evaluation, currentAlpha, currentBeta)
                }
                else {
                    return MoveEvaluation(bestEval.originPosition, bestEval.cardUsedIndex, bestEval.moveUsedIndex, bestEval.evaluation, currentAlpha, currentBeta)
                }
            }
            else {
                // Calculate the Static Board Evaluator
                return determineSBE(node, currentColor, originalColor, alpha, beta)
            }
        }

        /**
         * Create all possible piece move combinations from the parent node (The argument node), with the given player color.
         *
         * @param node The parent node to which all possible move will be generated.
         * @param currentColor The color of which player is making the move.
         *
         * @return An ArrayList of OnitamaNode object, storing the next state of the game.
         */
        fun generateChildren(node: OnitamaNode, currentColor: PlayerColor): ArrayList<OnitamaNode> {
            val childrenNodes = arrayListOf<OnitamaNode>()

            // Get the cards from the current player making the move.
            val cardList = if (currentColor == PlayerColor.BLUE) node.blueCards else node.redCards

            // Perform a deep copy of the piece list
            val pieceList = arrayListOf<Piece>()
            val tempPieceList = if (currentColor == PlayerColor.BLUE) node.board.bluePieces else node.board.redPieces
            for (i in tempPieceList.indices) {
                pieceList.add(Piece(tempPieceList[i]))
            }

            // Add the master piece to the list of pieces
            if (currentColor == PlayerColor.BLUE) {
                if (node.board.blueMaster != null) pieceList.add(node.board.blueMaster!!)
            }
            else if (currentColor == PlayerColor.RED) {
                if (node.board.redMaster != null) pieceList.add(node.board.redMaster!!)
            }

            // Iterate between all pieces
            for (pieceIndex in pieceList.indices) {
                // Iterate between all cards
                for (cardIndex in cardList.indices) {
                    val cardUsed = cardList[cardIndex]  // The current card being used

                    // Iterate between all moves
                    for (moveIndex in cardUsed.possibleMoves.indices) {
                        val moveUsed = cardUsed.possibleMoves[moveIndex]    // The current move being used

                        val currentPiece = Piece(pieceList[pieceIndex])     // The current piece being used

                        // Create a deep copy of the node
                        val childNode = OnitamaNode(node)

                        // Store the new position separately to use in the `movePiece()` function in the board object
                        val newX = currentPiece.position.x + moveUsed.x * if (currentColor == PlayerColor.BLUE) -1 else 1
                        val newY = currentPiece.position.y + moveUsed.y * if (currentColor == PlayerColor.BLUE) -1 else 1

                        // Set the origin positions
                        childNode.originPosition = Coordinate(currentPiece.position.x, currentPiece.position.y)
                        childNode.cardUsedIndex = cardIndex
                        childNode.moveUsedIndex = moveIndex

                        // Move the piece to the new position, in a try and catch bracket, to prevent out of bounds and friendly capture
                        try {
                            // Move the piece and throws an exception if not a valid move
                            childNode.board.movePiece(currentPiece.position, Coordinate(newX, newY))
                        }
                        catch (e: Exception) {
                            // Continue to the next iteration when a not valid move is thrown
//                            Log.d("AI_MOVE_PLY", e.message!!)
                            continue
                        }

                        childNode.switchCard(cardIndex, currentColor)

                        Log.d("BOARD", "PieceIndex = ${pieceIndex}, cardUsed = ${cardIndex}, moveUsed = ${moveIndex}")
                        Board.print(childNode.board)

                        // Add the valid move to the childrenNodes list
                        childrenNodes.add(childNode)
                    }
                }
            }

            return childrenNodes
        }

        /**
         * Calculate the current board state to determine whether it is advantageous to the AI or the player.
         * Positive means it is advantages to the AI, while negative is advantageous to the player.
         *
         * @param node The node holding the current state of the board.
         * @param currentColor The color of the player making the move. (Alpha-Beta Pruning)
         * @param originalColor The color of the player calling the `evaluate()` function.
         * @param alpha The alpha value of the Alpha-Beta Pruning algorithm.
         * @param beta The beta value of the Alpha-Beta Pruning algorithm.
         */
        fun determineSBE(node: OnitamaNode, currentColor: PlayerColor, originalColor: PlayerColor, alpha: Double, beta: Double): MoveEvaluation {
            var totalEvaluation = 0.0

            val redMaster = node.board.redMaster!!
            val blueMaster = node.board.blueMaster!!

            var tempEvaluation = 0.0
            for (piece in node.board.bluePieces) {
                tempEvaluation = abs(redMaster.position.x - piece.position.x).toDouble().pow(2) * -1
                tempEvaluation += abs(redMaster.position.y - piece.position.y).toDouble().pow(2) * -1
            }
            totalEvaluation += tempEvaluation

            return MoveEvaluation(
                node.originPosition!!,
                node.cardUsedIndex!!,
                node.moveUsedIndex!!,
                totalEvaluation,
                alpha,
                beta
            )
        }

        fun checkWinLossCondition(depth: Int, node: OnitamaNode, currentColor: PlayerColor, originalColor: PlayerColor, alpha: Double, beta: Double): MoveEvaluation? {
            var tempEval: MoveEvaluation? = null

            val winEval = if (depth <= 1) Double.POSITIVE_INFINITY else Double.MAX_VALUE - depth * 10
            val lossEval = if (depth <= 1) Double.NEGATIVE_INFINITY else Double.MIN_VALUE + depth * 10

            if (node.board.redMaster == null ||
                (node.board
                    .blueMaster
                    ?.position
                    ?.x == Board.RED_MASTER_BLOCK.x &&
                    node.board
                        .blueMaster
                        ?.position
                        ?.y == Board.RED_MASTER_BLOCK.y)) {
                tempEval = MoveEvaluation(
                    node.originPosition!!,
                    node.cardUsedIndex!!,
                    node.moveUsedIndex!!,
                    winEval,
                    if (currentColor == PlayerColor.BLUE) winEval else alpha,
                    if (currentColor == PlayerColor.BLUE) beta else winEval,
                )
            }
            else if (node.board.blueMaster == null ||
                (node.board.redMaster?.position?.x == Board.BLUE_MASTER_BLOCK.x &&
                        node.board.redMaster?.position?.y == Board.BLUE_MASTER_BLOCK.y)) {
                tempEval = MoveEvaluation(
                    node.originPosition!!,
                    node.cardUsedIndex!!,
                    node.moveUsedIndex!!,
                    lossEval,
                    if (currentColor == PlayerColor.BLUE) lossEval else alpha,
                    if (currentColor == PlayerColor.BLUE) beta else lossEval,
                )
            }

            return tempEval
        }
    }
}
