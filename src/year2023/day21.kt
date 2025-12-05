package year2023

import util.*
import java.lang.Math.floorMod


fun main() {
    listOf(Day21::patterns, Day21::fit, Day21::corners).solveAll(
//        InputProvider.Example
//        InputProvider.raw(
//            """...........
//.....###.#.
//.###.##..#.
//..#.#...#..
//....#.#....
//.##..S####.
//.##..#...#.
//.......##..
//.##.#.####.
//.##..##.##.
//..........."""
//        )
    )
}


object Day21 {

    val patterns = solution {
        val parser = parser {
            var start = 0 point 0
            buildSet {
                for ((row, line) in lines.withIndex()) {
                    for ((col, c) in line.withIndex()) {
                        if (c != '#')
                            add(col point row)
                        if (c == 'S')
                            start = col point row
                    }
                }
            } to start
        }

        part1(parser) { (plots, start) ->
            val target = 64
            val cache = mutableSetOf<Pair<Int, IntPoint>>()
            val hits = mutableSetOf<IntPoint>()

            val q = ArrayDeque(listOf(start to 0))
            while (q.isNotEmpty()) {
                val (here, n) = q.removeFirst()

                if (n == target) {
                    hits.add(here)
                    continue
                }
                if ((n to here) in cache) continue
                cache.add(n to here)

                for (d in Direction.entries) {
                    val next = here + d
                    if (next in plots) q.add(next to n + 1)
                }
            }

            hits.count()
        }

        part2(parser) { (plots, start) ->
            // counting patterns

            val width = plots.maxOf { it.x } + 1
            val height = plots.maxOf { it.y } + 1

            fun isValid(point: IntPoint): Boolean {
                val (x, y) = point
                return (floorMod(x, width) point floorMod(y, height)) in plots

            }

            fun flood(start: IntPoint, to: Int): MutableMap<IntPoint, Int> {
                val seen = mutableMapOf(start to 0)

                val q = ArrayDeque(listOf(start to 0))
                while (q.isNotEmpty()) {
                    val (here, n) = q.removeFirst()
                    for (d in Direction.entries) {
                        val next = here + d
                        if (n <= to && next !in seen && isValid(next)) {
                            q.add(next to n + 1)
                            seen[next] = n + 1
                        }
                    }
                }

                return seen
            }

            flood(start, 65 + 132 * 2).let { ps ->
                val diamond = ps.filterValues { it <= 65 }.values.countOdd()
                val S1 = ps.filterValues { it <= 65 + 131 }.values.countEven()
                val blockEven =
                    ps.filter { (p, n) -> n <= 65 + 131 && p.within(width, height) }.values.countEven()
                val blockOdd =
                    ps.filter { (p, n) -> n <= 65 + 132 && p.within(width, height) }.values.countOdd()

                // S1 is extended 1 row of triangles, so
                val pat1 = blockEven + blockOdd
                val pat2 = S1 - pat1 - diamond

                val N = 26501365L
                val Q = (N - 65) / 131


                pat1 * Q * Q + pat2 * Q + diamond
            }
        }
    }

