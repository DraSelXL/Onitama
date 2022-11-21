package com.example.onitama

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import com.example.onitama.components.*
import java.util.*

class GameActivity : AppCompatActivity() {
    // Static Properties
    companion object {
        /** Indicates that currently is the player's turn to move. */
        var PLAYER_TURN = "player"

        /** Indicates that currently is the enemy's turn to move. */
        var ENEMY_TURN = "enemy"
    }

    // Main Properties
    private lateinit var ivEnemy1: ImageView    // Enemy Card Image View 1
    private lateinit var ivEnemy2: ImageView    // Enemy Card Image View 2

    private lateinit var ivPlayer1: ImageView   // Player Card Image View 1
    private lateinit var ivPlayer2: ImageView   // Player Card Image View 2

    private lateinit var ivNext: ImageView      // The Stored Card Image View

    private lateinit var enemy: Player          // The Enemy Specific Class (Cards, and Color)
    private lateinit var player: Player         // The Main Player Specific Identities (Cards, and Color)

    private var turn = GameActivity.PLAYER_TURN // Indicates whose turn it is currently
    private var selectedCard = -1               // Holds the current selected index from the owned cards
    private var selectedBlock: Block? = null    // Indicates what is the currently selected game board
    private var isSelecting = false

    // A list of all available card in the deck
    private var allCards:Array<Card> = arrayOf(
        Card(Card.TIGER, R.drawable.tiger, Card.BLUE),
        Card(Card.DRAGON, R.drawable.dragon, Card.RED),
        Card(Card.FROG, R.drawable.frog, Card.RED),
        Card(Card.RABBIT, R.drawable.rabbit, Card.BLUE),
        Card(Card.CRAB, R.drawable.crab, Card.BLUE),
        Card(Card.ELEPHANT, R.drawable.elephant, Card.RED),
        Card(Card.GOOSE, R.drawable.goose, Card.BLUE),
        Card(Card.ROOSTER, R.drawable.rooster, Card.RED),
        Card(Card.MONKEY, R.drawable.monkey, Card.BLUE),
        Card(Card.MANTIS, R.drawable.mantis, Card.RED),
        Card(Card.HORSE, R.drawable.horse, Card.RED),
        Card(Card.OX, R.drawable.ox, Card.BLUE),
        Card(Card.CRANE, R.drawable.crane, Card.BLUE),
        Card(Card.BOAR, R.drawable.boar, Card.RED),
        Card(Card.EEL, R.drawable.eel, Card.BLUE),
        Card(Card.COBRA, R.drawable.cobra, Card.RED),
    )

    private var storedCard: Card? = null                                        // The stored card that determine the starting turn
    private var board: MutableList<MutableList<ImageButton>> = mutableListOf()  // The variable that stores the ImageButtons
    private var blocks: MutableList<MutableList<Block>> = mutableListOf()       // The variable that stores the conditions of every block in the board

    /**
     * Randomizes 5 starting cards and store 2 of each of the randomized cards into each players.
     * Stores the last card to a variable in the activity.
     *
     * Automatically called when starting a new game within the `initNewGame()` method.
     */
    private fun initRandomCards() {
        var gameCards: ArrayList<Card> = ArrayList() // Temporary arraylist to hold the randomized cards

        // Random 5 cards to use in the game
        for (i in 0 until 5) {
            var rnd = 0

            if (gameCards.size > 0) { // Checks if a card has been randomized before
                // Randomizes a card until a non-duplicate card is set
                do {
                    rnd = Random(System.currentTimeMillis()).nextInt(allCards.size)
                } while(gameCards.contains(allCards[rnd]))
            }
            else {
                // Randomizes the first new card into the game
                rnd = Random(System.currentTimeMillis()).nextInt(allCards.size)
            }
            gameCards.add(allCards[rnd])
        }
        // Store the randomized cards into the players
        enemy = Player(arrayOf(gameCards[0], gameCards[1]), Card.BLUE)
        player = Player(arrayOf(gameCards[2], gameCards[3]), Card.RED)

        storedCard = gameCards[gameCards.lastIndex] // Store the last randomized card into the activity

        // Set the ImageView image resource to the appropriate card image resource
        ivEnemy1.setImageResource(enemy.cards[0].img)
        ivEnemy2.setImageResource(enemy.cards[1].img)

        ivPlayer1.setImageResource(player.cards[0].img)
        ivPlayer2.setImageResource(player.cards[1].img)

        ivNext.setImageResource(storedCard!!.img)
    }

