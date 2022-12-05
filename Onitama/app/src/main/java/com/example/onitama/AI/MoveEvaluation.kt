package com.example.onitama.AI

import com.example.onitama.components.Coordinate

/**
 * A data class used to store the evaluation result, the card and the move used, and the origin of the piece before moved.
 *
 * @param originPosition The position of a piece before being moved.
 * @param cardUsedIndex The card that is used for making the move.
 * @param moveUsedIndex The move from the used card that is used.
 * @param evaluation The final score of the evaluation.
 * @param alpha The alpha result value of the Alpha-Beta Pruning algorithm.
 * @param beta The beta result value of the Alpha-Beta Pruning algorithm.
 */
data class MoveEvaluation(
    val depth: Int,
    val originPosition: Coordinate,
    val cardUsedIndex: Int,
    val moveUsedIndex: Int,
    val evaluation: Double,
    var alpha: Double,
    var beta: Double,
)