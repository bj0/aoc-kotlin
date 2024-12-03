package year2023

import util.*
import util.Direction.*
import kotlin.math.max

fun main() {
    listOf(::solution).solveAll(
//        InputProvider.raw(
//            """.|...\....
//|.-.\.....
//.....|-...
//........|.
//..........
//.........\
//..../.\\..
//.-.-/..|..
//.|....-|.\
//..//.|...."""
//        )
    )
}

private val solution = puzzle {

    val parser = parser {
        buildMap {
            lines.forEachIndexed { j, line ->
                line.forEachIndexed { i, c ->
                    put(i point j, c)
                }
            }
        }
    }

    fun Map<IntPoint, Char>.energized(start: IntPoint, direction: Direction) = beam(start to direction).count()

    part1(parser) { map ->
        val start = 0 point 0
        map.energized(start, Right)
    }

    part2 {
        val map = parser.parse()
        val height = lines.size
        val width = lines.first().length
        max(
            lines.first().indices.maxOf { i ->
                max(map.energized(IntPoint(i, 0), Down), map.energized(IntPoint(i, height), Up))
            },
            lines.indices.maxOf { j ->
                max(map.energized(IntPoint(0, j), Right), map.energized(IntPoint(width, j), Left))
            })
    }
}


private fun Map<IntPoint, Char>.beam(ray: Pair<IntPoint, Direction>): List<IntPoint> {
    val seen = mutableSetOf<Pair<IntPoint, Direction>>()
    DeepRecursiveFunction<Pair<IntPoint, Direction>, Unit> { ray ->
        if (ray in seen) return@DeepRecursiveFunction
        val (p, dir) = ray
        if (p !in this@beam) return@DeepRecursiveFunction
        seen.add(ray)
        when (this@beam[p]) {
            '/' -> callRecursive(
                when (dir) {
                    Right -> p + Up to Up
                    Down -> p + Left to Left
                    Left -> p + Down to Down
                    Up -> p + Right to Right
                }
            )

            '\\' -> callRecursive(
                when (dir) {
                    Right -> p + Down to Down
                    Down -> p + Right to Right
                    Left -> p + Up to Up
                    Up -> p + Left to Left
                }
            )

            '|' -> when (dir) {
                Up, Down -> callRecursive(p + dir to dir)
                Left, Right -> {
                    callRecursive(p + Up to Up)
                    callRecursive(p + Down to Down)
                }
            }

            '-' -> when (dir) {
                Left, Right -> callRecursive(p + dir to dir)
                Up, Down -> {
                    callRecursive(p + Left to Left)
                    callRecursive(p + Right to Right)
                }
            }

            '.' -> callRecursive(p + dir to dir)
        }
    }(ray)

    return seen.map { it.first }.distinct()
}
