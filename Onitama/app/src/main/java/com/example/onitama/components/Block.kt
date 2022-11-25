package com.example.onitama.components

data class Block(
    //0 for empty
    var status: Int,
    var pos: Coordinate,
    var occupier: String = Block.OCCUPY_NONE, // Enemy or Player
    var piece: Piece? = null,
) {
    // Static Variables
    companion object {
        var OCCUPY_ENEMY = "enemy"      // Indicates that the block's occupier is an enemy
        var OCCUPY_PLAYER = "player"    // Indicates that the block's occupier is a player
        var OCCUPY_NONE = "none"        // Indicates that the block is currently unoccupied
    }
}