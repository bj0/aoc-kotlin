@file:Suppress("EnumEntryName")

package year2023

import util.getIntList
import util.puzzle
import util.solveAll


fun main() {
    listOf(Day22::solution).solveAll(
//        InputProvider.Example
    )
}


object Day22 {

    data class Point3(val x: Int, val y: Int, val z: Int) {
        companion object {
            operator fun invoke(pts: List<Int>) = Point3(pts[0], pts[1], pts[2])
        }

        override fun toString() = "($x,$y,$z)"
    }

    operator fun Point3.rangeTo(other: Point3) = sequence {
        for (i in x..other.x) {
            for (j in y..other.y) {
                for (k in z..other.z) {
                    yield(Point3(i, j, k))
                }
            }
        }
    }

    val Point3.down get() = copy(z = z - 1)

    data class Brick(val id: Int, val cubes: List<Point3>) {

        fun moveDown() = copy(cubes = cubes.map { p -> p.down })

        companion object {
            fun from(id: Int, ends: List<List<Int>>): Brick {
                val (start, finish) = ends.map { Point3(it) }
                return from(id, start, finish)
            }

            fun from(id: Int, start: Point3, end: Point3) = Brick(id, (start..end).toList())
        }

        override fun toString() = "Brick$id$cubes"
    }


    val solution = puzzle {

        val parser = parser {
            lines.withIndex().map { (i, line) ->
                line.split("~").let { ends -> Brick.from(i, ends.map { it.getIntList() }) }
            }
        }

        part1(parser) { bricks ->
            val cubes = buildMap {
                for (block in bricks) {
                    for (p in block.cubes) {
                        put(p, block)
                    }
                }
            }

            fun step(cubes: Map<Point3, Brick>): MutableMap<Point3, Brick> {
                val cubes = cubes.toMutableMap()
                for (block in cubes.values.sortedBy { it.cubes.minOf { c -> c.z } }) {
                    if (block.cubes.all { p -> p.z > 1 && (p.down in block.cubes || p.down !in cubes) }) {
                        // can move!
                        val moved = block.moveDown()
                        cubes.keys.removeAll(block.cubes.toSet())
                        cubes.putAll(moved.cubes.map { it to moved })
                    }
                }

                return cubes
            }

            val stable = generateSequence(cubes) { step(it) }
                .zipWithNext()
                .dropWhile { (a, b) -> a != b }
                .first().second

            stable.values.distinct()

            val supportedBy = buildMap {
                stable.values.forEach { block ->
                    val moved = block.moveDown()
                    put(block, moved.cubes.mapNotNull { stable[it]?.takeIf { it != block } }.distinct())
                }
            }

            supportedBy.keys.map { block ->
                supportedBy.values.count { supports -> block in supports && supports.size == 1 }
            }
                .count { it == 0 }
        }

        part2(parser) { bricks ->
            val cubes = buildMap {
                for (block in bricks) {
                    for (p in block.cubes) {
                        put(p, block)
                    }
                }
            }

            fun step(cubes: Map<Point3, Brick>): MutableMap<Point3, Brick> {
                val cubes = cubes.toMutableMap()
                for (block in cubes.values.sortedBy { it.cubes.minOf { c -> c.z } }) {
                    if (block.cubes.all { p -> p.z > 1 && (p.down in block.cubes || p.down !in cubes) }) {
                        // can move!
                        val moved = block.moveDown()
                        cubes.keys.removeAll(block.cubes.toSet())
                        cubes.putAll(moved.cubes.map { it to moved })
                    }
                }

                return cubes
            }

            val stable = generateSequence(cubes) { step(it) }
                .zipWithNext()
                .dropWhile { (a, b) -> a != b }
                .first().second

            val finalBricks = stable.values.toSet()

            val supportedBy = buildMap {
                finalBricks.forEach { block ->
                    val moved = block.moveDown()
                    put(block, moved.cubes.mapNotNull { p -> stable[p]?.takeIf { it != block } }.toSet())
                }
            }

            val supports = finalBricks.associateWith { brick ->
                supportedBy.filterValues { supports -> brick in supports }.keys
            }

            val safe = finalBricks.filter { brick ->
                supports.getValue(brick).none { supportedBy.getValue(it).size == 1 }
            }.toSet()

            val unsafe = finalBricks - safe

            unsafe.sumOf { brick ->
                val fell = mutableSetOf(brick)
                val q = ArrayDeque(listOf(brick))
                while (q.isNotEmpty()) {
                    val b = q.removeFirst()
                    fell.add(b)

                    q += supports.getValue(b).filter { (supportedBy.getValue(it) - fell).isEmpty() }
                }

                fell.size - 1
            }
        }
    }
}

