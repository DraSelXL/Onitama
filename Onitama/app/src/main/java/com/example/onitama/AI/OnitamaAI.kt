package com.example.onitama.AI

import com.example.onitama.components.Card
import com.example.onitama.components.Player

/**
 * A class to represent the Onitama's Artificial Intelligence.
 * Everything that needs to interact with the AI goes through this class.
 */
class OnitamaAI(
    override var cards: Array<Card>,
    override var color: String,
) : Player(cards, color) {

}