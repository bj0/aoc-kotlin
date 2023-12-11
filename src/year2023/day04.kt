package year2023

import util.*
import kotlin.math.pow

fun main() {
    Day04.solveAll()
}

object Day04 : PuzDSL({
    val parseWins = lineParser { line ->
        line.split(":").last().split(" | ")
            .map {
//                it.trim().split("\\s+".toRegex())
                it.getIntList()
            }
            .let { (winners, yours) -> yours.count(winners::contains) }
    }

    //27059
    part1(parseWins) { wins ->
        wins.filter { it > 0 }.sumOf { 1 shl (it - 1) }
    }

    //5744979
    part2(parseWins) { wins ->
        // sum from the back
        wins.reversed().fold(listOf<Int>()) { acc, i -> listOf(acc.take(i).sum() + 1) + acc }.sum()
    }
})