package com.example.onitama.AI

import com.example.onitama.components.*
import kotlin.math.abs

/**
 * A class that holds static evaluation functions to determine a player's advantage over the other.
 */
class BoardEvaluator {
    companion object {
        var MAX_PLY = 1 // The maximum ply for the AI to compute

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
                    return MoveEvaluation(
                        node.originPosition!!,
                        node.previousCardUsedIndex!!,
                        node.previousCardMoveUsedIndex!!,
                        Float.POSITIVE_INFINITY
                    )
                }
                else if (node.board.blueMaster == null ||
                    (node.board.redMaster?.pos == Board.BLUE_MASTER_BLOCK)) {
                    return MoveEvaluation(
                        node.originPosition!!,
                        node.previousCardUsedIndex!!,
                        node.previousCardMoveUsedIndex!!,
                        Float.NEGATIVE_INFINITY
                    )
                }
            }
            else if (originalColor == PlayerColor.RED) {
                if (node.board.blueMaster == null ||
                    (node.board.redMaster?.pos == Board.BLUE_MASTER_BLOCK)) {
                    return MoveEvaluation(
                        node.originPosition!!,
                        node.previousCardUsedIndex!!,
                        node.previousCardMoveUsedIndex!!,
                        Float.POSITIVE_INFINITY
                    )
                }
                else if (node.board.redMaster == null ||
                        node.board.blueMaster?.pos == Board.RED_MASTER_BLOCK) {
                    return MoveEvaluation(
                        node.originPosition!!,
                        node.previousCardUsedIndex!!,
                        node.previousCardMoveUsedIndex!!,
                        Float.NEGATIVE_INFINITY
                    )
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

                if (childNodes.size > 0) { // Check if there is a move available in this scenario
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
                else return MoveEvaluation(node.originPosition!!, node.previousCardUsedIndex!!, node.previousCardMoveUsedIndex!!, 0f)
            }
            else { // Calculate the SBE in the maximum depth of the node
                // Check for a win or loss condition first
                if (originalColor == PlayerColor.BLUE) { // Win/Loss condition for the blue player
                    // If the red player master has been eaten or their temple occupied by blue master, is a win condition
                    if (node.board.redMaster == null ||
                        (node.board.blueMaster?.pos == Board.RED_MASTER_BLOCK)) {
                        return MoveEvaluation(
                            node.originPosition!!,
                            node.previousCardUsedIndex!!,
                            node.previousCardMoveUsedIndex!!,
                            Float.POSITIVE_INFINITY
                        )
                    }
                    // If the blue player master has been eaten or blue temple occupied by red master, is a loss condition
                    else if (node.board.blueMaster == null ||
                        (node.board.redMaster?.pos == Board.BLUE_MASTER_BLOCK)) {
                        return MoveEvaluation(
                            node.originPosition!!,
                            node.previousCardUsedIndex!!,
                            node.previousCardMoveUsedIndex!!,
                            Float.NEGATIVE_INFINITY
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
                            Float.POSITIVE_INFINITY
                        )
                    }
                    // If the blue player master has been eaten, is a win condition
                    else if (node.board.redMaster == null ||
                        node.board.blueMaster?.pos == Board.RED_MASTER_BLOCK) {
                        return MoveEvaluation(
                            node.originPosition!!,
                            node.previousCardUsedIndex!!,
                            node.previousCardMoveUsedIndex!!,
                            Float.NEGATIVE_INFINITY
                        )
                    }
                }

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

                return MoveEvaluation(
                    node.originPosition!!,
                    node.previousCardUsedIndex!!,
                    node.previousCardMoveUsedIndex!!,
                    evaluation
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
                    val pieceList = if (playerColor == PlayerColor.RED) node.board.redPieces else node.board.bluePieces

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
                                    newNode.board.movePiece(piece.pos, newPosition)

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

//            // Generate the child nodes from each card
//            for (cardIndex in cards.indices) {
//                val card = cards[cardIndex]
//
//                // Generate the child nodes from each possible moves in the card
//                for (cardMoveIndex in card.possibleMoves.indices) {
//                    val newNode = OnitamaNode(Board(node.board), redCards, blueCards, storedCard)
//
//                    val cardMove = card.possibleMoves[cardMoveIndex] // The move that is going to be taken
//
//                    // Get the pieces according to the player color
//                    var pieceList = if (playerColor == PlayerColor.RED) node.board.redPieces else node.board.bluePieces
//                    if (playerColor == PlayerColor.RED) // Add the master piece to the list
//                        pieceList.add(node.board.redMaster!!)
//                    else
//                        pieceList.add(node.board.blueMaster!!)
//
//                    // Iterate between pieces to move them
//                    var pieces = if (playerColor == PlayerColor.RED) node.board.redPieces else node.board.bluePieces
//                    var moveModifier = if (playerColor == PlayerColor.RED) 1 else -1
//
//                    for (pieceIndex in pieces.indices) {
//                        var piece = pieces[pieceIndex]
//                        var xPos = piece.pos.x
//                        var yPos = piece.pos.y
//                        val blockPiece = newNode.board.blocks[yPos][xPos].piece
//
//                        // Check whether the block has a piece
//                        if (blockPiece != null) {
//                            // Check whether the piece is the correct player's piece
//                            if (blockPiece.color == playerColor) {
//                                var newYPos = cardMove.y * moveModifier + yPos
//                                var newXPos = cardMove.x * moveModifier + xPos
//
//                                // Check whether the move is not out of bounds
//                                if (newXPos >= 0 && newXPos < Board.WIDTH &&
//                                    newYPos >= 0 && newYPos < Board.HEIGHT) {
//
//                                    var destinationBlock = newNode.board.blocks[newYPos][newXPos]
//
//                                    // Check if the destination block has a friendly block
//                                    if (destinationBlock.piece == null || destinationBlock.piece!!.color != playerColor) {
//                                        newNode.originPosition = Coordinate(yPos, xPos) // Set the origin of the piece before moving the piece
//
//                                        // Move the piece inside the board to the desired location
//                                        try {
//                                            newNode.board.movePiece(Coordinate(yPos, xPos), Coordinate(newYPos, newXPos))
//                                        }
//                                        catch (e: Exception) {
//                                            continue
//                                        }
//
//                                        // Switch the card after a move is made
//                                        newNode.switchCard(cardIndex, playerColor)
//
//                                        newNode.previousCardUsedIndex = cardIndex
//                                        newNode.previousCardMoveUsedIndex = cardMoveIndex
//
//                                        // Add the new board to the list of child nodes
//                                        childNodes.add(newNode)
//                                    }
//                                }
//                            }
//                        }
//                    }

                    // Iterate the blocks to find pieces
//                    for (row in newNode.board.blocks.indices) {
//                        for (column in newNode.board.blocks[row].indices) {
//                            val blockPiece = newNode.board.blocks[row][column].piece
//
//                            // Check whether the block has a piece
//                            if (blockPiece != null) {
//                                // Check whether the piece is the correct player's piece
//                                if (blockPiece.color == playerColor) {
//                                    // Check whether the move is not out of bounds
//                                    if (cardMove.x + column >= 0 && cardMove.x + column < Board.WIDTH &&
//                                            cardMove.y + row >= 0 && cardMove.y + row < Board.HEIGHT) {
//                                        var destinationBlock = newNode.board.blocks[row][column]
//
//                                        // Check if the destination block has a friendly block
//                                        if (destinationBlock.piece?.color != playerColor) {
//                                            newNode.originPosition = Coordinate(row, column) // Set the origin of the piece before moving the piece
//
//                                            // Move the piece inside the board to the desired location
//                                            newNode.board.movePiece(Coordinate(row, column), cardMove)
//
//                                            // Switch the card after a move is made
//                                            newNode.switchCard(cardIndex, playerColor)
//
//                                            newNode.previousCardUsedIndex = cardIndex
//                                            newNode.previousCardMoveUsedIndex = cardMoveIndex
//
//                                            // Add the new board to the list of child nodes
//                                            childNodes.add(newNode)
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }

            return childNodes
        }
    }
}