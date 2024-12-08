package year2024

import util.*

fun main() {
    Day8.solveAll(
        InputProvider.raw(
            """
            ............
            ........0...
            .....0......
            .......0....
            ....0.......
            ......A.....
            ............
            ............
            ........A...
            .........A..
            ............
            ............
        """.trimIndent()
        )
    )
    Day8.solveAll()
}

object Day8 : Solutions {

    val solution = puzzle {

        part1 {
            val grid = lines.toMapGrid()
            val signals =
                grid.filterValues { it != '.' }.entries.groupBy(keySelector = { it.value }, valueTransform = { it.key })
            signals.entries.flatMap { (_, locations) ->
                locations.flatMap { a ->
                    (locations - a).map { b ->
                        a + (b - a) * 2
                    }
                }
            }.distinct().count { it in grid }
        }

        part2 {
            val grid = lines.toMapGrid()
            val signals =
                grid.filterValues { it != '.' }.entries.groupBy(keySelector = { it.value }, valueTransform = { it.key })
            signals.entries.flatMap { (_, locations) ->
                locations.flatMap { a ->
                    (locations - a).flatMap { b ->
                        val dr = b - a
                        generateSequence(b) { it + dr }.takeWhile { it in grid }
                    }
                }
            }.distinct().count()
        }
    }
}