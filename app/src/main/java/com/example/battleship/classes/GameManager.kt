package com.example.battleship.classes

class GameManager(val playerBoard: Board, val enemyBoard: Board) {
    var playerTurn: Boolean = true
    val botMoves: MutableList<Int> = (0..63).toMutableList()

    fun attack(row: Int, col: Int): AttackResult {
        val defender = if (playerTurn) "Enemy's" else "Your"
        val targetBoard = if (playerTurn) enemyBoard else playerBoard
        playerTurn = !playerTurn

        val targetShip = targetBoard.grid[row][col]

        if (targetShip == null) {
            return AttackResult("Miss", defender = defender)
        }

        targetShip.hits++

        if (targetShip.hits == targetShip.size) {
            val coords = mutableListOf<Pair<Int, Int>>()
            for (r in 0..7) {
                for (c in 0..7) {
                    if (targetBoard.grid[r][c] == targetShip) {
                        coords.add(Pair(r, c))
                    }
                }
            }
            return AttackResult("Sink", coords, targetShip.name, defender)
        }
        return AttackResult("Hit", defender = defender)
    }

    fun isGameOver() : Boolean {
        return playerBoard.isAllShipsSunk() || enemyBoard.isAllShipsSunk()
    }

    fun getWinner(): String {
        if (playerBoard.isAllShipsSunk()) {
            return "Enemy"
        }
        else {
            return "Player"
        }
    }
    fun getBotMove() : Pair<Int, Int> {
        if (botMoves.isEmpty()) {
            throw IllegalStateException("No bot moves left")
        }

        val index = botMoves.indices.random()
        val cell = botMoves[index]

        botMoves.removeAt(index)

        return Pair(cell / 8, cell % 8)
    }
}