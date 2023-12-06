package year2023

import util.PuzDSL
import util.groupValues
import util.solveAll

object Day01 : PuzDSL({

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
})

fun main() {
    Day01.solveAll(
//        input = InputProvider.Example
    )
}
