package year2023

import util.PuzDSL
import util.solveAll
import year2023.Day10.down
import year2023.Day10.neighbors
import year2023.Day10.right
import year2023.Day10.steps
import year2023.Day10.up
import year2023.Day10WithQueues.neighbors
import year2023.Day10WithQueues.steps
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.max

fun main() {
    listOf(Day10, Day10WithMath).solveAll(
//        InputProvider.Example
//        InputProvider.raw(
//            """FF7FSF7F7F7F7F7F---7
//L|LJ||||||||||||F--J
//FL-7LJLJ||||||LJL-77
//F--JF--7||LJLJ7F7FJ-
//L---JF-JLJ.||-FJLJJ7
//|F|F-JF---7F7-L7L|7|
//|FFJF7L7F-JF7|JL---7
//7-L-JL7||F7|L7F-7F7|
//L.L7LFJ|||||FJL7||LJ
//L7JLJL-JLJLJL--JLJ.L
//"""
//        )

//        InputProvider.raw(
//            """.F----7F7F7F7F-7....
//.|F--7||||||||FJ....
//.||.FJ||||||||L7....
//FJL7L7LJLJ||LJ.L-7..
//L--J.L7...LJS7F-7L7.
//....F-J..F7FJ|L7L7L7
//....L7.F7||L7|.L7L7|
//.....|FJLJ|FJ|F7|.LJ
//....FJL-7.||.||||...
//....L---J.LJ.LJLJ..."""
//        )
    )
}

object Day10WithMath : PuzDSL({
    val parser = parser {
        val map = lines.flatMapIndexed { row, line ->
            line.mapIndexed { col, c ->
                Day10.Point(col, row) to c
            }
        }.toMap()
        val start = map.entries.find { it.value == 'S' }!!.key
        with(Day10.Plan(start, map)) {
            listOf(start) + generateSequence(
                start to start.steps().first()
            ) { (p0, p1) -> p1 to (p1.steps() - p0).first() }
                .takeWhile { (_, p1) -> p1 != start }.map { it.second } + start

        }.toList()
    }
    part1(parser) { path ->
        path.size / 2
    }

    part2(parser) { path ->
        // Pick's theorem, and "Shoelace formula" (Gauss's Area)
        (path.windowed(2).sumOf { (p0, p1) -> p0.x * p1.y - p0.y * p1.x }
            .absoluteValue - (path.size - 1)) / 2 + 1
    }
})

object Day10 : PuzDSL({
    val parser = parser {
        val map = lines.flatMapIndexed { row, line ->
            line.mapIndexed { col, c ->
                Point(col, row) to c
            }
        }.toMap()
        val start = map.entries.find { it.value == 'S' }!!.key
        with(Plan(start, map)) {

            val path = generateSequence(start to start.steps().first()) { (p0, p1) -> p1 to (p1.steps() - p0).first() }
                .takeWhile { (_, p1) -> p1 != start }.toList().let { it + (it.last().second to start) }
            val curl = path.sumOf { (p0, p1) -> p0.x * p1.y - p0.y * p1.x }

            this to (if (curl > 0)
                path.map { (p0, p1) -> p1 to p0 }
            else
                path).toMap()

        }
    }
    part1(parser) { (_, path) ->
        path.entries.size / 2
    }

    part2(parser) { (plan, path) ->
        with(plan) {
            val spots = map.keys.filter { it !in path }
            fun region(point: Point): MutableSet<Point> {
                val q = LinkedList(listOf(point))
                val seen = mutableSetOf<Point>()
                while (q.isNotEmpty()) {
                    val p = q.poll()
                    if (p in seen) continue
                    seen += p
                    q += p.neighbors().filter { it !in seen }.filter { it in spots }
                }
                return seen
            }

            val seen = mutableSetOf<Point>()
            spots.sumOf { p ->
                if (p !in seen) {
                    val reg = region(p)
                    seen += reg
                    val check = generateSequence(reg.first()) { it.right }.takeWhile { it in map }
                        .find { it in path }
                    when {
                        check == null -> 0
                        path[check] == check.up || path[check.down] == check -> reg.size
                        else -> 0
                    }
                } else 0
            }
        }
    }
}) {
    data class Plan(val start: Point, val map: Map<Point, Char>)
    data class Point(val x: Int, val y: Int)

    val Point.left get() = copy(x = x - 1)
    val Point.right get() = copy(x = x + 1)
    val Point.up get() = copy(y = y - 1)

    val Point.down get() = copy(y = y + 1)


    context(Plan)
    fun Point.steps(): Set<Point> = when (map[this] ?: '.') {
        '|' -> setOf(up, down)
        '-' -> setOf(left, right)
        'L' -> setOf(up, right)
        'J' -> setOf(up, left)
        '7' -> setOf(left, down)
        'F' -> setOf(right, down)
        'S' -> setOf(up, down, left, right).filter { x -> x.steps().contains(this) }.toSet()
        else -> setOf()
    }

    fun Point.neighbors(): List<Point> = listOf(up, down, left, right)
}

