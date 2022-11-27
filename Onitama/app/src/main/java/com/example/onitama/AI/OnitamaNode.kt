package com.example.onitama.AI

import com.example.onitama.components.Board
import com.example.onitama.components.Card
import com.example.onitama.components.PlayerColor

/**
 * A class to represent a state of the game in the AI.
 * Used to evaluate whether the position is advantageous or not to the AI.
 */
data class OnitamaNode(
    var board: Board,
    var redCards: Array<Card>,
    var blueCards: Array<Card>,
    var storedCard: Card
) {
    var previousCardUsedIndex: Int? = null
    var previousCardMoveUsedIndex: Int? = null

    /**
     * Switches the card with the given storedCard argument.
     *
     * @param cardIndex The index of the card in the array of cards.
     * @param playerColor The color of the player switch their card.
     */
    fun switchCard(cardIndex: Int, playerColor: String) {
        if (playerColor == PlayerColor.RED) {
            var tempCard = Card(redCards[cardIndex].name, redCards[cardIndex].img, redCards[cardIndex].color)
            redCards[cardIndex] = storedCard
            storedCard = tempCard
        }
        else if (playerColor == PlayerColor.BLUE) {
            var tempCard = Card(blueCards[cardIndex].name, blueCards[cardIndex].img, blueCards[cardIndex].color)
            blueCards[cardIndex] = storedCard
            storedCard = tempCard
        }
    }
}