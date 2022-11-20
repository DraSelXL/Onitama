package com.example.onitama

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.measureNanoTime

class GameActivity : AppCompatActivity() {

    private lateinit var ivEnemy1: ImageView
    private lateinit var ivEnemy2: ImageView
    private lateinit var ivPlayer1: ImageView
    private lateinit var ivPlayer2: ImageView
    private lateinit var ivNext: ImageView
    private lateinit var enemy:Player
    private lateinit var player:Player

    //player's turn
    private var turn = "player"
    private var selectedCard = -1
    private var selectedBlock:Block? = null
    private var isSelecting = false

    private var allCards:Array<Card> = arrayOf(
        Card("tiger",R.drawable.tiger,"blue"),
        Card("dragon",R.drawable.dragon,"red"),
        Card("frog",R.drawable.frog,"red"),
        Card("rabbit",R.drawable.rabbit,"blue"),
        Card("crab",R.drawable.crab,"blue"),
        Card("elephant",R.drawable.elephant,"red"),
        Card("goose",R.drawable.goose,"blue"),
        Card("rooster",R.drawable.rooster,"red"),
        Card("monkey",R.drawable.monkey,"blue"),
        Card("mantis",R.drawable.mantis,"red"),
        Card("horse",R.drawable.horse,"red"),
        Card("ox",R.drawable.ox,"blue"),
        Card("crane",R.drawable.crane,"blue"),
        Card("boar",R.drawable.boar,"red"),
        Card("eel",R.drawable.eel,"blue"),
        Card("cobra",R.drawable.cobra,"red"),
    )

    private var gameCards: ArrayList<Card> = ArrayList()
    private var board: MutableList<MutableList<ImageButton>> = mutableListOf()
    private var blocks: MutableList<MutableList<Block>> = mutableListOf()

    //start init
    private fun initRandomCards(){
        //random 5 cards to use in the game
        for(i in 0 until 5){
            var rnd = 0
            if(gameCards.size > 0){
                do{
                    rnd = Random(measureNanoTime {  }).nextInt(16)
                }while(gameCards.contains(allCards[rnd]))
            }
            else{
                rnd = Random(measureNanoTime {  }).nextInt(16)
            }
            gameCards.add(allCards[rnd])
        }
        ivEnemy1.setImageResource(gameCards[0].img)
        ivEnemy2.setImageResource(gameCards[1].img)
        ivPlayer1.setImageResource(gameCards[2].img)
        ivPlayer2.setImageResource(gameCards[3].img)
        ivNext.setImageResource(gameCards[4].img)

        enemy = Player(arrayOf(gameCards[0],gameCards[1]),"blue")
        player = Player(arrayOf(gameCards[2],gameCards[3]),"red")

    }

    private fun initBlocks(){
        //row
        for (i in 0..4){
            var rowBlock = mutableListOf<Block>()
            for (j in 0..4){
                board[i][j].setBackgroundColor(Color.parseColor("#FFFFFFFF"))
                if(i == 0){
                    //enemy
                    if(j != 2){
                        rowBlock.add(Block(1, Coordinate(i,j),"enemy",Piece("pawn",R.drawable.ic_pawn_blue)))
                    }
                    else{
                        rowBlock.add(Block(1, Coordinate(i,j),"enemy",Piece("pawn",R.drawable.ic_crown_blue)))
                    }
                }
                else if(i == 4){
                    if(j != 2){
                        rowBlock.add(Block(1, Coordinate(i,j),"player",Piece("pawn",R.drawable.ic_pawn_red)))
                    }
                    else{
                        rowBlock.add(Block(1, Coordinate(i,j),"player",Piece("pawn",R.drawable.ic_crown_red)))
                    }
                }
                else {
                    rowBlock.add(Block(0, Coordinate(i,j)))
                }
            }
            blocks.add(rowBlock)
        }
    }

    private fun initNewGame(){
        turn = "player"
        initRandomCards()
        initBlocks()
    }

    private fun isValidPosition(y:Int,x:Int):Boolean{
        var isValid = true
        if((!(y in 0..4 && x in 0..4))||blocks[y][x].status != 0){
            isValid = false
        }
        return isValid
    }

    private fun refreshBoardColor(){
        for (i in board.indices){
            for (j in board[i].indices){
                board[i][j].setBackgroundColor(Color.parseColor("#FFFFFFFF"))
                board[i][j].tag = -1
            }
        }
    }

    private fun setColorBlock(y:Int,x:Int){
        board[y][x].setBackgroundColor(Color.GREEN)
        board[y][x].setBackgroundResource(R.drawable.occupy_bg)
    }

    private fun refreshSelection(){
        refreshBoardColor()
        selectedBlock = null
        selectedCard = -1
        isSelecting = false
        ivPlayer1.setBackgroundResource(0)
        ivPlayer2.setBackgroundResource(0)
        ivEnemy1.setBackgroundResource(0)
        ivEnemy2.setBackgroundResource(0)
    }

