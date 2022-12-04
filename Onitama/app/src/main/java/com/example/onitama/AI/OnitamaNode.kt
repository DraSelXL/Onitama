package com.example.onitama.AI

import com.example.onitama.components.Board
import com.example.onitama.components.Card
import com.example.onitama.components.Coordinate
import com.example.onitama.components.PlayerColor

/**
 * A class to represent a state of the game in the AI.
 * Used to evaluate whether the position is advantageous or not to the AI.
 *
 * @param board The state of the Onitama board.
 * @param redCards An array of the red player cards.
 * @param blueCards An array of the blue player cards.
 * @param storedCard The card which is not help by both players.
 */
class OnitamaNode(
    val board: Board,
    val redCards: Array<Card>,
    val blueCards: Array<Card>,
    var storedCard: Card
) {
    // Properties
    var originPosition: Coordinate? = null
    var cardUsedIndex: Int? = null
    var moveUsedIndex: Int? = null

    constructor(oldNode: OnitamaNode) : this(Board(oldNode.board), Array(2) { oldNode.redCards[it] } , Array(2) { oldNode.blueCards[it] }, oldNode.storedCard) {
        this.originPosition = oldNode.originPosition
        this.cardUsedIndex = oldNode.cardUsedIndex
        this.moveUsedIndex = oldNode.moveUsedIndex
    }

    /**
     * Switch the targeted card with the given player color, with the current stored card.
     *
     * @param cardIndex The index of the card to switch.
     * @param playerColor The color of the player card to switch.
     */
    fun switchCard(cardIndex: Int, playerColor: PlayerColor) {
        if (playerColor == PlayerColor.BLUE) {
            val tempCard = Card(blueCards[cardIndex].name, blueCards[cardIndex].img, blueCards[cardIndex].color)
            blueCards[cardIndex] = Card(storedCard.name, storedCard.img, storedCard.color)
            storedCard = tempCard
        }
        else if (playerColor == PlayerColor.RED) {
            val tempCard = Card(redCards[cardIndex].name, redCards[cardIndex].img, redCards[cardIndex].color)
            redCards[cardIndex] = Card(storedCard.name, storedCard.img, storedCard.color)
            storedCard = tempCard
        }
    }
}