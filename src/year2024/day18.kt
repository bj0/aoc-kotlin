package year2024

import util.*
import java.util.*

fun main() {
    Day18.solveAll()

}

object Day18 : Solutions {
    val solution = solution {
        part1 {
            val width = 70L
            val height = width
            val points = lines.map { it.getLongList() }.map { (a, b) -> LongPoint(a, b) }

            val blocks = points.take(1024).toSet()
            val end = width point height

            fun search(): Int {
                val q = PriorityQueue<Pair<LongPoint, Int>>(compareBy { it.second })
                q += (0L point 0) to 0
                val visited = mutableSetOf<LongPoint>()
                while (q.isNotEmpty()) {
                    val (pos, count) = q.remove()
                    if (pos in visited) continue
                    if (pos == end) return count
                    visited.add(pos)

                    for (next in pos.neighbors().filter { it.within(width + 1, height + 1) && it !in blocks })
                        q += next to (count + 1)
                }
                error("no path")
            }

            search()
        }

        part2 {
            val width = 70L
            val height = width
            val points = lines.map { it.getLongList() }.map { (a, b) -> a point b }
            val end = width point height

            fun check(n: Int): Boolean {
                val blocks = points.take(n).toSet()

                val q = PriorityQueue<Pair<LongPoint, Int>>(compareBy { it.second })
                q += (0L point 0) to 0

                val visited = mutableSetOf<LongPoint>()
                while (q.isNotEmpty()) {
                    val (pos, count) = q.remove()
                    if (pos in visited) continue
                    if (pos == end) return true
                    visited.add(pos)

                    for (next in pos.neighbors().filter { it.within(width + 1, height + 1) && it !in blocks })
                        q += next to (count + 1)
                }

                return false
            }

            tailrec fun search(low: Int, high: Int): Int {
                val n = (low + high) / 2
                if (n == low) return n
                return if (check(n))
                    search(n, high)
                else
                    search(low, n)
            }

            val n = search(1024, points.size)
            points.drop(n).first()
        }
    }
}