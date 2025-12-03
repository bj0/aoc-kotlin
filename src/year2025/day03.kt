package year2025

import util.InputProvider
import util.Solutions
import util.puzzle
import util.solveAll


fun main() {
    Day3.solveAll(
        InputProvider.raw(
            """
            987654321111111
            811111111111119
            234234234234278
            818181911112111
        """.trimIndent()
        )
    )
    Day3.solveAll()
}

object Day3 : Solutions {
    val first = puzzle {
        part1 {
            lines.sumOf { line ->
                val max = line.dropLast(1).withIndex().maxBy { it.value }
                (max.value.toString() + line.drop(max.index + 1).max()).toInt()
            }
        }

        part2 {
            fun findMax(bank: String, n: Int): String {
                if (n == 0) return ""
                val max = bank.dropLast(n - 1).withIndex().maxBy { it.value }
                return max.value + findMax(bank.drop(max.index + 1), n - 1)
            }
            lines.sumOf { line ->
                findMax(line, 12).toLong()
            }
        }
    }

    val tailrec = puzzle {
        part2 {
            tailrec fun String.findMax(n: Int, jolts: Long = 0): Long {
                if (n == 0) return jolts
                val max = dropLast(n - 1).withIndex().maxBy { it.value }
                return drop(max.index + 1).findMax(n - 1, 10 * jolts + (max.value - '0'))
            }
            lines.sumOf { line ->
                line.findMax(12)
            }
        }

    }

    val seq = puzzle {
        part2 {
            lines.sumOf { line ->
                (12 downTo 1).fold("" to line) { (m, left), i ->
                    val max = left.dropLast(i - 1).withIndex().maxBy { it.value }
                    (m + max.value) to left.drop(max.index + 1)
                }.first.toLong()
            }
        }
    }
}