    val fit = solution {
        val parser = parser {
            var start = 0 point 0
            buildSet {
                for ((row, line) in lines.withIndex()) {
                    for ((col, c) in line.withIndex()) {
                        if (c != '#')
                            add(col point row)
                        if (c == 'S')
                            start = col point row
                    }
                }
            } to start
        }

        part2(parser) { (plots, start) ->
            // polynomial fit
            val width = plots.maxOf { it.x } + 1
            val height = plots.maxOf { it.y } + 1

            fun isValid(point: IntPoint): Boolean {
                val (x, y) = point
                return (floorMod(x, width) point floorMod(y, height)) in plots

            }

            fun flood(start: IntPoint, to: Int): MutableMap<IntPoint, Int> {
                val seen = mutableMapOf(start to 0)

                val q = ArrayDeque(listOf(start to 0))
                while (q.isNotEmpty()) {
                    val (here, n) = q.removeFirst()
                    for (d in Direction.entries) {
                        val next = here + d
                        if (n <= to && next !in seen && isValid(next)) {
                            q.add(next to n + 1)
                            seen[next] = n + 1
                        }
                    }
                }

                return seen
            }


            flood(start, 65 + 132 * 2).let { ps ->
                val y0 = ps.filterValues { it <= 65 }.values.countOdd()
                val y1 = ps.filterValues { it <= 65 + 131 }.values.countEven() - y0
                val y2 = ps.filter { (p, n) -> n <= 65 + 131 * 2 }.values.countOdd() - y0

                // the reachable plots follow a square polynomial
                // f(n) = a * x^2 + b * x + c
                // f(N-65) = a * x^2 + b * x + f(65)
                // calculate x = 0, 1, 2 -> n = 65, 65+131, 65+131*2
                // solve for a & b

                val a = (y2 - 2 * y1) / 2
                val b = y1 - a
                val N = 26501365L
                val Q = (N - 65) / 131

                a * Q * Q + b * Q + y0
            }
        }
    }


    val corners = solution {
        val parser = parser {
            var start = 0 point 0
            buildSet {
                for ((row, line) in lines.withIndex()) {
                    for ((col, c) in line.withIndex()) {
                        if (c != '#')
                            add(col point row)
                        if (c == 'S')
                            start = col point row
                    }
                }
            } to start
        }

        part2(parser) { (plots, start) ->
            // counting corners

            fun flood(start: IntPoint): MutableMap<IntPoint, Int> {
                val seen = mutableMapOf(start to 0)
                val q = ArrayDeque(listOf(start to 0))
                while (q.isNotEmpty()) {
                    val (here, n) = q.removeFirst()
                    for (d in Direction.entries) {
                        val next = here + d
                        if (next !in seen && next in plots) {
                            q.add(next to n + 1)
                            seen[next] = n + 1
                        }
                    }
                }

                return seen
            }



            flood(start).let { ps ->
                val evenCorners = ps.filterKeys { p -> p mdist start > 65 }.values.countEven()
                val oddCorners = ps.filterKeys { p -> p mdist start > 65 }.values.countOdd()
                val evenFill = ps.values.countEven()
                val oddFill = ps.values.countOdd()

                val N = 26501365L
                val Q = (N - 65) / 131

                (Q + 1) * (Q + 1) * oddFill + Q * Q * evenFill - (Q + 1) * oddCorners + Q * evenCorners
            }

        }
    }


    val alt = solution {
        // trying to get total manhattan distance minus number of #s enclosed, doesn't work, close but always off by a little bit
        // probably because im assuming all spots are reachable but some could be blocked off by other blocks.
        part2 {
            var start = 0 point 0
            val set = buildSet {
                for ((row, line) in lines.withIndex()) {
                    for ((col, c) in line.withIndex()) {
                        if (c == '#') add(col point row)
                        if (c == 'S') start = col point row
                    }
                }
            }

            val N = 26501365L


            // gaps in normal triangle
            val x = set.count { p -> (p mdist start).let { d -> d < 65 && d % 2 == 1 } }.debug("< o:")
            // gaps in outer edges
            val y = set.count { p -> (p mdist start).let { d -> d > 65 && d % 2 == 1 } }.debug("> o:")

            // gaps in inverted triangle
            val nx = set.count { p -> (p mdist start).let { d -> d < 65 && d % 2 == 0 } }.debug("< e:")
            // gaps in inverted outer edges
            val ny = set.count { p -> (p mdist start).let { d -> d > 65 && d % 2 == 0 } }.debug("> e:")

            fun mdistA(n: Long) = (n + 1) * (n + 1)

            val X = mdistA(65) - x // off by 1
            val S1 = mdistA(130 + 65) - x - 4 * nx - 2 * (y + ny) // off by -284

            println("X=$X, S1=$S1, ($x, $y, $nx, $ny)")
            // ...

            0
        }
    }

    private fun Collection<Int>.countOdd(): Int = count { it % 2 == 1 }
    private fun Collection<Int>.countEven(): Int = count { it % 2 == 0 }
}