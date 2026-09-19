package com.example.battleship.classes

data class Ship (val name : String, val size : Int){
    var hits = 0

    // Reconstruct with primitive types
    constructor(primitiveShip: String) : this(name = primitiveShip.split("|")[0], size = primitiveShip.split("|")[1].toInt()) {
        hits = primitiveShip.split("|")[2].toInt()
    }

    override fun toString(): String{
        return "$name ($size)"
    }

    fun convertToPrimitive(): String {
        return "$name|$size|$hits"
    }
}