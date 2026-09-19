package com.example.battleship.classes

class Board () {
    val grid: Array<Array<Ship?>> = Array(8) { Array(8) { null } }
    val ships: MutableList<Ship> = mutableListOf()

    // Reconstruct with primitive types
    constructor(initialGrid: Array<String>?) : this() {
        if (initialGrid == null) return

        val shipMap = mutableMapOf<String, Ship>()

        for (r in 0..7) {
            for (c in 0..7) {
                val index = r * 8 + c
                val cellData = initialGrid[index]
                if (cellData != "null") {
                    val parts = cellData.split("|")
                    val shipName = parts[0]

                    val ship = shipMap.getOrPut(shipName) {
                        val newShip = Ship(parts.joinToString("|"))
                        newShip
                    }

                    grid[r][c] = ship
                    if (!ships.contains(ship)) ships.add(ship)
                }
            }
        }
    }

    fun isPlacementValid(row: Int, col: Int, ship: Ship, direction: String): Boolean {

        val shipSize = ship.size

        if (direction == "Vertical") {
            if (row + shipSize - 1 >= 8) {
                return false
            }
            for (i in (0..shipSize-1)) {
                if (grid[row+i][col] != null) {
                    return false
                }
            }
        }

        if (direction == "Horizontal") {
            if (col + shipSize - 1 >= 8) {
                return false
            }
            for (i in (0..shipSize-1)) {
                if (grid[row][col+i] != null) {
                    return false
                }
            }
        }
        return true
    }

    // Always used with, isPlacementValid method
    fun placeShip(row: Int, col: Int, ship: Ship, direction: String) {
        ships.add(ship)

        val shipSize = ship.size

        if (direction == "Vertical") {
            for (i in 0..shipSize-1) {
                grid[row + i][col] = ship
            }
        }

        if (direction == "Horizontal") {
            for (i in 0..shipSize-1) {
                grid[row][col + i] = ship
            }
        }
    }

    fun isAllShipsSunk() : Boolean {
        for (ship in ships) {
            if (ship.hits != ship.size) {
                return false
            }
        }
        return true
    }

    fun reset() {
        for (row in 0..7) {
            for (col in 0..7) {
                grid[row][col] = null
            }
        }
    }

    fun convertToPrimitives(): Array<String> {
        val primitiveGrid = Array(8) { Array(8) { "null" } }

        for (r in 0..7) {
            for (c in 0..7) {
                primitiveGrid[r][c] = grid[r][c]?.convertToPrimitive() ?: "null"
            }
        }
        return primitiveGrid.flatten().toTypedArray()
    }

    fun placeShipsRandomly() {
        val ships = listOf(
            Ship("Carrier", 5),
            Ship("Battleship", 4),
            Ship("Cruiser", 3),
            Ship("Destroyer1", 2),
            Ship("Destroyer2", 2),
            Ship("Submarine1", 1),
            Ship("Submarine2", 1)
        )

        for (ship in ships) {
            var placed = false
            while (!placed) {
                val direction = if ((0..1).random() == 0) "Horizontal" else "Vertical"
                val row = (0..7).random()
                val col = (0..7).random()
                if (isPlacementValid(row, col, ship, direction)) {
                    placeShip(row, col, ship, direction)
                    placed = true
                }
            }
        }
    }
}