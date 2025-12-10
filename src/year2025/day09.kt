package year2025

import util.*
import kotlin.math.abs

fun main() {
    Day9.solveAll(
        InputProvider.raw(
            """
                7,1
                11,1
                11,7
                9,7
                9,5
                2,5
                2,3
                7,3
            """.trimIndent()
        )
    )
    Day9.solveAll()
}

object Day9 : Solutions {


    val first = solution {
        fun List<LongPoint>.combinations() = sequence {
            dropLast(1).forEachIndexed { i, point ->
                drop(i + 1).forEach { point2 ->
                    yield(point to point2)
                }
            }
        }

        part1 {
            val points = lines.map { it.getLongList().let { (x, y) -> x point y } }
            points.combinations().maxOf { (a, b) ->
                (abs(a.x - b.x) + 1) * (abs(a.y - b.y) + 1)
            }
        }

        part2 {
        }
    }

}
