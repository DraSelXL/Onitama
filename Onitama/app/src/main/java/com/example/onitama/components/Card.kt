package com.example.onitama.components

//constructor
data class Card(
    var name: String,
    var img: Int,
    var color: PlayerColor, // Red or Blue
    var possibleMoves: ArrayList<Coordinate> = ArrayList(),
) {
    // Static Properties
    companion object {
        // Card Names
        var TIGER = "tiger"
        var DRAGON = "dragon"
        var FROG = "frog"
        var RABBIT = "rabbit"
        var CRAB = "crab"
        var ELEPHANT = "elephant"
        var GOOSE = "goose"
        var ROOSTER = "rooster"
        var MONKEY = "monkey"
        var MANTIS = "mantis"
        var HORSE = "horse"
        var OX = "ox"
        var CRANE = "crane"
        var BOAR = "boar"
        var EEL = "eel"
        var COBRA = "cobra"
    }

    init {
        possibleMoves = getPossibleMove(name)
    }

    /**
     * Generates the card's moves based on what type the card being passed as a parameter.
     *
     * @param name The name of the type of the card.
     * @return The ArrayList of possible moves from the given card type.
     */
    private fun getPossibleMove(name:String): ArrayList<Coordinate> {
        var coors = arrayListOf<Coordinate>()
        when (name) {
            TIGER -> {
                coors.add(Coordinate(0, -2))
                coors.add(Coordinate(0, 1))
            }
            DRAGON -> {
                coors.add(Coordinate(-2, -1))
                coors.add(Coordinate(2, -1))
                coors.add(Coordinate(-1, 1))
                coors.add(Coordinate(1, 1))
            }
            FROG -> {
                coors.add(Coordinate(-1, -1))
                coors.add(Coordinate(-2, 0))
                coors.add(Coordinate(1, 1))
            }
            RABBIT -> {
                coors.add(Coordinate( 1, -1))
                coors.add(Coordinate(2, 0))
                coors.add(Coordinate( -1, 1))
            }
            CRAB -> {
                coors.add(Coordinate( 0, -1))
                coors.add(Coordinate(-2, 0))
                coors.add(Coordinate( 2, 0))
            }
            ELEPHANT -> {
                coors.add(Coordinate(-1, -1))
                coors.add(Coordinate(1, -1))
                coors.add(Coordinate(-1, 0))
                coors.add(Coordinate(1, 0))
            }
            GOOSE -> {
                coors.add(Coordinate(-1, -1))
                coors.add(Coordinate(-1, 0))
                coors.add(Coordinate(1, 0))
                coors.add(Coordinate(1, 1))
            }
            ROOSTER -> {
                coors.add(Coordinate(1, -1))
                coors.add(Coordinate(-1, 0))
                coors.add(Coordinate(1, 0))
                coors.add(Coordinate(-1, 1))
            }
            MONKEY -> {
                coors.add(Coordinate(-1, -1))
                coors.add(Coordinate(1, -1))
                coors.add(Coordinate(-1, 1))
                coors.add(Coordinate(1, 1))
            }
            MANTIS -> {
                coors.add(Coordinate(-1, -1))
                coors.add(Coordinate( 1, -1))
                coors.add(Coordinate(0, 1))
            }
            HORSE -> {
                coors.add(Coordinate(0, -1))
                coors.add(Coordinate(-1, 0))
                coors.add(Coordinate(0, 1))
            }
            OX -> {
                coors.add(Coordinate(0, -1))
                coors.add(Coordinate(1, 0))
                coors.add(Coordinate(0, 1))
            }
            CRANE -> {
                coors.add(Coordinate(0, -1))
                coors.add(Coordinate(-1, 1))
                coors.add(Coordinate(1, 1))
            }
            BOAR -> {
                coors.add(Coordinate(0, -1))
                coors.add(Coordinate(-1, 0))
                coors.add(Coordinate(1, 0))
            }
            EEL -> {
                coors.add(Coordinate(-1, -1))
                coors.add(Coordinate(1, 0))
                coors.add(Coordinate(-1, 1))
            }
            COBRA ->{
                coors.add(Coordinate(1, -1))
                coors.add(Coordinate(-1, 0))
                coors.add(Coordinate(1, 1))
            }
        }
        return coors
    }
}