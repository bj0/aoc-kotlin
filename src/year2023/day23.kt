package year2023

import util.*


fun main() {
    listOf(Day23::solution).solveAll(
//        InputProvider.Example
    )
}


object Day23 {

    fun Char.slide(): Direction = when (this) {
        '>' -> Direction.Right
        '<' -> Direction.Left
        '^' -> Direction.Up
        'v' -> Direction.Down
        else -> error("bad dir")
    }

    val solution = solution {

        val parser = parser {
            buildMap {
                lines.forEachIndexed { row, line ->
                    line.forEachIndexed { col, c ->
                        when {
                            c in ".<>^v" -> {
                                put(col point row, c)
                                Unit
                            }
                        }
                    }
                }
            }
        }

        part1(parser) { map ->
            val start = 1 point 0
            val end = map.maxBy { (p, _) -> p.y }.key


            fun bfs(start: IntPoint, end: IntPoint): MutableList<List<IntPoint>> {
                val paths = mutableListOf<List<IntPoint>>()
                val q = ArrayDeque(listOf(listOf(start)))
                while (q.isNotEmpty()) {
                    val path = q.removeFirst()
                    val here = path.last()
                    if (here == end) {
                        paths.add(path)
                        continue
                    }

                    when (val c = map[here]) {
                        null -> {}
                        in "<>^v" -> (here + c.slide()).let { next ->
                            if (next !in path)
                                q += (path + next)
                        }

                        else -> {
                            for (d in Direction.entries) {
                                val next = here + d
                                if (next !in path) {
                                    val nc = map[next]
                                    when (nc) {
                                        null -> {}
                                        in "<>^v." -> {
                                            if (next !in path)
                                                q += (path + next)
                                        }

                                        else -> {}
                                    }
                                } else null
                            }
                        }
                    }
                }

                return paths
            }

            val paths = bfs(start, end)

            paths.maxOf { it.size } - 1
        }

        part2(parser) { map ->
            val start = 1 point 0
            val end = map.maxBy { (p, _) -> p.y }.key

            data class Walk(val from: IntPoint, val here: IntPoint, val last: IntPoint, val steps: Int)
            // find nodes
            val edges = buildMap {
                with(map) {
                    val visited = mutableSetOf<IntPoint>()
                    val q = ArrayDeque(listOf(Walk(start, start, start, 0)))
                    while (q.isNotEmpty()) {
                        val (from, here, last, n) = q.removeFirst()

                        if (here == end) {
                            val edge = from edge here
                            if (edge !in this@buildMap) put(edge, n)
                            continue
                        }

                        visited.add(here)
                        val next = here.next - last
                        when {

                            next.size > 1 -> {
                                val edge = from edge here
                                if (edge in this@buildMap) continue
                                put(edge, n)
                                q += next.filter { it !in visited }.map { p -> Walk(here, p, here, 1) }
                            }

                            next.size == 1 -> {
                                q += Walk(from, next.first(), here, n + 1)
                            }
                        }
                    }
                }
            }

            val graph = buildMap {
                edges.forEach { (e, n) ->
                    put(e.a, (get(e.a) ?: listOf<Pair<IntPoint, Int>>()) + (e.b to n))
                    put(e.b, (get(e.b) ?: listOf<Pair<IntPoint, Int>>()) + (e.a to n))
                }
            }

            data class Input(val here: IntPoint, val visited: Set<IntPoint>, val dist: Int)

            fun flood(start: IntPoint, end: IntPoint): Int {
                return DeepRecursiveFunction<Input, Int> { input ->
                    val (here, visited, dist) = input

                    if (here == end) return@DeepRecursiveFunction dist

                    val next = graph.getValue(here)

                    next.maxOf { (nx, d) ->
//                        println("nx:$here -> $nx , $d")
                        when {
                            nx in visited -> 0
                            else -> callRecursive(Input(nx, visited + nx, dist + d))
                        }
                    }


                }(Input(start, setOf(start), 0))
            }

            flood(start, end)
        }

    }


    data class Edge(val a: IntPoint, val b: IntPoint) {
        override fun toString(): String {
            return "(${a.x},${a.y})->(${b.x},${b.y})"
        }
    }

    infix fun IntPoint.edge(other: IntPoint): Edge {
        val origin = 0 point 0
        return if ((origin mdist this) < (origin mdist other)) {
            Edge(this, other)
        } else Edge(other, this)
    }

    context(Map<IntPoint, Char>)
    val IntPoint.next
        get() = Direction.entries.map { d -> this + d }.filter { get(it)?.let { c -> c != '#' } == true }
}

