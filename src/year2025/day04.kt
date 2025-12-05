package year2025

import util.Grid
import util.InputProvider
import util.Solutions
import util.neighbors
import util.solution
import util.solveAll
import util.toMapGrid
import kotlin.collections.plus

fun main() {
    Day4.solveAll(
        InputProvider.raw(
            """
                ..@@.@@@@.
                @@@.@.@.@@
                @@@@@.@.@@
                @.@@@@..@.
                @@.@@@@.@@
                .@@@@@@@.@
                .@.@.@.@@@
                @.@@@.@@@@
                .@@@@@@@@.
                @.@.@@@.@.
            """.trimIndent()
        )
    )
    Day4.solveAll()
}

object Day4 : Solutions {
    fun dirs(c: Int, r: Int) = sequence {
        for (i in -1..1)
            for (j in -1..1)
                if (i == 0 && j == 0) continue else yield((c + i) to (r + j))
    }

    val first = solution {
        part1 {
            val map = buildMap {
                lines.withIndex().forEach { (r, row) ->
                    row.withIndex().forEach { (c, v) ->
                        put((c to r), v)
                    }
                }
            }//.debug()

            map.entries.count { (pos, v) ->
                val (c, r) = pos
                v == '@' && (dirs(c, r).count { map[it] == '@' } < 4)
            }

        }

        part2 {
            val map = buildMap {
                lines.withIndex().forEach { (r, row) ->
                    row.withIndex().forEach { (c, v) ->
                        put((c to r), v)
                    }
                }
            }

            tailrec fun remove(map: Map<Pair<Int, Int>, Char>, count: Long = 0): Long {
                val toRemove = map.entries.filter { (pos, v) ->
                    val (c, r) = pos
                    v == '@' && (dirs(c, r).count { map[it] == '@' } < 4)
                }.map { it.key }
                if (toRemove.isEmpty()) return count
                return remove(map + toRemove.associateWith { '.' }, count + toRemove.size)
            }

            remove(map)
        }
    }

    val grid = solution {
        val parser = parser { lines.toMapGrid() }

        part1(parser) { grid ->
            grid.findAll { pos, v -> v == '@' && pos.neighbors(true).count { grid[it] == '@' } < 4 }.count()
        }

        part2(parser) { grid ->
            tailrec fun remove(grid: Grid<Char>, count: Long = 0): Long {
                val toRemove = grid.findAll { pos, v -> v == '@' && pos.neighbors(true).count { grid[it] == '@' } < 4 }
                if (toRemove.isEmpty()) return count
                return remove(grid.copy(toRemove.associateWith { '.' }), count + toRemove.size)
            }

            remove(grid)
        }
    }
}