    /**
     * Initialize the blocks of the game board with the starting pieces for both players.
     * The first row of the board is filled with the enemy's pieces, while the last row is filled with the player's pieces.
     *
     * Automatically called when starting a new game within the `initNewGame()` method.
     */
    private fun initBlocks() {
        blocks.clear()
        // Rows
        for (i in 0..4) {
            var rowBlock = mutableListOf<Block>()   // A temporary row MutableList to contain the row blocks
            // Columns
            for (j in 0..4) {
                board[i][j].setBackgroundColor(Color.parseColor("#FFFFFFFF"))

                if (i == 0) { // Is the current row the enemy's?
                    if (j != 2) { // Is the current column not the master's column?
                        rowBlock.add(
                            Block(1, Coordinate(i,j), Block.OCCUPY_ENEMY,
                                Piece(Piece.PAWN, R.drawable.ic_pawn_blue)
                            )
                        )
                    }
                    else { // Set the column with a master piece
                        rowBlock.add(
                            Block(1, Coordinate(i,j), Block.OCCUPY_ENEMY,
                                Piece(Piece.MASTER, R.drawable.ic_crown_blue)
                            )
                        )
                    }
                }
                else if (i == 4) { // Is the current row the player's row?
                    if(j != 2){ // Is the current row not the master's column?
                        rowBlock.add(
                            Block(1, Coordinate(i,j), Block.OCCUPY_PLAYER,
                                Piece(Piece.PAWN, R.drawable.ic_pawn_red)
                            )
                        )
                    }
                    else { // Set the column with a master piece
                        rowBlock.add(
                            Block(1, Coordinate(i,j), Block.OCCUPY_PLAYER,
                                Piece(Piece.MASTER, R.drawable.ic_crown_red)
                            )
                        )
                    }
                }
                else { // Sets the current block as an empty block
                    rowBlock.add(Block(0, Coordinate(i,j)))
                }
            }
            blocks.add(rowBlock)
        }
    }

    /**
     * Starts a new game by randomizing the cards and resetting the blocks.
     *
     * This method calls the `initRandomCards()` method to randomize the cards and then decides who goes first.
     * After the randomization, resets the blocks to the initial state with the `initBlocks()` method.
     */
    private fun initNewGame() {
        initRandomCards()

        turn = if (storedCard!!.color == Card.BLUE) GameActivity.ENEMY_TURN else GameActivity.PLAYER_TURN

        initBlocks()
    }

    /**
     * Checks whether the given coordinate is valid for a piece movement.
     *
     * @param y The row position of the board.
     * @param x The column position of the board.
     * @return True if the position can be occupied by a piece.
     */
    private fun isValidPosition(y: Int, x: Int): Boolean {
        var isValid = true

        // Is the given position inside the board and the block is empty
        if (!(y in 0..4 && x in 0..4) || blocks[y][x].status != 0) {
            isValid = false
        }

        return isValid
    }

    /**
     * Resets the background color of the blocks in the board to white.
     */
    private fun refreshBoardColor(){
        for (i in board.indices) { // Rows
            for (j in board[i].indices) { // Columns
                board[i][j].setBackgroundColor(Color.parseColor("#FFFFFFFF"))
                board[i][j].tag = -1
            }
        }
    }

    /**
     * Set the block's background color with the given position to green to indicate a possible move for a piece.
     */
    private fun setColorBlock(y:Int, x:Int){
        board[y][x].setBackgroundColor(Color.GREEN)
        board[y][x].setBackgroundResource(R.drawable.occupy_bg)
//        board[y][x].setBackgroundResource(0)
    }

    /**
     * Resets the selected block (with a piece) to null, resets all blocks background color, and resets the selected card.
     */
    private fun refreshSelection(){
        refreshBoardColor() // Clears all block background color

        selectedBlock = null
        selectedCard = -1
        isSelecting = false

        // Remove the cards selected indicators
        ivPlayer1.setBackgroundResource(0)
        ivPlayer2.setBackgroundResource(0)

        ivEnemy1.setBackgroundResource(0)
        ivEnemy2.setBackgroundResource(0)
    }

    /**
     * Resets the selected block to an empty block.
     *
     * @param y The row position inside the board.
     * @param x The column position inside the board.
     */
    private fun cleanBlock(y: Int, x: Int){
        // board[y][x].setImageResource(R.drawable.plain_bg)
        board[y][x].setImageResource(0)
        blocks[y][x] = Block(0, Coordinate(y,x))
    }

