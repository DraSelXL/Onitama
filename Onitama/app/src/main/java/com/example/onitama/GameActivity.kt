package com.example.onitama

import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.measureNanoTime

class GameActivity : AppCompatActivity() {
    var turn = 0
    var allCards: Array<Int> = arrayOf(R.drawable.boar, R.drawable.cobra, R.drawable.crab, R.drawable.crane,
        R.drawable.dragon, R.drawable.eel, R.drawable.elephant, R.drawable.frog,
        R.drawable.goose, R.drawable.horse, R.drawable.mantis, R.drawable.monkey,
        R.drawable.ox, R.drawable.rabbit, R.drawable.rooster, R.drawable.tiger)
    var gameCards: ArrayList<Int> = ArrayList()
    var board: MutableList<MutableList<ImageButton>> = mutableListOf()

    lateinit var ivEnemy1: ImageView
    lateinit var ivEnemy2: ImageView
    lateinit var ivPlayer1: ImageView
    lateinit var ivPlayer2: ImageView
    lateinit var ivNext: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        //set board
        board.add(mutableListOf(findViewById(R.id.ib00), findViewById(R.id.ib01), findViewById(R.id.ib02), findViewById(R.id.ib03), findViewById(R.id.ib04)))
        board.add(mutableListOf(findViewById(R.id.ib10), findViewById(R.id.ib11), findViewById(R.id.ib12), findViewById(R.id.ib13), findViewById(R.id.ib14)))
        board.add(mutableListOf(findViewById(R.id.ib20), findViewById(R.id.ib31), findViewById(R.id.ib22), findViewById(R.id.ib23), findViewById(R.id.ib24)))
        board.add(mutableListOf(findViewById(R.id.ib30), findViewById(R.id.ib31), findViewById(R.id.ib32), findViewById(R.id.ib33), findViewById(R.id.ib34)))
        board.add(mutableListOf(findViewById(R.id.ib40), findViewById(R.id.ib41), findViewById(R.id.ib42), findViewById(R.id.ib43), findViewById(R.id.ib44)))

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

        //set cards
        ivEnemy1 = findViewById(R.id.ivEnemy1)
        ivEnemy2 = findViewById(R.id.ivEnemy2)
        ivPlayer1 = findViewById(R.id.ivPlayer1)
        ivPlayer2 = findViewById(R.id.ivPlayer2)
        ivNext = findViewById(R.id.ivNext)
        
        ivEnemy1.setImageResource(gameCards.get(0))
        ivEnemy2.setImageResource(gameCards.get(1))
        ivPlayer1.setImageResource(gameCards.get(2))
        ivPlayer2.setImageResource(gameCards.get(3))
        ivNext.setImageResource(gameCards.get(4))
    }
}