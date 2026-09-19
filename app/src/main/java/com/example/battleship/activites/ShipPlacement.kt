package com.example.battleship.activites

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.battleship.R
import com.example.battleship.classes.Board
import com.example.battleship.classes.Ship
import com.example.battleship.classes.ShipPlacementManager

class ShipPlacement : AppCompatActivity() {
    lateinit var board: Board
    lateinit var placementManager: ShipPlacementManager
    lateinit var spinnerShip: Spinner
    lateinit var spinnerDir: Spinner
    lateinit var shipAdapter: ArrayAdapter<Ship>
    lateinit var dirAdapter: ArrayAdapter<String>
    lateinit var idMatrix: Array<IntArray>
    lateinit var originalBackground: Drawable
    lateinit var ships: MutableList<Ship>
    lateinit var selectedShip: Ship
    var selectedDirection = "Horizontal"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ship_placement)

        // Ships
        val carrier = Ship("Carrier",5)
        val battleship = Ship("Battleship",4)
        val cruiser = Ship("Cruiser", 3)
        val destroyer1 = Ship("Destroyer1", 2)
        val destroyer2 = Ship("Destroyer2", 2)
        val submarine1 = Ship("Submarine1", 1)
        val submarine2 = Ship("Submarine2", 1)

        ships = mutableListOf(carrier, battleship, cruiser, destroyer1, destroyer2, submarine1, submarine2)

        // Ship Spinner
        spinnerShip = findViewById(R.id.spinner)
        shipAdapter = ArrayAdapter<Ship>(this, android.R.layout.simple_spinner_dropdown_item, ships)
        spinnerShip.adapter = shipAdapter
        spinnerShip.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedShip = ships[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        // Direction Spinner
        val directions = arrayOf("Horizontal", "Vertical")
        selectedDirection = "Horizontal"

        spinnerDir = findViewById(R.id.spinner2)
        dirAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, directions)
        spinnerDir.adapter = dirAdapter
        spinnerDir.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedDirection = directions[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        // GRID
        idMatrix = arrayOf(
            intArrayOf(R.id.button2,  R.id.button8,  R.id.button9,  R.id.button10, R.id.button11, R.id.button12, R.id.button13, R.id.button14),
            intArrayOf(R.id.button27, R.id.button25, R.id.button24, R.id.button23, R.id.button26, R.id.button29, R.id.button28, R.id.button30),
            intArrayOf(R.id.button35, R.id.button33, R.id.button32, R.id.button31, R.id.button34, R.id.button37, R.id.button36, R.id.button38),
            intArrayOf(R.id.button43, R.id.button41, R.id.button40, R.id.button39, R.id.button42, R.id.button45, R.id.button44, R.id.button46),
            intArrayOf(R.id.button51, R.id.button49, R.id.button48, R.id.button47, R.id.button50, R.id.button53, R.id.button52, R.id.button54),
            intArrayOf(R.id.button59, R.id.button57, R.id.button56, R.id.button55, R.id.button58, R.id.button61, R.id.button60, R.id.button62),
            intArrayOf(R.id.button67, R.id.button65, R.id.button64, R.id.button63, R.id.button66, R.id.button69, R.id.button68, R.id.button70),
            intArrayOf(R.id.button75, R.id.button78, R.id.button77, R.id.button74, R.id.button71, R.id.button72, R.id.button76, R.id.button73)
        )

        originalBackground = findViewById<Button>(R.id.button2).background

        // Restore Board
        val savedGrid = savedInstanceState?.getStringArray("BOARD_STATE")
        board = if (savedGrid != null) Board(savedGrid) else Board()
        placementManager = ShipPlacementManager(board)

        // Restore Remaining Ships
        val remainingShips = savedInstanceState?.getStringArrayList("REMAINING_SHIPS")
        if (remainingShips != null) {
            ships.retainAll { remainingShips.contains(it.name) }
            shipAdapter.notifyDataSetChanged()
        }

        spinnerShip.setSelection(savedInstanceState?.getInt("SHIP_INDEX") ?: 0)
        spinnerDir.setSelection(savedInstanceState?.getInt("DIR_INDEX") ?: 0)

        redrawGrid()

        // Grid Button Listeners
        for (row in 0..7) {
            for (col in 0..7) {

                val button = findViewById<Button>(idMatrix[row][col])

                button.setOnClickListener {
                    // Placing ship
                    if (placementManager.placeShip(row, col, selectedShip, selectedDirection)) {

                        val cells = placementManager.cellsToPlace(row, col, selectedShip, selectedDirection)

                        // Redraw placed ship buttons
                        for (cell: Pair<Int, Int> in cells) {
                            val button = findViewById<Button>(idMatrix[cell.first][cell.second])
                            button.setBackgroundColor(Color.DKGRAY)
                            button.isEnabled = false
                        }

                        // Remove ship from spinner
                        ships.remove(selectedShip)
                        shipAdapter.notifyDataSetChanged()

                        // Check spinner empty
                        if (ships.isNotEmpty()) {
                            selectedShip = ships[0]
                            spinnerShip.setSelection(0)
                        } else {
                            Toast.makeText(this, "All ships placed!", Toast.LENGTH_LONG).show()
                            for (row in 0..7) {
                                for (col in 0..7) {
                                    findViewById<Button>(idMatrix[row][col]).isEnabled = false
                                }
                            }
                        }
                    }
                }
            }
        }

        // Reset Button
        val resetButton = findViewById<Button>(R.id.button3)
        resetButton.setOnClickListener {
            placementManager.reset()

            // Redraw buttons
            for (row in 0..7) {
                for (col in 0..7) {
                    val button = findViewById<Button>(idMatrix[row][col])
                    button.background = originalBackground
                    button.isEnabled = true
                }
            }
            // Reset ships and spinner
            ships.clear()
            ships.addAll(mutableListOf(carrier, battleship, cruiser, destroyer1, destroyer2, submarine1, submarine2))

            shipAdapter.notifyDataSetChanged()
            selectedShip = ships[0]
            spinnerDir.setSelection(0)

            Toast.makeText(this, "Reset done", Toast.LENGTH_SHORT).show()
        }

        // Done Button
        val doneButton = findViewById<Button>(R.id.button4)
        doneButton.setOnClickListener {
            // Check is every ship placed
            if (ships.isNotEmpty()) {
                Toast.makeText(this, "Place all ships first!", Toast.LENGTH_SHORT).show()
            }
            else {
                val intent = Intent(this, GamePlay::class.java)
                intent.putExtra("PLAYER_GRID_DATA", board.convertToPrimitives())
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArray("BOARD_STATE", board.convertToPrimitives())
        outState.putStringArrayList("REMAINING_SHIPS", ArrayList(ships.map { it.name }))
        outState.putInt("SHIP_INDEX", spinnerShip.selectedItemPosition)
        outState.putInt("DIR_INDEX", spinnerDir.selectedItemPosition)
    }
    // Redraw Ships
    fun redrawGrid() {
        for (r in 0..7) {
            for (c in 0..7) {
                val button = findViewById<Button>(idMatrix[r][c])
                if (board.grid[r][c] != null) {
                    button.setBackgroundColor(Color.DKGRAY)
                    button.isEnabled = false
                }
                else {
                    button.background = originalBackground
                    button.isEnabled = true
                }
                if (ships.isEmpty()) {
                    button.isEnabled = false
                }
            }
        }
    }
}