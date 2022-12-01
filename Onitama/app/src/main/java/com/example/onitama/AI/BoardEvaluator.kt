package com.example.onitama.AI

import android.util.Log
import com.example.onitama.components.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * A class that holds static evaluation functions to determine a player's advantage over the other.
 */
class BoardEvaluator {
    companion object {
        var MAX_PLY = 4 // The maximum ply for the AI to compute

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
        suspend fun evaluate(board: Board, player: Player, enemy: Player, storedCard: Card, playerColor: String): MoveEvaluation {
            val rootNode = OnitamaNode(Board(board), player.cards, enemy.cards, storedCard) // Create a new node with a duplicate Board

            var beta = Double.POSITIVE_INFINITY
            var alpha = Double.NEGATIVE_INFINITY
            val bestMove = nextPly(0, rootNode, playerColor, playerColor, alpha, beta)

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
        private suspend fun nextPly(depth: Int, node: OnitamaNode, playerColor: String, originalColor: String, alpha: Double, beta: Double): MoveEvaluation {
            // Stop the iteration when a loss or win condition is met
            val winEval = checkWinLossCondition(node, originalColor, alpha, beta)
            if (winEval != null) return winEval

            if (depth < MAX_PLY) { // Generate child node and go to the next ply
                var childNodes = generateChildNodes(node, node.redCards, node.blueCards, node.storedCard, playerColor)

                var moveEvaluations = arrayListOf<MoveEvaluation>()
                if (playerColor == PlayerColor.RED) { //player (minimizing)
                    var childBeta = beta
                    var childAlpha = alpha
                    for (child in childNodes) {
                        var next = nextPly(depth+1, child, PlayerColor.BLUE, originalColor, childAlpha, childBeta)
                        childBeta = min(childBeta, next.evaluation.toDouble())
                        Log.d("alpha", "alpha: ${childAlpha}")
                        Log.d("beta", "beta: ${childBeta}")

                        if(childBeta <= childAlpha){
                            Log.d("Prune", "prune")
                            break
                        }
                        moveEvaluations.add(next)
                    }
                }
                else { //AI (maximizing)
                    var childBeta = beta
                    var childAlpha = alpha
                    for (child in childNodes) {
                        var next = nextPly(depth+1, child, PlayerColor.RED, originalColor, childAlpha, childBeta)
                        childAlpha = max(childAlpha, next.evaluation.toDouble())
                        Log.d("alpha", "alpha: ${childAlpha}")
                        Log.d("beta", "beta: ${childBeta}")

                        if(childBeta <= childAlpha){
                            Log.d("Prune", "prune")
                            break
                        }
                        moveEvaluations.add(next)
                    }
                }

                if (moveEvaluations.size > 0) { // Check if there is a move available in this scenario
                    // Search for the best move from the children move nodes
                    var bestMove: MoveEvaluation = moveEvaluations[0]
                    for (moveIndex in 1 until moveEvaluations.size) {
                        val tempMove = moveEvaluations[moveIndex]
                        if (bestMove.evaluation < tempMove.evaluation) {
                            bestMove = tempMove
                        }
                    }

                    if (node.originPosition == null)
                        return MoveEvaluation(bestMove.originPosition!!, bestMove.cardIndex, bestMove.cardMoveIndex, bestMove.evaluation, alpha, beta)
                    else
                        return MoveEvaluation(node.originPosition!!, node.previousCardUsedIndex!!, node.previousCardMoveUsedIndex!!, bestMove.evaluation, alpha, beta)
                }
                else return MoveEvaluation(node.originPosition!!, node.previousCardUsedIndex!!, node.previousCardMoveUsedIndex!!, 0f, alpha, beta)
            }
            else { // Calculate the SBE in the maximum depth of the node
                // Check for a win/loss condition
                val winLossEval = checkWinLossCondition(node, originalColor, alpha, beta)
                if (winLossEval != null) return winLossEval

                // Start the SBE
                var evaluation = 0f
                var evalModifier = if (originalColor == PlayerColor.RED) 1 else -1 // If the original color is from red player, make the eval positive for the red player

                // The closer to the enemy's master piece, the better.
                var tempEval = 0f
                for (bluePiece in node.board.bluePieces) {
                    tempEval += (Board.WIDTH-1) - abs(node.board.redMaster!!.pos.x - bluePiece.pos.x)
                    tempEval += (Board.HEIGHT-1) - abs(node.board.redMaster!!.pos.y - bluePiece.pos.y)
                }
                evaluation += ((tempEval * evalModifier * -1) * 20)

                //if a piece can reach the enemy's master piece
                tempEval = 0f
                for (bluePiece in node.board.bluePieces) {
                    tempEval += abs(node.board.redMaster!!.pos.x - bluePiece.pos.x)
                    tempEval += abs(node.board.redMaster!!.pos.y - bluePiece.pos.y)
                }
                if(tempEval == 2f){
                    if(evalModifier==1){
                        evaluation = Float.NEGATIVE_INFINITY
                    }
                    else{
                        evaluation = Float.POSITIVE_INFINITY
                    }
                }

                //the closer to an enemy piece
                tempEval = 0f
                for (bluePiece in node.board.bluePieces) {
                    for (redPiece in node.board.redPieces) {
                        tempEval += (Board.WIDTH-1) - abs(bluePiece.pos.x - redPiece.pos.x)
                        tempEval += (Board.HEIGHT-1) - abs(bluePiece.pos.y - redPiece.pos.y)
                    }
                }
                evaluation += ((tempEval * evalModifier * -1) * 10)

                // The closer your master to the enemy's temple, the better.
                tempEval = ((Board.WIDTH-1) - abs(node.board.blueMaster!!.pos.x - Board.RED_MASTER_BLOCK.x)).toFloat()
                tempEval += ((Board.HEIGHT-1) - abs(node.board.blueMaster!!.pos.y - Board.RED_MASTER_BLOCK.y)).toFloat()
                evaluation += ((tempEval * evalModifier * -1) * 10)

                // The closer to your master piece, the worse.
                tempEval = 0f
                for (redPiece in node.board.redPieces) {
                    tempEval += (Board.WIDTH-1) - abs(node.board.blueMaster!!.pos.x - redPiece.pos.x)
                    tempEval += (Board.HEIGHT-1) - abs(node.board.blueMaster!!.pos.y - redPiece.pos.y)
                }
                evaluation += ((tempEval * evalModifier) * 15)

                //if the enemy piece can reach our master piece
                tempEval = 0f
                for (bluePiece in node.board.redPieces) {
                    tempEval += abs(node.board.blueMaster!!.pos.x - bluePiece.pos.x)
                    tempEval += abs(node.board.blueMaster!!.pos.y - bluePiece.pos.y)
                }
                if(tempEval == 2f){
                    if(evalModifier==1){
                        evaluation = Float.POSITIVE_INFINITY
                    }
                    else{
                        evaluation = Float.NEGATIVE_INFINITY
                    }
                }

                // The closer your master to the enemy's temple, the better.
                tempEval = ((Board.WIDTH-1) - abs(node.board.redMaster!!.pos.x - Board.BLUE_MASTER_BLOCK.x)).toFloat()
                tempEval += ((Board.HEIGHT-1) - abs(node.board.redMaster!!.pos.y - Board.BLUE_MASTER_BLOCK.y)).toFloat()
                evaluation += ((tempEval * evalModifier) * 8)

                // Return the evaluation result
                return MoveEvaluation(
                    node.originPosition!!,
                    node.previousCardUsedIndex!!,
                    node.previousCardMoveUsedIndex!!,
                    evaluation,
                    alpha,
                    beta
                )
            }
        }

        /**
         * Generate child nodes from the current node, red cards and blue cards, and the stored card.
         * The moved pieces are the ones that is the same color as the `playerColor` argument given.
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

            // Create a node for every card
            for (cardIndex in cards.indices) {
                val card = cards[cardIndex]

                for (cardMoveIndex in card.possibleMoves.indices) {
                    // Get the card move coordinates
                    val cardMove = Coordinate(card.possibleMoves[cardMoveIndex].y, card.possibleMoves[cardMoveIndex].x)

                    // Get the piece list to move
                    var tempPieceList = if (playerColor == PlayerColor.RED) node.board.redPieces else node.board.bluePieces

                    // Shallow copy the list to avoid mutating referenced array list
                    val pieceList = arrayListOf<Piece>()
                    for (piece in tempPieceList) {
                        pieceList.add(piece)
                    }

                    // Add the master piece to the list
                    if (playerColor == PlayerColor.RED) pieceList.add(node.board.redMaster!!)
                    else pieceList.add(node.board.blueMaster!!)

                    // Create a new set of card lists to avoid passing by reference
                    var newStoredCard = Card(card.name, card.img, card.color)
                    val newRedCards = Array<Card>(2) { Card("NaN", 0, "NaN") }
                    val newBlueCards = Array<Card>(2) { Card("NaN", 0, "NaN") }
                    if (playerColor == PlayerColor.RED) { // Swap the stored card with the used red card.
                        for (i in redCards.indices) {
                            if (i != cardIndex) newRedCards[i] = redCards[i]
                            else {
                                newStoredCard = newRedCards[i]
                                newRedCards[i] = storedCard
                            }
                        }

                        for (i in blueCards.indices) {
                            newBlueCards[i] = blueCards[i]
                        }
                    }
                    else { // Swap the stored card with the used blue card.
                        for (i in blueCards.indices) {
                            if (i != cardIndex) newBlueCards[i] = blueCards[i]
                            else {
                                newStoredCard = newBlueCards[i]
                                newBlueCards[i] = storedCard
                            }
                        }

                        for (i in redCards.indices) {
                            newRedCards[i] = redCards[i]
                        }
                    }

                    // Iterate between pieces and move them
                    for (pieceIndex in pieceList.indices) {
                        val piece = pieceList[pieceIndex]

                        // Create a new child node (the piece has not been moved yet)
                        val newNode = OnitamaNode(Board(node.board), newRedCards, newBlueCards, newStoredCard)

                        // Prepare to move the piece
                        var moveModifier = if (playerColor == PlayerColor.RED) 1 else -1
                        val newPosition = Coordinate(piece.pos.y + cardMove.y * moveModifier, piece.pos.x + cardMove.x * moveModifier)

                        // Check whether the move is a valid one
                        if ((newPosition.y >= 0 && newPosition.y < Board.HEIGHT) && (newPosition.x >= 0 && newPosition.x < Board.WIDTH)) {
                            val destinationPiece = newNode.board.blocks[newPosition.y][newPosition.x].piece

                            // Check whether there's a piece in the destination and is not friendly
                            if (destinationPiece == null || destinationPiece?.color != playerColor) {
                                try {
                                    // Save the position before moving the piece
                                    val originPosition = Coordinate(piece.pos.y, piece.pos.x)

                                    // Move the piece
                                    newNode.board.movePiece(originPosition, newPosition)

                                    // Attach the saved origin position to the node
                                    newNode.originPosition = originPosition

                                    // Attach the card used for this move to the node
                                    newNode.previousCardUsedIndex = cardIndex

                                    // Attach the card move index for this move to the node
                                    newNode.previousCardMoveUsedIndex = cardMoveIndex
                                }
                                catch (e: Exception) {
                                    // If for whatever reason the above code failed, skip adding it to the child node list
                                    continue
                                }

                                // add the node to the list of children nodes
                                childNodes.add(newNode)
                            }
                        }
                    }
                }
            }

            return childNodes
        }

        /**
         * Checks for a win or a loss condition while iterating the ply.
         *
         * @param node The node of the state of the game that is going to be examined.
         * @param originalColor The color of the player who's called the AI's evaluate function.
         * @return Null if a condition is not met, A move evaluation otherwise.
         */
        private fun checkWinLossCondition(node: OnitamaNode, originalColor: String, alpha: Double, beta: Double): MoveEvaluation? {
            // Check for a win or loss condition first
            if (originalColor == PlayerColor.BLUE) { // Win/Loss condition for the blue player
                // If the red player master has been eaten or their temple occupied by blue master, is a win condition
                if (node.board.redMaster == null ||
                    (node.board.blueMaster?.pos == Board.RED_MASTER_BLOCK)) {
                    return MoveEvaluation(
                        node.originPosition!!,
                        node.previousCardUsedIndex!!,
                        node.previousCardMoveUsedIndex!!,
                        Float.POSITIVE_INFINITY,
                        alpha,
                        beta
                    )
                }
                // If the blue player master has been eaten or blue temple occupied by red master, is a loss condition
                else if (node.board.blueMaster == null ||
                    (node.board.redMaster?.pos == Board.BLUE_MASTER_BLOCK)) {
                    return MoveEvaluation(
                        node.originPosition!!,
                        node.previousCardUsedIndex!!,
                        node.previousCardMoveUsedIndex!!,
                        Float.NEGATIVE_INFINITY,
                        alpha,
                        beta
                    )
                }
            }
            else { // Win/Loss condition for the red player
                // If the red player master has been eaten, is a loss condition
                if (node.board.blueMaster == null ||
                    (node.board.redMaster?.pos == Board.BLUE_MASTER_BLOCK)) {
                    return MoveEvaluation(
                        node.originPosition!!,
                        node.previousCardUsedIndex!!,
                        node.previousCardMoveUsedIndex!!,
                        Float.POSITIVE_INFINITY,
                        alpha,
                        beta
                    )
                }
                // If the blue player master has been eaten, is a win condition
                else if (node.board.redMaster == null ||
                    node.board.blueMaster?.pos == Board.RED_MASTER_BLOCK) {
                    return MoveEvaluation(
                        node.originPosition!!,
                        node.previousCardUsedIndex!!,
                        node.previousCardMoveUsedIndex!!,
                        Float.NEGATIVE_INFINITY,
                        alpha,
                        beta
                    )
                }
            }

            // A win/loss condition is not met
            return null
        }
    }
}