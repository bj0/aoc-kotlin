package year2024

import util.*
import java.util.PriorityQueue

fun main() {
    Day16.solveAll(
        InputProvider.raw(
            """
            ###############
            #.......#....E#
            #.#.###.#.###.#
            #.....#.#...#.#
            #.###.#####.#.#
            #.#.#.......#.#
            #.#.#####.###.#
            #...........#.#
            ###.#.#####.#.#
            #...#.....#.#.#
            #.#.#.###.#.#.#
            #.....#...#.#.#
            #.###.#.#.#.#.#
            #S..#.....#...#
            ###############
            """.trimIndent()
        )
    )

    Day16.solveAll()
}


object Day16 : Solutions {
    data class State(val pos: LongPoint, val dir: Direction)

    val solution = puzzle {
        part1 {
            val grid = input.toGrid()
            val start = grid.find { _, c -> c == 'S' }!!
            val end = grid.find { _, c -> c == 'E' }!!

            val startDir = Direction.Right

            val q = PriorityQueue<Pair<State, Int>>(compareBy { it.second })
            q += State(start, startDir) to 0
            val visited = mutableSetOf<State>()
            while (q.isNotEmpty()) {
                val (cur, score) = q.remove().also { visited += it.first }
                val (pos, dir) = cur
                if (pos == end) return@part1 score

                q += listOf(
                    cur.copy(pos = pos + dir) to score + 1,
                    cur.copy(pos = pos + dir.clockwise(), dir = dir.clockwise()) to score + 1001,
                    cur.copy(pos = pos + dir.counterClockwise(), dir = dir.counterClockwise()) to score + 1001
                )
                    .filter { it.first !in visited && grid[it.first.pos] != '#' }
            }
        }

        data class Path(val state: State, val score: Int, val prev: List<State>)

        part2 {
            val grid = input.toGrid()
            val start = grid.find { _, c -> c == 'S' }!!
            val end = grid.find { _, c -> c == 'E' }!!

            val startDir = Direction.Right

            val q = PriorityQueue<Path>(compareBy { it.score })
            q += Path(State(start, startDir), 0, emptyList())
            val visited = mutableSetOf<State>()
            val best = mutableSetOf(end)
            var min = Int.MAX_VALUE
            while (q.isNotEmpty()) {
                val (state, score, path) = q.remove().also { visited += it.state }

                if (state.pos == end) {
                    if (score <= min) {
                        min = score
                        best += path.map { it.pos }
                        continue
                    } else break
                }
                val (pos, dir) = state
                q += listOf(
                    Path(state.copy(pos = pos + dir), score + 1, path + state),
                    Path(state.copy(pos = pos + dir.clockwise(), dir = dir.clockwise()), score + 1001, path + state),
                    Path(
                        state.copy(pos = pos + dir.counterClockwise(), dir = dir.counterClockwise()),
                        score + 1001,
                        path + state
                    ),
                ).filter { it.state !in visited && grid[it.state.pos] != '#' }
            }

            best.size
        }
    }

    val dj = puzzle {
        data class State(val pos: LongPoint, val dir: Direction)
        part2 {
            val grid = input.toGrid()
            val start = grid.find { _, c -> c == 'S' }!!
            val end = grid.find { _, c -> c == 'E' }!!

            dijkstraAll(
                start = State(start, Direction.Right),
                initialCost = 0,
                isEnd = { it.pos == end },
                cost = { u, v -> if (u.dir == v.dir) 1 else 1001 }) { cur ->
                val (pos, dir) = cur
                listOf(dir, dir.clockwise(), dir.counterClockwise())
                    .map { State(pos + it, it) }
                    .filter { grid[it.pos] != '#' }
                    .forEach { yield(it) }
            }.flatMap { it.first.map { it.pos } }.toSet().size
        }
    }
}

fun Collection<LongPoint>.display(grid: Grid<Char>) = this.also {
    val w = this.maxOf { it.x }
    val h = this.maxOf { it.y }
    for (r in 0..h) {
        for (c in 0..w)
            print(if (c point r in this) "O" else ".")
        println("")
    }
}