package year2024

import util.*
import java.util.PriorityQueue

fun main() {
//    Day20.solveAll(
//        InputProvider.raw(
//            """
//###############
//#...#...#.....#
//#.#.#.#.#.###.#
//#S#...#.#.#...#
//#######.#.#.###
//#######.#.#...#
//#######.#.###.#
//###..E#...#...#
//###.#######.###
//#...###...#...#
//#.#####.#.###.#
//#.#...#.#.#...#
//#.#.#.#.#.#.###
//#...#...#...###
//###############
//    """.trimIndent()
//        )
//    )

    Day20.solveAll()
}

object Day20 : Solutions {

    val better = puzzle {
        val parser = parser {
            val grid = lines.toMapGrid()
            val start = grid.find { _, c -> c == 'S' }!!
//            generateSequence(start to start) { (prev, cur) ->
//                cur.neighbors().firstOrNull { it != prev && grid[it] != '#' }?.let { cur to it }
//            }.map { it.second }.withIndex()
            //list is 12x faster than sequences
            buildList {
                var cur = start
                var prev = start
                while (add(cur)) {
                    cur = cur.neighbors().firstOrNull { it != prev && grid[it] != '#' }?.also { prev = cur } ?: break
                }
            }.withIndex()
        }

         fun Iterable<IndexedValue<LongPoint>>.solve(cheatTime: Int, savedMin: Int) =
            sumOf { (cost, pos) ->
                count { (cost2, pos2) ->
                    pos2.mdist(pos).let { d -> d in 2..cheatTime && (cost2 - cost - d) >= savedMin }
                }
            }


        part1(parser) { path ->
            path.solve(2, 100)
        }

        part2(parser) { path ->
            path.solve(20, 100)
        }
    }

    data class Cheat(val frm: LongPoint, val to: LongPoint)
    data class State(val pos: LongPoint, val time: Int, val cheat: Cheat?)

    val solution = puzzle {
        part1 {
            val grid = lines.toMapGrid()

            val start = grid.find { _, c -> c == 'S' }!!
            val end = grid.find { _, c -> c == 'E' }!!

            fun walk0(): List<Pair<LongPoint, Int>> {
                val q = PriorityQueue<List<Pair<LongPoint, Int>>>(compareBy { it.last().second })
                q += listOf(start to 0)
                val visited = mutableSetOf<LongPoint>()
                while (q.isNotEmpty()) {
                    val path = q.remove()
                    val (cur, time) = path.last()
                    if (cur == end) return path
                    if (cur in visited) continue
                    visited.add(cur)
                    q += cur.neighbors().filter { it in grid && grid[it] != '#' }
                        .map { path + (it to time + 1) }
                }
                error("no path")
            }

            fun walk(path: Map<LongPoint, Int>, max: Int) =
                sequence {
                    val q = PriorityQueue<State>(compareBy { it.time })
                    q += State(start, 0, null)
                    val visited = mutableMapOf<Pair<LongPoint, Cheat?>, Int>()
                    while (q.isNotEmpty()) {
                        val (cur, time, cheat) = q.remove()
                        if (cur == end) {
                            yield(time)
                            continue
                        }
                        if ((cur to cheat) in visited && visited[cur to cheat]!! <= time) continue
                        visited[cur to cheat] = time

                        if (cheat != null && cur in path) {
                            val t = time + max - (path[cur]!!)
                            if (t < max) yield(t)
                            continue
                        }

                        for (n in cur.neighbors().filter { it in grid }) {
                            if (grid[n] == '#') {
                                if (cheat == null) {
                                    val d = n - cur
                                    q += State(n, time + 1, Cheat(n, (n + d)))
                                }
                            } else {
                                q += State(n, time + 1, cheat)
                            }
                        }
                    }
                }

            val path = walk0()
            val max = path.last().second.debug()
            walk(path.toMap(), max).onEach { }.count { (max - it) >= 100 }
        }


        data class State2(val pos: LongPoint, val start: LongPoint? = null, val end: LongPoint? = null) {
            val cheating get() = start != null && end == null
        }

        data class Wrap(val state: State2, val time: Int)

//        part2 {
//            val grid = lines.toMapGrid()
//
//            val start = grid.find { _, c -> c == 'S' }!!
//            val end = grid.find { _, c -> c == 'E' }!!
//
//            fun walk0(): List<Pair<LongPoint, Int>> {
//                val q = PriorityQueue<List<Pair<LongPoint, Int>>>(compareBy { it.last().second })
//                q += listOf(start to 0)
//                val visited = mutableSetOf<LongPoint>()
//                while (q.isNotEmpty()) {
//                    val path = q.remove()
//                    val (cur, time) = path.last()
//                    if (cur == end) return path
//                    if (cur in visited) continue
//                    visited.add(cur)
//                    q += cur.neighbors().filter { it in grid && grid[it] != '#' }
//                        .map { path + (it to time + 1) }
//                }
//                error("no path")
//            }
//
//            fun walk(path: Map<LongPoint, Int>, max: Int) =
//                sequence {
//                    val q = PriorityQueue<Wrap>(compareBy { it.time })
//                    q += Wrap(State2(start), 0)
//                    val visited = mutableMapOf<State2, Int>()
//                    while (q.isNotEmpty()) {
//                        val (state, time) = q.remove()
//
//                        val (cur, sc, ec) = state
//                        if (cur == end) {
//                            if (state.cheating)
//                                yield(state.copy(end = end) to time)
//                            else yield(state to time)
//                            continue
//                        }
//                        if (state.cheating && cur.mdist(state.start!!) >= 20) continue
//                        if (state in visited && visited[state]!! <= time) continue
//                        visited[state] = time
//
//                        if (ec != null && cur in path) {
//                            val t = time + max - (path[cur]!!)
//                            if (t <= max - 100) yield(state to t)
//                            continue
//                        }
//                        if (time > max - 100) continue
//
//                        for (n in cur.neighbors().filter { it in grid }) {
//                            if (grid[n] == '#') {
//                                when {
//                                    state.cheating -> q += Wrap(state.copy(pos = n), time + 1)
//                                    state.start == null -> q += Wrap(state.copy(pos = n, start = cur), time + 1)
//                                }
//                            } else {
//                                if (state.cheating) q += Wrap(state.copy(pos = n, end = n), time + 1)
//                                q += Wrap(state.copy(pos = n), time + 1)
//                            }
//                        }
//                    }
//                }
//
//            val path = walk0()
//            val max = path.last().second.debug()
////            walk(path.toMap(), max).onEach {  }.count{ (max - it) >= 100}
//            // 1154272 to high, 991949 too low
//            walk(path.toMap(), max).filter { (max - it.second) >= 100 }.groupBy { it.first.start to it.first.end }
////                .onEach { it.debug() }
//                .keys.count()
//        }
    }
}