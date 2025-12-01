package year2024

import util.*

fun main() {
    Day25.solveAll(
        InputProvider.raw(
            """
        #####
        .####
        .####
        .####
        .#.#.
        .#...
        .....

        #####
        ##.##
        .#.##
        ...##
        ...#.
        ...#.
        .....

        .....
        #....
        #....
        #...#
        #.#.#
        #.###
        #####

        .....
        .....
        #.#..
        ###..
        ###.#
        ###.#
        #####

        .....
        .....
        .....
        #....
        #.#..
        #.#.#
        #####
    """.trimIndent()
        )
    )

    Day25.solveAll()
}

object Day25 : Solutions {

    val solution = puzzle {
        part1 {
            val (keys,locks) = input.split("\n\n").map { it.lines().toMapGrid().filterValues { it == '#' } }.partition { (0L point 0) in it }

            keys.sumOf { key -> locks.count { (key.keys intersect it.keys).isEmpty() } }


        }
    }
}