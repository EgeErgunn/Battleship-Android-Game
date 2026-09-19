package com.example.battleship.activites

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AlertDialog
import com.example.battleship.R
import com.example.battleship.classes.AttackResult
import com.example.battleship.classes.Board
import com.example.battleship.classes.GameManager

class GamePlay : AppCompatActivity() {
    lateinit var myBoard: Board
    lateinit var enemyBoard: Board
    lateinit var gameManager: GameManager
    lateinit var myGrid: Array<IntArray>
    lateinit var enemyGrid: Array<IntArray>
    lateinit var enemyGridStatus: Array<Array<String>>
    lateinit var myGridStatus: Array<Array<String>>
    lateinit var originalBackground : Drawable
    var isGameEnded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_play)

        // Grids
        myGrid = arrayOf(
            intArrayOf(R.id.button196, R.id.button223, R.id.button241, R.id.button239, R.id.button271, R.id.button249, R.id.button269, R.id.button245),
            intArrayOf(R.id.button204, R.id.button219, R.id.button237, R.id.button242, R.id.button252, R.id.button263, R.id.button274, R.id.button268),
            intArrayOf(R.id.button205, R.id.button220, R.id.button238, R.id.button236, R.id.button255, R.id.button262, R.id.button253, R.id.button260),
            intArrayOf(R.id.button206, R.id.button222, R.id.button229, R.id.button227, R.id.button258, R.id.button270, R.id.button254, R.id.button267),
            intArrayOf(R.id.button207, R.id.button221, R.id.button228, R.id.button231, R.id.button272, R.id.button264, R.id.button251, R.id.button273),
            intArrayOf(R.id.button208, R.id.button226, R.id.button235, R.id.button234, R.id.button248, R.id.button246, R.id.button259, R.id.button244),
            intArrayOf(R.id.button209, R.id.button225, R.id.button233, R.id.button240, R.id.button243, R.id.button266, R.id.button261, R.id.button256),
            intArrayOf(R.id.button210, R.id.button224, R.id.button232, R.id.button230, R.id.button250, R.id.button265, R.id.button247, R.id.button257)
        )

        enemyGrid = arrayOf(
            intArrayOf(R.id.button102, R.id.button472, R.id.button488, R.id.button481, R.id.button506, R.id.button514, R.id.button509, R.id.button502),
            intArrayOf(R.id.button101, R.id.button469, R.id.button485, R.id.button476, R.id.button503, R.id.button494, R.id.button507, R.id.button510),
            intArrayOf(R.id.button100, R.id.button468, R.id.button477, R.id.button480, R.id.button493, R.id.button498, R.id.button519, R.id.button523),
            intArrayOf(R.id.button99,  R.id.button471, R.id.button482, R.id.button483, R.id.button499, R.id.button501, R.id.button513, R.id.button508),
            intArrayOf(R.id.button98,  R.id.button475, R.id.button487, R.id.button491, R.id.button505, R.id.button522, R.id.button517, R.id.button511),
            intArrayOf(R.id.button97,  R.id.button473, R.id.button484, R.id.button489, R.id.button515, R.id.button518, R.id.button497, R.id.button500),
            intArrayOf(R.id.button96,  R.id.button474, R.id.button478, R.id.button486, R.id.button495, R.id.button516, R.id.button504, R.id.button521),
            intArrayOf(R.id.button95,  R.id.button470, R.id.button490, R.id.button479, R.id.button520, R.id.button512, R.id.button496, R.id.button492)
        )
        originalBackground = findViewById<Button>(R.id.button196).background
        enemyGridStatus = Array(8) { Array(8) { "Empty" } }
        myGridStatus = Array(8) { Array(8) { "Empty" } }

        // Restore or Create Boards
        val myBoardSaved = savedInstanceState?.getStringArray("MY_BOARD")
        val enemyBoardSaved = savedInstanceState?.getStringArray("ENEMY_BOARD")
        myBoard = if (myBoardSaved != null) Board(myBoardSaved) else Board(intent.getStringArrayExtra("PLAYER_GRID_DATA"))
        enemyBoard = if (enemyBoardSaved != null) Board(enemyBoardSaved) else {
            val board = Board()
            board.placeShipsRandomly()
            board
        }

        // Restore or Create gameManager
        val savedBotMoves = savedInstanceState?.getIntegerArrayList("BOT_MOVES")
        gameManager = GameManager(myBoard, enemyBoard)
        if(savedBotMoves != null){
            gameManager.botMoves.clear()
            gameManager.botMoves.addAll(savedBotMoves)
        }

        isGameEnded = savedInstanceState?.getBoolean("GAME_ENDED") ?: false

        // Restore and draw cell status
        val flatStatus = savedInstanceState?.getStringArray("CELL_STATUS")
        if (flatStatus != null) {
            for (r in 0..7) {
                for (c in 0..7) {
                    enemyGridStatus[r][c] = flatStatus[r * 8 + c]
                }
            }
            redrawEnemyGrid()
        }

        val savedMyGridStatus = savedInstanceState?.getStringArray("MY_CELL_STATUS")
        if (savedMyGridStatus != null) {
            for (r in 0..7) {
                for (c in 0..7) {
                    myGridStatus[r][c] = savedMyGridStatus[r * 8 + c]
                }
            }
        }
        redrawGrid(myGrid, myBoard, false, myGridStatus)

        // Grid Buttons
        for (row in 0..7) {
            for (col in 0..7) {
                val button = findViewById<Button>(enemyGrid[row][col])

                button.setOnClickListener {
                    handlePlayerMove(row, col, button)
                }
            }
        }

        if (isGameEnded) {
            disableEnemyGrid()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArray("MY_BOARD", myBoard.convertToPrimitives())
        outState.putStringArray("ENEMY_BOARD", enemyBoard.convertToPrimitives())
        outState.putStringArray("CELL_STATUS", enemyGridStatus.flatten().toTypedArray())
        outState.putBoolean("GAME_ENDED", isGameEnded)
        outState.putStringArray("MY_CELL_STATUS", myGridStatus.flatten().toTypedArray())
        outState.putIntegerArrayList("BOT_MOVES", ArrayList(gameManager.botMoves))
    }
    fun redrawGrid(gridButtons: Array<IntArray>, board: Board, clickableIfEmpty: Boolean, status: Array<Array<String>>) {
        for (r in 0..7) {
            for (c in 0..7) {
                val button = findViewById<Button>(gridButtons[r][c])
                when (status[r][c]) {
                    "Hit" -> button.setBackgroundColor(Color.RED)
                    "Miss" -> button.setBackgroundColor(Color.BLUE)
                    "Sunk" -> button.setBackgroundColor(Color.BLACK)
                    else -> {
                        if (board.grid[r][c] != null) {
                            button.setBackgroundColor(Color.DKGRAY)
                            button.isEnabled = false
                        } else {
                            button.background = originalBackground
                            button.isEnabled = clickableIfEmpty
                        }
                    }
                }
            }
        }
    }
    fun redrawEnemyGrid() {
        for (r in 0..7) {
            for (c in 0..7) {
                val button = findViewById<Button>(enemyGrid[r][c])
                when (enemyGridStatus[r][c]) {
                    "Empty" -> button.background = originalBackground
                    "Hit" -> button.setBackgroundColor(Color.RED)
                    "Miss" -> button.setBackgroundColor(Color.BLUE)
                    "Sunk" -> button.setBackgroundColor(Color.BLACK)
                }
                button.isEnabled = enemyGridStatus[r][c] == "Empty"
            }
        }
    }

    fun handlePlayerMove(row: Int, col: Int, button: Button) {
        val attackResult = gameManager.attack(row, col)
        enemyGridStatus[row][col] = attackResult.status
        if (attackResult.status == "Sink") {
            for (cord in attackResult.sunkShipCoordinates!!) {
                enemyGridStatus[cord.first][cord.second] = "Sunk"
            }
        }
        updateButtonUI(button, attackResult, enemyGrid)

        if (handleGameOverUI()) return

        val (botRow, botCol) = gameManager.getBotMove()
        val botResult = gameManager.attack(botRow, botCol)
        myGridStatus[botRow][botCol] = botResult.status
        if (botResult.status == "Sink") {
            for (cord in botResult.sunkShipCoordinates!!) {
                myGridStatus[cord.first][cord.second] = "Sunk"
            }
        }
        updateButtonUI(findViewById(myGrid[botRow][botCol]), botResult, myGrid)

        handleGameOverUI()
    }
    fun saveWinner(winner: String) {
        val prefs = getSharedPreferences("BATTLESHIP_SCORES", MODE_PRIVATE)
        val editor = prefs.edit()

        when(winner) {
            "Player" -> {
                val playerWins = prefs.getInt("PLAYER_WINS", 0) + 1
                editor.putInt("PLAYER_WINS", playerWins)
            }
            "Enemy" -> {
                val botWins = prefs.getInt("BOT_WINS", 0) + 1
                editor.putInt("BOT_WINS", botWins)
            }
        }

        editor.apply()
    }

    fun handleGameOverUI() : Boolean {
        if (!gameManager.isGameOver()) {
            return false
        }
        // else, game over
        val winner = gameManager.getWinner()
        saveWinner(winner)

        disableEnemyGrid()
        showGameOverDialog(winner)
        return true
    }

    fun updateButtonUI(button: Button, attackResult: AttackResult, gridButtons: Array<IntArray>) {
        when (attackResult.status) {
            "Hit" -> button.setBackgroundColor(Color.RED)
            "Miss" -> button.setBackgroundColor(Color.BLUE)
            "Sink" -> {
                for (cell: Pair<Int, Int> in attackResult.sunkShipCoordinates!!) {
                    val button = findViewById<Button>(gridButtons[cell.first][cell.second])
                    button.setBackgroundColor(Color.BLACK)
                }
                Toast.makeText(this, attackResult.defender+" "+attackResult.shipName+" is SUNK!", Toast.LENGTH_SHORT).show()
            }
        }
        button.isEnabled = false
    }

    fun disableEnemyGrid() {
        for (r in 0..7) {
            for (c in 0..7) {
                findViewById<Button>(enemyGrid[r][c]).isEnabled = false
            }
        }
    }

    fun showGameOverDialog(winner: String) {
        AlertDialog.Builder(this)
            .setTitle("Game Over")
            .setMessage( "$winner wins!")
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ ->
                val intent = Intent(this, Start::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            .show()
    }
}