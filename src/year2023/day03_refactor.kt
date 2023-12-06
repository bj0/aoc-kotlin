package year2023

import util.InputProvider
import util.PuzDSL
import util.solveAll
import year2023.Day03.findNumber
import year2023.Day03.neighbors
import kotlin.math.max
import kotlin.math.min

fun main() {

    Day03.solveAll(
        input = InputProvider.Example
    )

//    fun part2(input: List<String>): Int = with(input.grid()) {
//        find('*').map { p ->
//            p.neighbors().mapNotNull { findNumber(it) }.toSet()
//        }.filter { it.size == 2 }
//            .map { it.map { (_, n) -> n.toInt() }.reduce(Int::times) }
//            .sum()
//    }
//
//    // test if implementation meets criteria from the description, like:
//    val testInput = readInput("day03_test")
//    check(part1(testInput) == 4361)
//
//    val input = readInput("day03")
//    part1(input).println()
//    part2(input).println()
}

object Day03 : PuzDSL({

    val parser = parser {
        Grid(lines.flatMapIndexed { j, row ->
            row.mapIndexedNotNull { i, char ->
                if (char != '.') Point(i, j) to char else null
            }
        }.toMap())
    }

    part1(parser) { grid ->
        with(grid) {
            data.filterValues { !it.isDigit() }.flatMap { (p, _) ->
                p.neighbors().mapNotNull { findNumber(it) }
            }.toSet()
                .sumOf { (_, n) -> n.toInt() }
        }
    }

}) {
    data class Point(val x: Int, val y: Int)

    operator fun Point.plus(other: Point) = Point(x + other.x, y + other.y)
    operator fun Point.unaryMinus() = Point(-x, -y)
    operator fun Point.minus(other: Point) = this + (-other)
    data class Grid<T>(val data: Map<Point, T>)

    fun <T> Grid<T>.find(item: T): Sequence<Point> = sequence {
        yieldAll(data.keys.filter { data[it] == item })
    }

    context(Grid<Char>)
    fun Point.neighbors() = sequence {
        (-1..1).forEach { i ->
            (-1..1).forEach { j ->
                if (!(i == 0 && j == 0)) {
                    val p = Point(x + i, y + j)
                    if (p in data)
                        yield(p)
                }
            }
        }
    }

    context(Grid<Char>)
    fun findNumber(point: Point): Pair<Point, String>? {
        if (data[point]?.isDigit() != true)
            return null
        var p = point + Point(-1, 0)
        while (data[p]?.isDigit() == true)
            p += Point(-1, 0)
        p += Point(1, 0)
        val start = p
        var num = ""
        while (data[p]?.isDigit() == true) {
            num += data[p]
            p += Point(1, 0)
        }
        return start to num
    }
}

object Day03AoK : PuzDSL({
    part1 {
        lines.mapIndexed { y, line ->
            "\\d+".toRegex().findAll(line).filter { m ->
                val sr = max(m.range.first - 1, 0)..min(m.range.last + 1, line.lastIndex)
                (max(y-1, 0)..min(y+1, lines.lastIndex))
                    .map { lines[it].substring(sr) }
                    .any { it.any { c -> c != '.' && !c.isDigit() } }
            }.sumOf { it.value.toInt() }
        }.sum()
    }
    part2 {
        lines.mapIndexed { y, line ->
            line.mapIndexedNotNull { idx, c -> idx.takeIf{ c == '*' } }.sumOf { x ->
                (max(y - 1, 0)..min(y + 1, lines.lastIndex)).flatMap {
                    "\\d+".toRegex().findAll(lines[it]).filter { m ->
                        x in m.range || (x - 1) in m.range || (x + 1) in m.range
                    }.map { it.value.toInt() }
                }.takeIf { it.size == 2 }?.let { (a, b) -> a * b } ?: 0
            }
        }.sum()
    }
})