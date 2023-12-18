package year2023

import util.*
import kotlin.math.absoluteValue

fun main() {
    listOf(Day18::solution, Day18::faster).solveAll(
//        InputProvider.raw(
//"""R 6 (#70c710)
//D 5 (#0dc571)
//L 2 (#5713f0)
//D 2 (#d2c081)
//R 2 (#59c680)
//D 2 (#411b91)
//L 5 (#8ceee2)
//U 2 (#caa173)
//L 1 (#1b58a2)
//U 2 (#caa171)
//R 2 (#7807d2)
//U 3 (#a77fa3)
//L 2 (#015232)
//U 2 (#7a21e3)"""        )
    )
}

object Day18 {
    val solution = puzzle {

        part1 {
            val trench = buildList<Point> {
                add(0 point 0)
                for (line in lines) {
                    val ret = line.split(" ")
                    val dir = Direction.parse(ret[0][0])
                    val n = ret[1].toInt()
                    repeat(n) { add(last() + dir) }
                }
            }

            area(trench)
        }

        part2 {
            val colMap = mapOf('0' to 'R', '1' to 'D', '2' to 'L', '3' to 'U')

            val trench = buildList<PointL> {
                add(0L point 0)
                for (line in lines.map { it.substringAfter("(#").substringBefore(")") }) {
                    val dir = Direction.parse(colMap[line[5]]!!)
                    val n = line.substring(0, 5).toInt(16)
                    repeat(n) { add(last() + dir) }
                }
            }

            area(trench)
        }

    }

    private fun area(trench: List<Point>) =
        trench.windowed(2).sumOf { (p0, p1) -> p0.x * p1.y - p0.y * p1.x }
            .absoluteValue / 2 - trench.size / 2 + 1 + trench.size - 1

    private fun area(trench: List<PointL>) =
        trench.windowed(2).sumOf { (p0, p1) -> p0.x * p1.y - p0.y * p1.x }
            .absoluteValue / 2 - trench.size / 2 + 1 + trench.size - 1


    val faster = puzzle {

        part1 {
            var pos = Point.Zero
            var last = pos
            var perim = 0
            var area = 0
            for (line in lines.map { it.split(" ").take(2) }) {
                val dir = Direction.parse(line[0][0])
                val n = line[1].toInt()
                perim += n
                pos = pos.move(dir, n)
                area += last.x * pos.y - last.y * pos.x
                last = pos
            }
            area / 2 - perim / 2 + 1 + perim
        }

        part2 {
            val colMap = mapOf('0' to 'R', '1' to 'D', '2' to 'L', '3' to 'U')

            var pos = PointL.Zero
            var last = pos
            var perim = 0L
            var area = 0L
            for (line in lines.map { it.substringAfter("(#").substringBefore(")") }) {
                val dir = Direction.parse(colMap[line[5]]!!)
                val n = line.substring(0, 5).toInt(16)
                perim += n
                pos = pos.move(dir, n)
                area += last.x * pos.y - last.y * pos.x
                last = pos
            }
            area / 2 - perim / 2 + 1 + perim
        }
    }
}