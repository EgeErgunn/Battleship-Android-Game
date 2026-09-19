package com.example.battleship.classes

class ShipPlacementManager (val board: Board) {
    fun placeShip (row: Int, col: Int, ship: Ship, direction: String): Boolean {
        if (!board.isPlacementValid(row, col, ship, direction)) {
            return false
        }
        board.placeShip(row, col, ship, direction)

        return true
    }
    fun reset() {
        board.reset()
    }

    // Return a Int pair for UI to draw
    fun cellsToPlace(row: Int, col: Int, ship: Ship, direction: String): List<Pair<Int, Int>> {
        val cells = mutableListOf<Pair<Int, Int>>()
        val shipSize = ship.size

        if (direction == "Vertical") {
            for (i in 0..shipSize-1) {
                cells.add(Pair(row + i, col))
            }
        } else if (direction == "Horizontal") {
            for (i in 0..shipSize-1) {
                cells.add(Pair(row, col + i))
            }
        }
        return cells
    }
}