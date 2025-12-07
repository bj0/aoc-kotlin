package year2025

import util.InputProvider
import util.Solutions
import util.debug
import util.part1
import util.part2
import util.point
import util.solution
import util.solveAll
import util.toMapGrid
import util.toStringGrid

fun main() {
    Day7.solveAll(
        InputProvider.raw(
            """
.......S.......
.......|.......
......|^|......
......|.|......
.....|^|^|.....
...............
.....^.^.^.....
...............
....^.^...^....
...............
...^.^...^.^...
...............
..^...^.....^..
...............
.^.^.^.^.^...^.
...............""".trimIndent()
        )
    )
    Day7.solveAll()
}

object Day7 : Solutions {
    val first = solution {
        part1 {
            val grid = lines.toMapGrid()
            val start = grid.find { _, c -> c == 'S' }!!

            var set = setOf(start.x)
            var count = 0L
            grid.yRange.drop(1).forEach { y ->
                set = buildSet {
                    set.forEach { x ->
                        if (grid[x point y] == '^') {
                            add(x - 1)
                            add(x + 1)
                            count++
                        } else add(x)
                    }
                }
            }

            count
        }

        part2 {
            val grid = lines.toStringGrid()
            val start = grid.find { _, c -> c == 'S' }!!

            var map = mapOf(start.x to 1L)
            grid.yRange.drop(1).forEach { y ->
                map = buildMap {
                    map.forEach { (x, t) ->
                        if (grid[x point y] == '^') {
                            put(x - 1, t + getOrDefault(x - 1, 0))
                            put(x + 1, t)
                        } else put(x, t + getOrDefault(x, 0))
                    }
                }
            }

            map.values.sum()
        }
    }

    val fold = solution {
        fun <K> MutableMap<K, Long>.add(key: K, n: Long) {
            put(key, getOrDefault(key, 0) + n)
        }

        part2 {
            lines.drop(1).fold(
                buildMap {
                    for ((i, c) in lines.first().withIndex()) {
                        if (c == 'S') put(i, 1L)
                    }
                }
            ) { acc, line ->
                buildMap {
                    for ((i, n) in acc) {
                        if (line.getOrNull(i) == '^') {
                            add(i - 1, n)
                            add(i + 1, n)
                        } else {
                            add(i, n)
                        }
                    }
                }
            }.values.sum()
        }
    }
}
