package year2024

import util.*

fun main() {
//    Day14.solveAll(
//        InputProvider.raw(
//            """
//                p=0,4 v=3,-3
//                p=6,3 v=-1,-3
//                p=10,3 v=-1,2
//                p=2,0 v=2,-1
//                p=0,0 v=1,3
//                p=3,0 v=-2,-2
//                p=7,6 v=-1,-3
//                p=3,0 v=-1,-2
//                p=9,3 v=2,3
//                p=7,3 v=-1,2
//                p=2,4 v=2,-3
//                p=9,5 v=-3,-3
//    """.trimIndent()
//        )
//    )
    Day14.solveAll()
}

object Day14 : Solutions {
    data class Bot(val pos: LongPoint, val velocity: LongPoint) {
        fun move(steps: Long = 1L) = pos + velocity * steps
    }

    val solution = puzzle {
        val parser =
            lineParser { it.getLongList().let { (px, py, vx, vy) -> Bot(LongPoint(px, py), LongPoint(vx, vy)) } }
        part1(parser) { bots ->
            val sz = 101L point 103L

            val moved = bots.map { b -> b.copy(pos = b.move(100).mod(sz)) }

            val (halfW, halfH) = sz / 2L
            listOf(
                moved.count { it.pos.x < halfW && it.pos.y < halfH },
                moved.count { it.pos.x > halfW && it.pos.y < halfH },
                moved.count { it.pos.x < halfW && it.pos.y > halfH },
                moved.count { it.pos.x > halfW && it.pos.y > halfH }
            ).product()
        }

        part2(parser) { bots ->
            val sz = 101L point 103L
            var moved = bots
            for (i in 0..10_000) {
                moved = moved.map { b -> b.copy(pos = b.move().mod(sz)) }
                val locations = moved.map { it.pos }.toSet()

                // the tree has a border so lets just look for a line of robots
                if (locations.any { loc ->
                        (1L..10).all { loc + it * (1L point 1) in locations }
                    }) {
                    moved.genPicture().printIt()
                    print("found at ${i + 1}!")
                    break
                }
            }
        }
    }

    private fun List<Bot>.genPicture() = buildList {
        val (width, height) = 101L to 103L
        val locations = this@genPicture.map { it.pos }.toSet()
        for (r in 0..<height) {
            add(buildString {
                for (c in 0..<width) {
                    append(if ((c point r) in locations) "x" else ".")
                }
            })
        }
    }
}


private fun List<String>.printIt() {
    println("")
    for (r in this) {
        println(r)

    }
    println("")
    println("")
    println("")
}
