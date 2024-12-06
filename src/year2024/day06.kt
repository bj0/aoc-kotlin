package year2024

import arrow.fx.coroutines.parMapUnordered
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.flowOn
import util.*
import util.GridDirection.*


fun main() {
    Day6.solveAll(
        InputProvider.raw(
            """
                ....#.....
                .........#
                ..........
                ..#.......
                .......#..
                ..........
                .#..^.....
                ........#.
                #.........
                ......#...
            """.trimIndent()
        )
    )
    Day6.solveAll()
}


@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
object Day6 : Solutions {

    private fun GridDirection.turn() = when (this) {
        East -> South
        South -> West
        West -> North
        North -> East
        else -> error("invalid direction $this")
    }

    fun Sequence<Pair<LongPoint, GridDirection>>.loops() = !all(mutableSetOf<Any>()::add)

    private fun walk(
        grid: Grid<Char>,
        start: LongPoint,
        obstacle: LongPoint? = null,
    ) = sequence {
        var cur = start
        var direction = North
        while (true) {
            yield(cur to direction)
            val next = cur.step(direction)
            when {
                next !in grid -> break
                grid[next] == '#' || next == obstacle -> direction = direction.turn()
                else -> {
                    cur = next
                }
            }
        }
    }

    val sequence = puzzle {
        fun walk(
            grid: Grid<Char>,
            start: LongPoint,
            obstacle: LongPoint? = null,
        ) = sequence {
            var cur = start
            var direction = North
            while (true) {
                yield(cur to direction)
                val next = cur.step(direction)
                when {
                    next !in grid -> break
                    grid[next] == '#' || next == obstacle -> direction = direction.turn()
                    else -> {
                        cur = next
                    }
                }
            }
        }
        part1 {
            val grid = lines.toMapGrid()

            // for some reason commenting out the locally defined walk and using the outer scope walk causes this call to error with
            // a type inference problem, but only this instance of it (not part2)
            walk(grid, grid.find { _, c -> c == '^' }!!).map { it.first }.toSet().size
        }

        part2 {

            val grid = lines.toMapGrid()

            val start = grid.find { _, c -> c == '^' }!!
            val path = walk(grid, start).map { it.first }.toSet()

            (path - start).count { walk(grid, start, it).loops() }
        }
    }

    val parallel = puzzle {
        part2 {
            val grid = lines.toMapGrid()

            val start = grid.find { _, c -> c == '^' }!!
            val path = walk(grid, start).map { it.first }.toSet()

//            (path - start).asFlow().flatMapMerge { o -> flowOf(Unit).filter { walk(grid, start, o).loops() } }.flowOn(Dispatchers.Default).count()
//            (path - start).parallelStream().map { o -> walk(grid, start, o).loops() }.filter { it }.count()
//            (path - start).asFlow().flatMapMerge { o -> flow { emit(walk(grid, start, o).loops()) } }.flowOn(Dispatchers.Default).count { it }
            (path - start).asFlow().parMapUnordered { o -> walk(grid, start, o).loops() }.flowOn(Dispatchers.Default)
                .count { it }
        }
    }

    val solution = puzzle {
        fun walk(
            grid: Grid<Char>,
            start: LongPoint,
            direction: GridDirection = North,
            obstacle: LongPoint? = null,
        ): MutableSet<Pair<LongPoint, GridDirection>>? {
            val path = mutableSetOf(start to direction)
            var cur = start
            var dir = direction
            while (true) {
                val next = cur.step(dir)
                when {
                    next !in grid -> break
                    grid[next] == '#' || next == obstacle -> dir = dir.turn()
                    else -> {
                        cur = next
                        if (cur to dir in path) return null
                        path.add(cur to dir)
                    }
                }
            }
            return path
        }


        part1 {
            val grid = lines.toMapGrid()

            val start = grid.find { _, c -> c == '^' }!!
            var direction = North
            var cur = start
            sequence {
                yield(cur)
                while (true) {
                    val next = cur.step(direction)
                    when (grid[next]) {
                        null -> break
                        '.', '^' -> cur = next.also { yield(it) }//.debug()
                        else -> direction = direction.turn()
                    }
                }
            }.distinct().count()
        }

        part2 {
            val grid = lines.toGrid()

            val start = grid.find { _, c -> c == '^' }!!

            val path = walk(grid, start)!!.map { it.first }.toSet()

            (path - start).count { obs ->
                walk(grid, start, obstacle = obs) == null
            }
        }

    }
}