package year2024

import util.*
import kotlin.math.abs


fun main() {
//    listOf(Day1::solution, Day1::cleaner, Day1::group).solveAll()
    listOf(Day2::solution, Day2::opt).solveAll(
//        InputProvider.raw("""
//            7 6 4 2 1
//            1 2 7 8 9
//            9 7 6 2 1
//            1 3 2 4 5
//            8 6 4 4 1
//            1 3 6 7 9
//        """.trimIndent())
    )
}

object Day2 : Solutions {

    val opt = solution {
        val reports = lineParser { it.getIntList() }

        fun safe(levels: List<Int>) = levels.zipWithNext { x, y -> y - x }.let { diffs ->
            diffs.all { it < 0 && it in -3..-1 } ||
                    diffs.all { it > 0 && it in 1..3 }
        }

        part1(reports) {
            it.count { report -> safe(report) }
        }

        part2(reports) {
            it.count { report ->
                safe(report) || report.indices.any { i -> safe(report.omit(i)) }
            }

        }
    }
    val solution = solution {
        val reports = lineParser { it.getIntList() }

        fun safe(levels: List<Int>) = levels.windowed(2).map { (x, y) -> y - x }
            .let { diffs ->
                (diffs.all { it > 0 } || diffs.all { it < 0 })
                        && diffs.all { abs(it) in 1..3 }
            }

        part1(reports) {
            it.count { report -> safe(report) }
        }

        part2(reports) {
            it.count { report ->
                safe(report) || report.indices.any { i -> safe(report.omit(i)) }
            }

        }
    }
}