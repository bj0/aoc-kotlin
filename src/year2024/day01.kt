package year2024

import util.getIntList
import util.puzzle
import util.solveAll
import kotlin.math.abs
import kotlin.math.absoluteValue


fun main() {
//    listOf(Day1::solution, Day1::cleaner, Day1::group).solveAll()
    Day1.solveAll()
}

object Day1 {
    // cleaned up solution
    val cleaner = puzzle {
        val parser = parser {
            lines.map { line -> line.getIntList().let { it.first() to it.last() } }.unzip()
        }

        part1(parser) { (left, right) ->
            left.sorted().zip(right.sorted())
                .sumOf { (x, y) -> abs(x - y) }
        }

        part2(parser) { (left, right) ->
            left.sumOf { l -> right.count { it == l } * l }
        }
    }

    // try with groupBy
    val group = puzzle {
        part2 {
            lines.map { line -> line.getIntList().let { it.first() to it.last() } }.unzip()
                .let { (left, right) ->
                    val map = right.groupingBy { it }.eachCount()
                    left.sumOf { l -> l * (map[l] ?: 0) }
                }
        }
    }

    // original solution
    val solution = puzzle {
        // 2430334
        part1 {
            val lists = lines.map { it.getIntList() }
            val a = lists.map { it[0] }.sorted()
            val b = lists.map { it[1] }.sorted()
            a.zip(b).sumOf { (x, y) ->
                (x - y).absoluteValue
            }
        }

        // 28786472
        part2 {
            val lists = lines.map { it.getIntList() }
            val a = lists.map { it[0] }
            val b = lists.map { it[1] }

            a.sumOf { x -> b.count { it == x } * x }
        }
    }
}