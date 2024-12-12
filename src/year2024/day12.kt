package year2024

import util.*

fun main() {
    Day12.solveAll(
        InputProvider.raw(
//            """
//            AAAAAA
//            AAABBA
//            AAABBA
//            ABBAAA
//            ABBAAA
//            AAAAAA
//        """.trimIndent()
            """
        RRRRIICCFF
        RRRRIICCCF
        VVRRRCCFFF
        VVRCCCJFFF
        VVVVCJJCFE
        VVIVCCJJEE
        VVIIICJJEE
        MIIIIIJJEE
        MIIISIJEEE
        MMMISSJEEE
    """.trimIndent()
        )
    )
    Day12.solveAll()
}

object Day12 : Solutions {
    data class Fence(val point: LongPoint, val direction: GridDirection)

    @JvmInline
    value class Plot(val point: LongPoint) {
        override fun toString() = point.toString()
    }

    val solution = puzzle {
        fun walk(
            map: Map<LongPoint, Char>,
            cur: LongPoint,
            plot: MutableSet<LongPoint>,
            fence: MutableSet<Pair<LongPoint, GridDirection>>
        ) {
            if (cur in plot) return
            plot.add(cur)
            GridDirection.directions().map { cur.step(it) to it }
                .forEach { (n, ndir) ->
                    when {
                        n !in map -> fence.add(cur to ndir)
                        else -> walk(map, n, plot, fence)
                    }
                }
        }

        fun pricePlot(map: Map<LongPoint, Char>): Long {
            var sub = map
            var cost = 0L
            while (sub.isNotEmpty()) {
                val plot = mutableSetOf<LongPoint>()
                val fence = mutableSetOf<Pair<LongPoint, GridDirection>>()
                walk(sub, sub.keys.first(), plot, fence)
                sub = sub.filterKeys { it !in plot }
                cost += plot.size * fence.size
            }
            return cost
        }

        fun LongPoint.leftRight(dir: GridDirection) = when (dir) {
            GridDirection.North -> listOf(East, West)
            GridDirection.East -> listOf(North, South)
            GridDirection.South -> listOf(East, West)
            GridDirection.West -> listOf(North, South)
            else -> error("bad direction $this -> $dir")
        }

        fun walkFence(
            subfence: Set<Pair<LongPoint, GridDirection>>,
            cur: LongPoint,
            dir: GridDirection,
            side: MutableSet<LongPoint>
        ) {
            if (cur in side) return
            side.add(cur)
            cur.leftRight(dir).filter { (it to dir) in subfence }.forEach { n ->
                walkFence(subfence, n, dir, side)
            }
        }

        fun discountPricePlot(map: Map<LongPoint, Char>): Long {
            var sub = map
            var cost = 0L
            while (sub.isNotEmpty()) {
                val plot = mutableSetOf<LongPoint>()
                val fence = mutableSetOf<Pair<LongPoint, GridDirection>>()
                walk(sub, sub.keys.first(), plot, fence)
                sub = sub.filterKeys { it !in plot }

                val subfence = fence.toMutableSet()
                while (subfence.isNotEmpty()) {
                    val (start, dir) = subfence.first()
                    val side = mutableSetOf<LongPoint>()
                    walkFence(subfence, start, dir, side)
                    subfence.removeAll { it.first in side && it.second == dir }
                    cost += plot.size
                }
            }
            return cost
        }

        part1 {
            val grid = lines.toMapGrid()
            val plants = grid.values.toSet()

            plants.sumOf { plant -> pricePlot(grid.filterValues { it == plant }) }
        }

        part2 {
            val grid = lines.toMapGrid()
            val plants = grid.values.toSet()

            plants.sumOf { plant -> discountPricePlot(grid.filterValues { it == plant }) }
        }
    }

    val cleaned = puzzle {
        // generic bfs walk, return visited points
        fun <T> walk(
            start: T,
            getNeighbors: (T) -> List<T>
        ): MutableSet<T> {
            val visited = mutableSetOf<T>()
            fun innerWalk(cur: T) {
                if (cur in visited) return
                visited.add(cur)
                getNeighbors(cur).forEach { innerWalk(it) }
            }
            innerWalk(start)
            return visited
        }

        // walk a out a plot and find its fence
        fun walkPlot(
            map: Map<LongPoint, Char>,
            start: LongPoint
        ): Pair<Set<LongPoint>, Set<Pair<LongPoint, GridDirection>>> {
            val fence = mutableSetOf<Pair<LongPoint, GridDirection>>()
            val plot = walk(start) { cur ->
                buildList {
                    GridDirection.directions().forEach { dir ->
                        when (val n = cur.step(dir)) {
                            !in map -> fence.add(cur to dir)
                            else -> add(n)
                        }
                    }
                }
            }
            return plot to fence
        }

        fun LongPoint.leftRight(dir: GridDirection) = when (dir) {
            GridDirection.North -> listOf(East, West)
            GridDirection.East -> listOf(North, South)
            GridDirection.South -> listOf(East, West)
            GridDirection.West -> listOf(North, South)
            else -> error("bad direction $this -> $dir")
        }

        // walk along a fence side
        fun walkFence(map: Set<Pair<LongPoint, GridDirection>>, start: LongPoint, dir: GridDirection): Set<LongPoint> {
            val side = walk(start) { cur ->
                buildList {
                    cur.leftRight(dir).filter { (it to dir) in map }.forEach { n ->
                        add(n)
                    }
                }
            }
            return side
        }

        fun pricePlots(map: Map<LongPoint, Char>): Long {
            var sub = map
            var cost = 0L
            while (sub.isNotEmpty()) {
                val (plot, fence) = walkPlot(sub, sub.keys.first())
                sub = sub.filterKeys { it !in plot }
                cost += plot.size * fence.size
            }
            return cost
        }

        fun discountPricePlot(map: Map<LongPoint, Char>): Long {
            var subMap = map
            var cost = 0L
            while (subMap.isNotEmpty()) {
                val (plot, fence) = walkPlot(map, subMap.keys.first())
                subMap = subMap.filterKeys { it !in plot }

                val subFence = fence.toMutableSet()
                while (subFence.isNotEmpty()) {
                    val (start, dir) = subFence.first()
                    val side = walkFence(subFence, start, dir)
                    subFence.removeAll { it.first in side && it.second == dir }
                    cost += plot.size
                }
            }
            return cost
        }

        part1 {
            val grid = lines.toMapGrid()
            val plants = grid.values.toSet()

            plants.sumOf { plant -> pricePlots(grid.filterValues { it == plant }) }
        }

        part2 {
            val grid = lines.toMapGrid()
            val plants = grid.values.toSet()

            plants.sumOf { plant -> discountPricePlot(grid.filterValues { it == plant }) }
        }
    }

