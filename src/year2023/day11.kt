package year2023

import util.*
import year2023.Day11Independent.solve

fun main() {
    listOf(Day11, Day11Independent).solveAll(
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
            lines.forEachIndexed { row, line ->
                line.forEachIndexed { col, c ->
                    if (c == '#')
                        add(expandedX(col.toLong()) point expandedY(row.toLong()))
                }
            }
        }
    }

    part1(parser()) { galaxies ->
        sequence {
            val seen = mutableSetOf<LongPoint>()
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
            val seen = mutableSetOf<LongPoint>()
            galaxies.forEach { p ->
                seen += p
                yieldAll((galaxies - seen).map { p to it })
            }
        }.sumOf { (a, b) -> (a mdist b) }
    }
})

object Day11Independent : PuzDSL({

    part1 {
        lines.solve(2)
    }

    part2 {
        lines.solve(1_000_000)
    }

}) {
    fun List<String>.solve(n: Long): Long =
        solve1(map { it.count('#'::equals) }, n) + solve1(
            0.until(maxOfOrNull { it.length } ?: 0).map { x ->
                count { x < it.length && it[x] == '#' }
            },
            n
        )

    fun solve1(data: List<Int>, n: Long): Long {
        var total = 0L
        for ((i, a) in data.withIndex()) {
            if (a == 0) continue
            var m = 0L
            for (j in i + 1..data.lastIndex) {
                val b = data[j]
                m += if (b == 0) n else 1
                total += m * a * b
            }
        }
        return total
    }
}