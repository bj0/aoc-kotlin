package year2023

import util.groupValues
import util.println
import util.readInput

fun main() {
    fun part1(input: List<String>): Int {
        val pat = """\d""".toRegex()
        return input.sumOf { line ->
            val nums = pat.findAll(line).map(MatchResult::value)
            (nums.first() + nums.last()).toInt()
        }
    }

    fun part2(input: List<String>): Int {
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
        val wat = """(?=(\d|${words.keys.joinToString("|")}))"""
        val pat = wat.toRegex()
        return input.sumOf {
            val nums = it.groupValues(pat).flatten()
            ((words[nums.first()] ?: nums.first()) + (words[nums.last()] ?: nums.last())).toInt()
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day01_test")
    check(part1(testInput) == 142)

    val input = readInput("day01")
    part1(input).println()
    part2(input).println()
}