object Day10WithQueues : PuzDSL({

    val parser = parser {
        lateinit var start: Point
        val map = lines.flatMapIndexed { row, line ->
            line.mapIndexed { col, c ->
                if (c == 'S') start = Point(col, row)
                Point(col, row) to c
            }
        }.toMap()
        Plan(start, map)
    }


    part1(parser) { plan ->
        with(plan) {
            val q = LinkedList(listOf(0 to plan.start))
            val seen = mutableSetOf(plan.start)
            var maxN = 0
            while (q.isNotEmpty()) {
                val (n, curr) = q.poll()
                seen += curr
                maxN = max(maxN, n)
                curr.steps().filter { it !in seen }
                    .forEach { p ->
                        q += (n + 1) to p
                    }
            }

            maxN
        }
    }

    part2(parser) { plan ->
        with(plan) {
            fun findPath(): Set<Point> {
                // find path
                val q = LinkedList(listOf(plan.start))
                val seen = mutableSetOf(plan.start)
                while (q.isNotEmpty()) {
                    val p = q.poll()
                    seen += p
                    q += p.steps().filter { it !in seen }
                }
                return seen
            }

            val path = findPath()

            val holes = map.keys.filter { it !in path }.toSet()
            fun isOutside(point: Point): Pair<Boolean, MutableSet<Point>> {
                val seen = mutableSetOf<Point>()
                val q = LinkedList(listOf(point))
                var isOut = false
                while (q.isNotEmpty()) {
                    val p = q.poll()
                    seen += p
                    val next = p.neighbors().filter { it !in seen }
                    if (!isOut && next.any { it !in map }) isOut = true
                    q += next.filter { it in holes }
                }
                return isOut to seen
            }

            val q = LinkedList(holes)
            val seen = mutableSetOf<Point>()
            var inside = 0
            while (q.isNotEmpty()) {
                val p = q.poll()
                if (p in seen) continue
                seen += p
                val (out, visited) = isOutside(p)
                if (!out) {
                    inside += visited.size
                }
                seen += visited
            }
            inside
        }
    }

}) {
    data class Plan(val start: Point, val map: Map<Point, Char>)
    data class Point(val x: Int, val y: Int)

    val Point.left get() = copy(x = x - 1)
    val Point.right get() = copy(x = x + 1)
    val Point.up get() = copy(y = y - 1)

    val Point.down get() = copy(y = y + 1)


    context(Plan)
    fun Point.steps(): List<Point> = when (map[this] ?: '.') {
        '|' -> listOf(up, down)
        '-' -> listOf(left, right)
        'L' -> listOf(up, right)
        'J' -> listOf(up, left)
        '7' -> listOf(left, down)
        'F' -> listOf(right, down)
        'S' -> listOf(up, down, left, right).filter { x -> x.steps().contains(this) }
        else -> listOf()
    }

    fun Point.neighbors(): List<Point> = listOf(up, down, left, right)
}
