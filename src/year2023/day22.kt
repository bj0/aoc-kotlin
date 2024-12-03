package year2023

import util.*


fun main() {
    listOf(Day22::solution).solveAll(
//        InputProvider.Example
    )
}


object Day22 {

    val solution = puzzle {

        val parser = parser {
            lines.map { line ->
                line.split("~").let { ends -> Brick.from(ends.map { it.getIntList() }) }
            }
        }

        part1(parser) { bricks ->
            val snapshot = buildMap {
                for (block in bricks) {
                    for (p in block.cubes) {
                        put(p, block)
                    }
                }
            }

            val stable = settle(snapshot)

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

            finalBricks.count { brick ->
                supports.getValue(brick).none { supportedBy.getValue(it).size == 1 }
            }
        }

        part2(parser) { bricks ->
            val snapshot = buildMap {
                for (block in bricks) {
                    for (p in block.cubes) {
                        put(p, block)
                    }
                }
            }


            val stable = settle(snapshot)

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

    private fun settle(cubes: Map<IntPoint3, Brick>): Map<IntPoint3, Brick> {
        fun step(cubes: Map<IntPoint3, Brick>): Map<IntPoint3, Brick> {
            return buildMap {
                putAll(cubes)
                for (block in values.sortedBy { it.cubes.minOf { c -> c.z } }) {
                    if (block.cubes.all { p -> p.z > 1 && (p.down in block.cubes || p.down !in this) }) {
                        // can move!
                        val moved = block.moveDown()
                        keys.removeAll(block.cubes.toSet())
                        putAll(moved.cubes.map { it to moved })
                    }
                }
            }
        }

        return generateSequence(cubes) { step(it) }.zipWithNext().dropWhile { (a, b) -> a != b }.first().second
    }


    val IntPoint3.down get() = copy(z = z - 1)

    data class Brick(val cubes: List<IntPoint3>) {
        fun moveDown() = copy(cubes = cubes.map { p -> p.down })

        companion object {
            fun from(ends: List<List<Int>>): Brick {
                val (start, finish) = ends.map { IntPoint3(it) }
                return from(start, finish)
            }

            private fun from(start: IntPoint3, end: IntPoint3) = Brick((start..end).toList())
        }
    }

}

