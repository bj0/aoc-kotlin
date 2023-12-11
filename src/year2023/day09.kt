package year2023

import util.*

fun main() {
    listOf(Day09).solveAll(
//        InputProvider.Example
    )
}

object Day09 : PuzDSL({
    fun next(list: List<Int>): Int {
        val diffs = list.windowed(2) { (a, b) -> b - a }
        if (diffs.all { it == 0 }) return list.last()
        return list.last() + next(diffs)
    }

    part1 {
        lines.map { it.getIntList() }.sumOf(::next)
    }

    part2 {
        lines.map { it.getIntList().reversed() }.sumOf(::next)
    }
})
