package year2023

import arrow.core.fold
import util.*

fun main() {
    listOf(Day1::regex, Day1::easier).solveAll()
}

object Day1 {
    val regex = solution {
        // using regex
        part1 {
            lines.sumOf { line -> "${line.first { it.isDigit() }}${line.last { it.isDigit() }}".toInt() }
        }

        part2 {
            val words = mapOf(
                "one" to "1",
                "two" to "2",
                "three" to "3",
                "four" to "4",
                "five" to "5",
                "six" to "6",
                "seven" to "7",
                "eight" to "8",
                "nine" to "9"
            )
            val pat = """(?=(\d|${words.keys.joinToString("|")}))""".toRegex()
            lines.sumOf {
                val nums = it.groupValues(pat).flatten()
                ((words[nums.first()] ?: nums.first()) + (words[nums.last()] ?: nums.last())).toInt()
            }
        }
    }

    val easier = solution {
        part2 {
            // manipulate input to make search easier
            val words = mapOf(
                "one" to "o1e",
                "two" to "t2o",
                "three" to "t3e",
                "four" to "f4r",
                "five" to "f5e",
                "six" to "s6x",
                "seven" to "s7n",
                "eight" to "e8t",
                "nine" to "n9e"
            )

            lines.sumOf { line ->
                words.fold(line) { acc, (k, v) -> acc.replace(k, v) }
                    .filter { it.isDigit() }.let { "${it.first()}${it.last()}".toInt() }
            }
        }
    }
}