    /**
     * Add highlights to the board blocks based on the currently selected piece and possible moves of the selected card.
     */
    private fun applyBoardMoves():Boolean{
        var isAble = false
        //color the board
        if (selectedBlock != null) { // Is there a block currently being selected?
            var myCard: Card
            refreshBoardColor()
            if (turn == GameActivity.PLAYER_TURN) { // Is it the player's turn?
                myCard = player.cards[selectedCard] // Get the currently selected card from the player

                // Add a highlight in blocks based on the coordinates of the possible moves from the player's perspective
                for (i in myCard.possibleMoves.indices){
                    var tempMove = myCard.possibleMoves[i]
                    var yNext = selectedBlock!!.pos.y + tempMove.y
                    var xNext = selectedBlock!!.pos.x + tempMove.x

                    if (isValidPosition(yNext,xNext)) { // Checks whether the move is a valid one
                        setColorBlock(yNext, xNext) // Highlight the block

                        // Tag indicates that the current block is a valid move
                        board[yNext][xNext].tag = 1
                        isAble = true
                    }
                }
            }
            else if (turn == GameActivity.ENEMY_TURN) { // Is it the enemy's turn?
                myCard = enemy.cards[selectedCard] // Get the currently selected card from the enemy

                // Add a highlight in blocks based on the coordinates of the possible moves from the enemy's perspective
                for (i in myCard.possibleMoves.indices) {
                    var tempMove = myCard.possibleMoves[i]
                    var yNext = selectedBlock!!.pos.y + (tempMove.y * -1)
                    var xNext = selectedBlock!!.pos.x + (tempMove.x * -1)

                    if (isValidPosition(yNext,xNext)) {
                        setColorBlock(yNext, xNext)

                        // Tag indicates that the current block is a valid move
                        board[yNext][xNext].tag = 1
                        isAble = true
                    }
                }
            }
        }
        return isAble
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Set board
        board.add(mutableListOf(findViewById(R.id.ib00), findViewById(R.id.ib01), findViewById(R.id.ib02), findViewById(R.id.ib03), findViewById(R.id.ib04)))
        board.add(mutableListOf(findViewById(R.id.ib10), findViewById(R.id.ib11), findViewById(R.id.ib12), findViewById(R.id.ib13), findViewById(R.id.ib14)))
        board.add(mutableListOf(findViewById(R.id.ib20), findViewById(R.id.ib21), findViewById(R.id.ib22), findViewById(R.id.ib23), findViewById(R.id.ib24)))
        board.add(mutableListOf(findViewById(R.id.ib30), findViewById(R.id.ib31), findViewById(R.id.ib32), findViewById(R.id.ib33), findViewById(R.id.ib34)))
        board.add(mutableListOf(findViewById(R.id.ib40), findViewById(R.id.ib41), findViewById(R.id.ib42), findViewById(R.id.ib43), findViewById(R.id.ib44)))

        // Set cards
        ivEnemy1 = findViewById(R.id.ivEnemy1)
        ivEnemy2 = findViewById(R.id.ivEnemy2)
        ivPlayer1 = findViewById(R.id.ivPlayer1)
        ivPlayer2 = findViewById(R.id.ivPlayer2)
        ivNext = findViewById(R.id.ivNext)

        initNewGame() // Start a new game

        // Add player's cards event listeners
        ivPlayer1.setOnClickListener {
            // Select the first player card when it's the player's turn
            if(turn == GameActivity.PLAYER_TURN){
                selectedCard = 0 //left
                ivPlayer1.setBackgroundResource(R.drawable.border)
                ivPlayer2.setBackgroundResource(0)
            }
        }
        ivPlayer2.setOnClickListener {
            // Select the second player card when it's the player's turn
            if(turn == GameActivity.PLAYER_TURN) {
                selectedCard = 1 //right
                ivPlayer2.setBackgroundResource(R.drawable.border)
                ivPlayer1.setBackgroundResource(0)
            }
        }

        // Add Enemy's Cards event listeners
        ivEnemy1.setOnClickListener {
            // Select the first enemy card when it's the enemy's turn
            if(turn == GameActivity.ENEMY_TURN) {
                selectedCard = 0 //left
                ivEnemy1.setBackgroundResource(R.drawable.border)
                ivEnemy2.setBackgroundResource(0)
            }
        }
        ivEnemy2.setOnClickListener {
            // Select the second enemy card when it's the enemy's turn
            if(turn == GameActivity.ENEMY_TURN) {
                selectedCard = 1 //right
                ivEnemy2.setBackgroundResource(R.drawable.border)
                ivEnemy1.setBackgroundResource(0)
            }
        }

        // Add an event listener for every board blocks
        for (i in board.indices) {
            for (j in board[i].indices) {
                board[i][j].setOnClickListener {
                    // Check if current matches the player turn
                    if (!isSelecting) { // Is the current mode not piece selected
                        if (selectedCard != -1 && blocks[i][j].status != 0 && blocks[i][j].occupier == turn) {
                            // Start highlighting blocks for possible moves and prepare to move the piece
                            selectedBlock = blocks[i][j]
                            isSelecting = true
                            var isAble = applyBoardMoves()
                            if(!isAble){
                                selectedBlock = null
                                isSelecting = false
                            }
                        }
                    }
                   else {
                       //board[i][j] = new location
                       if (board[i][j].tag == 1) { // Is the clicked block a valid move?
                           // Place the piece to the clicked block
                           var oldY = selectedBlock!!.pos.y
                           var oldX = selectedBlock!!.pos.x

                           // Set the block attributes
                           blocks[i][j] = selectedBlock!!
                           blocks[i][j].pos = Coordinate(i,j)
                           blocks[i][j].occupier = turn

                           board[i][j].setImageResource(blocks[i][j].piece!!.img)

                           cleanBlock(oldY, oldX) // Resets the previous piece block position
                           refreshSelection()

                           turn = if (turn == GameActivity.PLAYER_TURN) GameActivity.ENEMY_TURN else GameActivity.PLAYER_TURN
                       }
                    }
                }
            }
        }
    }
}