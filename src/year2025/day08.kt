package year2025

import util.InputProvider
import util.Solutions
import util.debug
import util.getLongList
import util.part1
import util.part2
import util.solution
import util.solveAll
import kotlin.math.pow
import kotlin.math.sqrt

fun main() {
    Day8.solveAll(
        InputProvider.raw(
            """
                162,817,812
                57,618,57
                906,360,560
                592,479,940
                352,342,300
                466,668,158
                542,29,236
                431,825,988
                739,650,466
                52,470,668
                216,146,977
                819,987,18
                117,168,530
                805,96,715
                346,949,466
                970,615,88
                941,993,340
                862,61,35
                984,92,344
                425,690,689
            """.trimIndent()
        )
    )
    Day8.solveAll()
}

object Day8 : Solutions {

    data class Point(val x: Long, val y: Long, val z: Long)

    infix fun Point.distTo(other: Point) =
        sqrt((other.x - x).toDouble().pow(2) + (other.y - y).toDouble().pow(2) + (other.z - z).toDouble().pow(2))

    fun combinations(pts: List<Point>) = sequence {
        pts.dropLast(1).forEachIndexed { i, point ->
            pts.drop(i + 1).forEach { point2 ->
                yield(point to point2)
            }
        }
    }

    val first = solution {
        part1 {
            val points = lines.map { it.getLongList().let { (x, y, z) -> Point(x, y, z) } }
            val pairs = combinations(points).sortedBy { (a, b) -> a distTo b }
            val circuits = points.map { setOf(it) }.toMutableSet()

            val N = if (points.size < 25) 10 else 1000

            var it = 0
            for ((a, b) in pairs) {
                val aset = circuits.first { a in it }
                val bset = circuits.first { b in it }
                // the puzzle text makes me think non-connections shouldn't count toward the total, but they do
//                if (aset == bset) continue
                circuits.remove(aset)
                circuits.remove(bset)
                circuits.add(aset + bset)
                it++
                if (it == N) break
            }

//            circuits.sortedBy { it.size }.reversed().map { it.size }.debug()
            circuits.sortedBy { it.size }.reversed().take(3).fold(1L) { acc, points -> acc * points.size }
        }

        part2 {
            val points = lines.map { it.getLongList().let { (x, y, z) -> Point(x, y, z) } }
            val pairs = combinations(points).sortedBy { (a, b) -> a distTo b }

            val circuits = points.map { setOf(it) }.toMutableSet()

            for ((a, b) in pairs) {
                val aset = circuits.first { a in it }
                val bset = circuits.first { b in it }
                circuits.remove(aset)
                circuits.remove(bset)
                circuits.add(aset + bset)
                if (circuits.size == 1)
                    return@part2 a.x * b.x
            }
        }
    }

}
