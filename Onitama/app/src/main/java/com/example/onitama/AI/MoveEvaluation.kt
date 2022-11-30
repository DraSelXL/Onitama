package com.example.onitama.AI

import com.example.onitama.components.Coordinate

class MoveEvaluation(
    var originPosition: Coordinate,
    var cardIndex: Int,
    var cardMoveIndex: Int,
    var evaluation: Float,
    var alpha: Double,
    var beta: Double
)