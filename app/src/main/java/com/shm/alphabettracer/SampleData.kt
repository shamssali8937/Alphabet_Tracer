package com.shm.alphabettracer

object SampleData {
    // simple words for A-Z (expand to your preference)
    fun wordFor(c: Char): String {
        return when (c) {
            'A' -> "apple"
            'B' -> "ball"
            'C' -> "cat"
            'D' -> "dog"
            'E' -> "elephant"
            'F' -> "fish"
            'G' -> "grapes"
            'H' -> "house"
            'I' -> "ice cream"
            'J' -> "juice"
            'K' -> "kite"
            'L' -> "lion"
            'M' -> "monkey"
            'N' -> "nest"
            'O' -> "orange"
            'P' -> "pig"
            'Q' -> "queen"
            'R' -> "rabbit"
            'S' -> "sun"
            'T' -> "tree"
            'U' -> "umbrella"
            'V' -> "violin"
            'W' -> "whale"
            'X' -> "xylophone"
            'Y' -> "yacht"
            'Z' -> "zebra"
            else -> "item"
        }
    }

    fun imageFor(c: Char): Int {
        return when (c.uppercaseChar()) {
            'A' -> R.mipmap.apple
            'B' -> R.mipmap.ball
            'C' -> R.mipmap.cat
            'D' -> R.mipmap.dog
            'E' -> R.mipmap.elephant
            'F' -> R.mipmap.fruits
            'G' -> R.mipmap.g
            'H' -> R.mipmap.hut
            'I' -> R.mipmap.ink
            'J' -> R.mipmap.jaguar
            'K' -> R.mipmap.kite
            'L' -> R.mipmap.lion
            'M' -> R.mipmap.mouse
            'N' -> R.mipmap.noteboook
            'O' -> R.mipmap.owl
            'P' -> R.mipmap.parrot
            'Q' -> R.mipmap.queen
            'R' -> R.mipmap.rose
            'S' -> R.mipmap.sea
            'T' -> R.mipmap.turtle
            'U' -> R.mipmap.umbrella
            'V' -> R.mipmap.voiln
            'W' -> R.mipmap.whale
            'X' -> R.mipmap.xray
            'Y' -> R.mipmap.kangroo
            'Z' -> R.mipmap.zebra
            else -> R.mipmap.ic_launcher // fallback image
        }
   }
}
