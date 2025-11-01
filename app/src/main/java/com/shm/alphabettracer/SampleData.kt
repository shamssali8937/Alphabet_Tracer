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

    // Unsplash example images — free to use under Unsplash license
//    fun imageFor(c: Char): String {
//        return when (c) {
//            'A' -> "https://images.unsplash.com/photo-1506806732259-39c2d0268443?auto=format&fit=crop&w=800&q=80"
//            'B' -> "https://images.unsplash.com/photo-1508051123996-69f8caf4891b?auto=format&fit=crop&w=800&q=80"
//            'C' -> "https://images.unsplash.com/photo-1518791841217-8f162f1e1131?auto=format&fit=crop&w=800&q=80"
//            'D' -> "https://images.unsplash.com/photo-1517423440428-a5a00ad493e8?auto=format&fit=crop&w=800&q=80"
//            'E' -> "https://images.unsplash.com/photo-1508182316059-3c3f2c6e482d?auto=format&fit=crop&w=800&q=80"
//            'F' -> "https://images.unsplash.com/photo-1528731708534-816fe59f90b9?auto=format&fit=crop&w=800&q=80"
//            'G' -> "https://images.unsplash.com/photo-1528825871115-3581a5387919?auto=format&fit=crop&w=800&q=80"
//            'H' -> "https://images.unsplash.com/photo-1505691723518-36a3f09b8e9b?auto=format&fit=crop&w=800&q=80"
//            'I' -> "https://images.unsplash.com/photo-1551024601-bec78aea704b?auto=format&fit=crop&w=800&q=80"
//            'J' -> "https://images.unsplash.com/photo-1504754524776-8f4f37790ca0?auto=format&fit=crop&w=800&q=80"
//            'K' -> "https://images.unsplash.com/photo-1519681393784-d120267933ba?auto=format&fit=crop&w=800&q=80"
//            'L' -> "https://images.unsplash.com/photo-1508672019048-805c876b67e2?auto=format&fit=crop&w=800&q=80"
//            'M' -> "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=800&q=80"
//            'N' -> "https://images.unsplash.com/photo-1519681393784-d120267933ba?auto=format&fit=crop&w=800&q=80"
//            'O' -> "https://images.unsplash.com/photo-1530797196325-8f5b8a6e0872?auto=format&fit=crop&w=800&q=80"
//            'P' -> "https://images.unsplash.com/photo-1517423440428-a5a00ad493e8?auto=format&fit=crop&w=800&q=80"
//            'Q' -> "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=800&q=80"
//            'R' -> "https://images.unsplash.com/photo-1518791841217-8f162f1e1131?auto=format&fit=crop&w=800&q=80"
//            'S' -> "https://images.unsplash.com/photo-1501973801540-537f08ccae7b?auto=format&fit=crop&w=800&q=80"
//            'T' -> "https://images.unsplash.com/photo-1501785888041-af3ef285b470?auto=format&fit=crop&w=800&q=80"
//            'U' -> "https://images.unsplash.com/photo-1528740561666-dc2479dc08ab?auto=format&fit=crop&w=800&q=80"
//            'V' -> "https://images.unsplash.com/photo-1512374382149-233c42b6a3f0?auto=format&fit=crop&w=800&q=80"
//            'W' -> "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80"
//            'X' -> "https://images.unsplash.com/photo-1520975861048-0a0b2b6f06c9?auto=format&fit=crop&w=800&q=80"
//            'Y' -> "https://images.unsplash.com/photo-1504198453319-5ce911bafcde?auto=format&fit=crop&w=800&q=80"
//            'Z' -> "https://images.unsplash.com/photo-1500917293891-ef795e70e1f6?auto=format&fit=crop&w=800&q=80"
//            else -> "https://images.unsplash.com/photo-1546182990-dffeafbe841d?auto=format&fit=crop&w=800&q=80"
//        }
    fun imageFor(c: Char): Int {
        return when (c.uppercaseChar()) {
            'A' -> R.mipmap.apple
            'B' -> R.mipmap.ball
            'C' -> R.mipmap.cat
            'D' -> R.mipmap.dog
            'E' -> R.mipmap.elephant
            'F' -> R.mipmap.fruits
            'G' -> R.mipmap.g
            'H' -> R.mipmap.hello
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
            'Z' -> R.mipmap.zoo
            else -> R.mipmap.ic_launcher // fallback image
        }
   }
}