    val sequence = puzzle {
        // generic bfs walk, return visited points
        fun <T> walk(
            start: T,
            getNeighbors: suspend SequenceScope<T>.(T) -> Unit
        ): MutableSet<T> {
            val visited = mutableSetOf<T>()
            fun innerWalk(cur: T) {
                if (cur in visited) return
                visited.add(cur)
                sequence { getNeighbors(cur) }.forEach { innerWalk(it) }
            }
            innerWalk(start)
            return visited
        }

        // walk a out a plot and find its fence
        fun walkPlot(
            map: Map<LongPoint, Char>,
            start: LongPoint
        ): Pair<Set<LongPoint>, Set<Pair<LongPoint, GridDirection>>> {
            val fence = mutableSetOf<Pair<LongPoint, GridDirection>>()
            val plot = walk(start) { cur ->
                GridDirection.directions().forEach { dir ->
                    when (val n = cur.step(dir)) {
                        !in map -> fence.add(cur to dir)
                        else -> yield(n)
                    }
                }
            }
            return plot to fence
        }

        fun LongPoint.leftRight(dir: GridDirection) = when (dir) {
            GridDirection.North -> listOf(East, West)
            GridDirection.East -> listOf(North, South)
            GridDirection.South -> listOf(East, West)
            GridDirection.West -> listOf(North, South)
            else -> error("bad direction $this -> $dir")
        }

        // walk along a fence side
        fun walkFence(map: Set<Pair<LongPoint, GridDirection>>, start: LongPoint, dir: GridDirection): Set<LongPoint> {
            val side = walk(start) { cur ->
                cur.leftRight(dir).filter { (it to dir) in map }.forEach { n ->
                    yield(n)
                }
            }
            return side
        }

        fun pricePlots(map: Map<LongPoint, Char>): Long {
            var sub = map
            var cost = 0L
            while (sub.isNotEmpty()) {
                val (plot, fence) = walkPlot(sub, sub.keys.first())
                sub = sub.filterKeys { it !in plot }
                cost += plot.size * fence.size
            }
            return cost
        }

        fun discountPricePlot(map: Map<LongPoint, Char>): Long {
            var subMap = map
            var cost = 0L
            while (subMap.isNotEmpty()) {
                val (plot, fence) = walkPlot(map, subMap.keys.first())
                subMap = subMap.filterKeys { it !in plot }

                val subFence = fence.toMutableSet()
                while (subFence.isNotEmpty()) {
                    val (start, dir) = subFence.first()
                    val side = walkFence(subFence, start, dir)
                    subFence.removeAll { it.first in side && it.second == dir }
                    cost += plot.size
                }
            }
            return cost
        }

        part1 {
            val grid = lines.toMapGrid()
            val plants = grid.values.toSet()

            plants.sumOf { plant -> pricePlots(grid.filterValues { it == plant }) }
        }

        part2 {
            val grid = lines.toMapGrid()
            val plants = grid.values.toSet()

            plants.sumOf { plant -> discountPricePlot(grid.filterValues { it == plant }) }
        }
    }

    // slower
    val classes = puzzle {
        fun walk(map: Map<LongPoint, Char>, start: LongPoint) = sequence {
            val seen = mutableSetOf<LongPoint>()
            suspend fun SequenceScope<Any>.rec(cur: LongPoint) {
                seen.add(cur)
                yield(Plot(cur))
                // without this .asSequence, the filter checks are converted to list (checked before all branches can add
                // themselves to seen, which can result in duplicates, that means this is probably a bad place to filter
                GridDirection.directions().map { cur.step(it) to it }.asSequence().filter { it.first !in seen }
                    .forEach { (n, dir) ->
                        when (n) {
                            !in map -> yield(Fence(n, dir))
                            else -> rec(n)
                        }
                    }
            }
            rec(start)
        }.let { it.filterIsInstance<Plot>().toList() to it.filterIsInstance<Fence>().toList() }

        part1 {
            val grid = lines.toMapGrid()

            val plants = grid.values.toSet()

            plants.sumOf { plant ->
                generateSequence(grid.filterValues { it == plant } to 0L) { (map, cost) ->
                    if (map.isEmpty()) null
                    else {
                        val (plot, fence) = walk(map, map.keys.first())
                        map.filterKeys { Plot(it) !in plot } to cost + plot.size * fence.size
                    }
                }.last().second
            }
        }

    }
}
