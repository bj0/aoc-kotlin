package year2023

import util.*
import kotlin.math.pow

fun main() {
    Day04.solveAll()
}

object Day04 : PuzDSL({
    part1 {
        lines.sumOf { line ->
            val (winning, yours) = line.split(":").last().split(" | ")
                .map { it.getIntList() }
            yours.filter { winning.contains(it) }.size.takeIf { it > 0 }?.let { 2.0.pow(it - 1).toInt() } ?: 0
        }
    }

    part2 {
        val winners = lines.map { line ->
            val (winning, yours) = line.split(":").last().split(" | ")
                .map { it.getIntList() }
            yours.filter { winning.contains(it) }.size
        }

        // sum from the back
        winners.reversed().fold(listOf<Int>()) { acc, i -> listOf(acc.take(i).sum() + 1) + acc }.sum()
    }
})
