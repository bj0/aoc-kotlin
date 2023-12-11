package year2023

import util.*

fun main() {
    listOf(Day11).solveAll(
//        InputProvider.Example
    )
}

object Day11 : PuzDSL({

    fun parser(n: Long = 2) = parser {
        val ys = lines.indices.filter { lines[it].all { c -> c == '.' } }
        val xs = lines.first().indices.filter { lines.all { line -> line[it] == '.' } }
        fun expandedX(x: Long) = x + xs.count { it < x } * (n - 1)
        fun expandedY(y: Long) = y + ys.count { it < y } * (n - 1)
        buildList {
            lines.forEachIndexed  { row, line ->
                line.forEachIndexed { col, c ->
                    if (c == '#')
                        add(PointL(expandedX(col.toLong()), expandedY(row.toLong())))
                }
            }
        }
    }

    part1(parser()) { galaxies ->
        sequence {
            val seen = mutableSetOf<PointL>()
            galaxies.forEach { p ->
                seen += p
                yieldAll((galaxies - seen).map { p to it })
            }
        }.sumOf { (a, b) -> (a mdist b) }
    }

    //625243292686
    part2(parser(1_000_000L)) { galaxies ->
//        galaxies.toList().let { gs -> gs.indices.flatMap { i -> gs.drop(i + 1).map { gs[i] to it } } }.size.debug()
        sequence {
            val seen = mutableSetOf<PointL>()
            galaxies.forEach { p ->
                seen += p
                yieldAll((galaxies - seen).map { p to it })
            }
        }.sumOf { (a, b) -> (a mdist b) }
    }
})
