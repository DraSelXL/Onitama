package com.example.onitama

//constructor
class Card(
    var name:String,
    var img:Int,
    var color:String, //red or blue
    var possibleMoves:ArrayList<Coordinate> = ArrayList(),
) {

    init{
        possibleMoves = getPossibleMove(name)
    }

    private fun getPossibleMove(name:String):ArrayList<Coordinate>{
        var coors = arrayListOf<Coordinate>()
        when(name){
            "tiger" -> {
                coors.add(Coordinate(-2,0))
                coors.add(Coordinate(1,0))
            }
            "dragon" -> {
                coors.add(Coordinate(-1,-2))
                coors.add(Coordinate(-1,2))
                coors.add(Coordinate(1,-1))
                coors.add(Coordinate(1,1))
            }
            "frog" -> {
                coors.add(Coordinate(-1,-1))
                coors.add(Coordinate(0,-2))
                coors.add(Coordinate(1,1))
            }
            "rabbit" -> {
                coors.add(Coordinate(-1,1))
                coors.add(Coordinate(0,2))
                coors.add(Coordinate(1,-1))
            }
            "crab" -> {
                coors.add(Coordinate(-1,0))
                coors.add(Coordinate(0,-2))
                coors.add(Coordinate(0,2))
            }
            "elephant" -> {
                coors.add(Coordinate(-1,-1))
                coors.add(Coordinate(-1,1))
                coors.add(Coordinate(0,-1))
                coors.add(Coordinate(0,1))
            }
            "goose" -> {
                coors.add(Coordinate(-1,-1))
                coors.add(Coordinate(0,-1))
                coors.add(Coordinate(0,1))
                coors.add(Coordinate(1,1))
            }
            "rooster" -> {
                coors.add(Coordinate(-1,1))
                coors.add(Coordinate(0,-1))
                coors.add(Coordinate(0,1))
                coors.add(Coordinate(1,-1))
            }
            "monkey" -> {
                coors.add(Coordinate(-1,-1))
                coors.add(Coordinate(-1,1))
                coors.add(Coordinate(1,-1))
                coors.add(Coordinate(1,1))
            }
            "mantis" -> {
                coors.add(Coordinate(-1,-1))
                coors.add(Coordinate(-1,1))
                coors.add(Coordinate(1,0))
            }
            "horse" -> {
                coors.add(Coordinate(-1,0))
                coors.add(Coordinate(0,-1))
                coors.add(Coordinate(1,0))
            }
            "ox" -> {
                coors.add(Coordinate(-1,0))
                coors.add(Coordinate(0,1))
                coors.add(Coordinate(1,0))
            }
            "crane" -> {
                coors.add(Coordinate(-1,0))
                coors.add(Coordinate(1,-1))
                coors.add(Coordinate(1,1))
            }
            "boar" -> {
                coors.add(Coordinate(-1,0))
                coors.add(Coordinate(0,-1))
                coors.add(Coordinate(0,1))
            }
            "eel" -> {
                coors.add(Coordinate(-1,-1))
                coors.add(Coordinate(0,1))
                coors.add(Coordinate(1,-1))
            }
            "cobra" ->{
                coors.add(Coordinate(-1,1))
                coors.add(Coordinate(0,-1))
                coors.add(Coordinate(1,1))
            }
        }
        return coors
    }
}