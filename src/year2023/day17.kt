package year2023

import util.*
import java.util.*

fun main() {
    listOf(Day17::solution, Day17::dij, Day17Alt::eph).solveAll(
//        InputProvider.raw(
//            """2413432311323
//3215453535623
//3255245654254
//3446585845452
//4546657867536
//1438598798454
//4457876987766
//3637877979653
//4654967986887
//4564679986453
//1224686865563
//2546548887735
//4322674655533"""
//        )
    )
}

object Day17 {
    private data class Input(val location: IntPoint, val direction: Direction, val run: Int = 0)

    private fun Input.move(dir: Direction) = Input(location + dir, dir, if (dir == direction) run + 1 else 1)


    val solution = solution {
        val parser = parser {
            buildMap {
                for ((y, line) in lines.withIndex()) for ((x, n) in line.withIndex()) put(x point y, n.digitToInt())
            }
        }


        fun findPath(map: Map<IntPoint, Int>, minR: Int = 0, maxR: Int = 3): Int {
            val start = Input(0 point 0, Direction.Up, 0)
            val end = map.keys.maxWith(compareBy<IntPoint> { it.y }.thenBy { it.x })
            val best = mutableMapOf<Input, Int>()
            val q = PriorityQueue<Pair<Input, Int>>(compareBy { (inp, loss) -> loss })
            q.add(start to 0)

            while (q.isNotEmpty()) {
                val (inp, loss) = q.remove()
                val (here, dir, run) = inp

                if (run > maxR) continue
                if ((best[inp] ?: Int.MAX_VALUE) <= loss) continue
                if (here == end) {
                    if (run >= minR) {
                        return loss
                    }
                    continue
                }
                best[inp] = loss

                when {
                    run == 0 -> q += listOf(Direction.Right, Direction.Down).map { d -> inp.move(d) }
                        .map { it to map[it.location]!! }

                    run < minR -> inp.move(dir).let { if (it.location in map) q.add(it to loss + map[it.location]!!) }
                    else -> {
                        for (d in Direction.entries) {
                            if (d == -dir) continue
                            val next = inp.move(d)
                            if (next.location in map) q.add(next to loss + map[next.location]!!)
                        }
                    }
                }
            }

            return Int.MAX_VALUE
        }

        part1(parser) { map ->
            findPath(map)
        }

        part2(parser) { map ->
            findPath(map, 4, 10)
        }
    }


    val dij = solution {
        data class State(val pos: IntPoint, val direction: Direction, val speed: Int) {
            fun move(dir: Direction) = State(pos + dir, dir, if (dir == direction) speed + 1 else 1)
        }

        fun List<String>.isValid(pos: IntPoint) = pos.y in indices && pos.x in first().indices


        fun List<String>.route(minSpeed: Int = 0, maxSpeed: Int = 3) =
            dijkstra(State(0 point 0, Direction.Right, 0),
                isEnd = { v -> v.speed >= minSpeed && v.pos.x == first().lastIndex && v.pos.y == lastIndex },
                cost = { _, v -> this[v.pos.y][v.pos.x].digitToInt() }) { s ->
                suspend fun SequenceScope<State>.tryMove(dir: Direction) = s.move(dir).let {
                    if (isValid(it.pos)) yield(it)
                }

                when {
                    s.speed == 0 -> for (d in listOf(Direction.Right, Direction.Down)) tryMove(d)
                    s.speed < minSpeed -> tryMove(s.direction)
                    s.speed < maxSpeed -> for (d in listOf(
                        s.direction.counterClockwise(), s.direction.clockwise(), s.direction
                    )) tryMove(d)

                    else -> for (d in listOf(s.direction.counterClockwise(), s.direction.clockwise())) tryMove(d)
                }
            }.second

        part1 {
            lines.route()
        }

        part2 {
            lines.route(4, 10)
        }
    }
}

object Day17Alt {

    private inline fun bfs(
        input: List<String>,
        ok: (distance: Int) -> Boolean,
        next: (direction: Direction, distance: Int) -> Iterable<Direction>,
    ): Int? {
        val start = State(0, 0, Direction.R, 0)
        val costs = mutableMapOf(start to 0)
        val queue = PriorityQueue<IndexedValue<State>>(compareBy { (cost, state) -> cost - state.y - state.x })
        queue.add(IndexedValue(0, start))
        while (!queue.isEmpty()) {
            val (cost, state) = queue.remove()
            if (state.y == input.lastIndex && state.x == input.last().lastIndex && ok(state.distance)) return cost
            if (costs.getValue(state) < cost) continue
            @Suppress("LoopWithTooManyJumpStatements") for (direction in next(state.direction, state.distance)) {
                val newState = state.move(direction)
                if (newState.y !in input.indices || newState.x !in input[state.y].indices) continue
                val newCost = cost + input[newState.y][newState.x].digitToInt()
                if (costs.getOrElse(newState) { Int.MAX_VALUE } <= newCost) continue
                costs[newState] = newCost
                queue.add(IndexedValue(newCost, newState))
            }
        }
        return null
    }

    private enum class Direction {
        U, L, D, R;

        operator fun plus(other: Int): Direction = entries[(ordinal + other).mod(entries.size)]
        operator fun minus(other: Int): Direction = entries[(ordinal - other).mod(entries.size)]
    }

    private data class State(
        val y: Int,
        val x: Int,
        val direction: Direction,
        val distance: Int,
    )

    private fun State.move(direction: Direction): State {
        val y = when (direction) {
            Direction.U -> y - 1
            Direction.D -> y + 1
            else -> y
        }
        val x = when (direction) {
            Direction.L -> x - 1
            Direction.R -> x + 1
            else -> x
        }
        return State(
            y = y,
            x = x,
            direction = direction,
            distance = if (direction == this.direction) distance + 1 else 1,
        )
    }

    val eph = solution {
        part1 {
            bfs(lines.mapNotNull { it.filter(('1'..'9')::contains).ifEmpty { null } },
                ok = { true },
                next = { direction, distance ->
                    if (distance < 3) listOf(direction - 1, direction + 1, direction) else listOf(
                        direction - 1, direction + 1
                    )
                })
        }

        part2 {
            bfs(
                lines.mapNotNull { it.filter(('1'..'9')::contains).ifEmpty { null } },
                ok = { it >= 4 },
                next = { direction, distance ->
                    when {
                        distance < 4 -> listOf(direction)
                        distance < 10 -> listOf(direction - 1, direction + 1, direction)
                        else -> listOf(direction - 1, direction + 1)
                    }
                },
            )
        }
    }
}