    private fun cleanBlock(y:Int,x:Int){
        board[y][x].setImageResource(R.drawable.plain_bg)
        blocks[y][x] = Block(0, Coordinate(y,x))
    }

    private fun applyBoardMoves(){
        //color the board
        if (selectedBlock != null){
            var myCard:Card
            if(turn == "player"){
                refreshBoardColor()
                myCard = player.cards[selectedCard]
                //apply from selected blocks
                for (i in myCard.possibleMoves.indices){
                    var tempMove = myCard.possibleMoves[i]
                    var yNext = selectedBlock!!.pos.y + tempMove.y
                    var xNext = selectedBlock!!.pos.x + tempMove.x
                    if(isValidPosition(yNext,xNext)){
                        setColorBlock(yNext,xNext)
                        //tag indicates that the current block is a valid move
                        board[yNext][xNext].tag = 1
                    }
                }
            }
            else if(turn == "enemy"){
                refreshBoardColor()
                myCard = enemy.cards[selectedCard]
                //apply from selected blocks
                for (i in myCard.possibleMoves.indices){
                    var tempMove = myCard.possibleMoves[i]
                    var yNext = selectedBlock!!.pos.y + (tempMove.y * -1)
                    var xNext = selectedBlock!!.pos.x + (tempMove.x * -1)
                    if(isValidPosition(yNext,xNext)){
                        setColorBlock(yNext,xNext)
                        //tag indicates that the current block is a valid move
                        board[yNext][xNext].tag = 1
                    }
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        //set board
        board.add(mutableListOf(findViewById(R.id.ib00), findViewById(R.id.ib01), findViewById(R.id.ib02), findViewById(R.id.ib03), findViewById(R.id.ib04)))
        board.add(mutableListOf(findViewById(R.id.ib10), findViewById(R.id.ib11), findViewById(R.id.ib12), findViewById(R.id.ib13), findViewById(R.id.ib14)))
        board.add(mutableListOf(findViewById(R.id.ib20), findViewById(R.id.ib21), findViewById(R.id.ib22), findViewById(R.id.ib23), findViewById(R.id.ib24)))
        board.add(mutableListOf(findViewById(R.id.ib30), findViewById(R.id.ib31), findViewById(R.id.ib32), findViewById(R.id.ib33), findViewById(R.id.ib34)))
        board.add(mutableListOf(findViewById(R.id.ib40), findViewById(R.id.ib41), findViewById(R.id.ib42), findViewById(R.id.ib43), findViewById(R.id.ib44)))

        //set cards
        ivEnemy1 = findViewById(R.id.ivEnemy1)
        ivEnemy2 = findViewById(R.id.ivEnemy2)
        ivPlayer1 = findViewById(R.id.ivPlayer1)
        ivPlayer2 = findViewById(R.id.ivPlayer2)
        ivNext = findViewById(R.id.ivNext)

        initNewGame()

        ivPlayer1.setOnClickListener {
            if(turn == "player"){
                selectedCard = 0 //left
                ivPlayer1.setBackgroundResource(R.drawable.border)
                ivPlayer2.setBackgroundResource(0)
            }
        }
        ivPlayer2.setOnClickListener {
            if(turn == "player") {
                selectedCard = 1 //right
                ivPlayer2.setBackgroundResource(R.drawable.border)
                ivPlayer1.setBackgroundResource(0)
            }
        }

        ivEnemy1.setOnClickListener {
            if(turn == "enemy") {
                selectedCard = 0 //left
                ivEnemy1.setBackgroundResource(R.drawable.border)
                ivEnemy2.setBackgroundResource(0)
            }
        }

        ivEnemy2.setOnClickListener {
            if(turn == "enemy") {
                selectedCard = 1 //right
                ivEnemy2.setBackgroundResource(R.drawable.border)
                ivEnemy1.setBackgroundResource(0)
            }
        }

        for (i in board.indices){
            for (j in board[i].indices){
                board[i][j].setOnClickListener {
                    //set onclick listener
                    //check if current matches the player turn
                    if(!isSelecting){
                        if(selectedCard!= -1 && blocks[i][j].status!=0 && blocks[i][j].occupier == turn){
                            //valid
                            selectedBlock = blocks[i][j]
                            isSelecting = true
                            applyBoardMoves()
                        }
                    }
                   else{
                       //board[i][j] = new location
                       if(board[i][j].tag == 1){
                          //place
                           var oldY = selectedBlock!!.pos.y
                           var oldX = selectedBlock!!.pos.x

                           blocks[i][j] = selectedBlock!!
                           blocks[i][j].pos = Coordinate(i,j)
                           blocks[i][j].occupier = turn
                           board[i][j].setImageResource(blocks[i][j].piece!!.img)
                           cleanBlock(oldY,oldX)
                           refreshSelection()

                           turn = if(turn == "player") "enemy" else "player"
                       }
                    }
                }
            }
        }
    }
}