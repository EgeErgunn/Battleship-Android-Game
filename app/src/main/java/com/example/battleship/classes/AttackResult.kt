package com.example.battleship.classes

data class AttackResult (val status : String,
                         val sunkShipCoordinates: List<Pair<Int, Int>>? = null,
                         val shipName : String? = null,
                         val defender: String )