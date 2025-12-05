package year2024

import util.*

fun main() {
    Day10.solveAll(
        InputProvider.raw(
            """
            89010123
            78121874
            87430965
            96549874
            45678903
            32019012
            01329801
            10456732
        """.trimIndent()
        )
    )

    Day10.solveAll()
}


object Day10 : Solutions {

    val sequence = solution {
        part2 {
            val map = lines.map { line -> line.map { it.digitToInt() } }.toGrid()
            val starts = map.findAll { _, c -> c == 0 }

            fun walk(path: List<LongPoint>, height: Int = 0): Sequence<List<LongPoint>> = sequence {
                if (height == 9) yield(path)
                path.last().neighbors()
                    .filter { map[it] == height + 1 }
                    .forEach { neighbor -> yieldAll(walk(path + neighbor, height + 1)) }
            }

            starts.map { walk(listOf(it)).toSet() }.sumOf { it.size }
        }
    }

    val dij = solution {
        part2 {
            val map = lines.map { line -> line.map { it.digitToInt() } }.toGrid()
            val starts = map.findAll { _, c -> c == 0 }

            starts.map { start ->
                dijkstraAll(
                    listOf(start),
                    { map[it.last()] == 9 },
                    { 1 }) { path ->
                    val here = path.last()
                    here.neighbors()
                        .filter { map[it] == map[here]!! + 1 }
                        .forEach { yield(path + it) }
                }.toSet()
            }.sumOf { it.size }
        }
    }

    val sequenceScope = solution {
        part2 {
            val map = lines.map { line -> line.map { it.digitToInt() } }.toGrid()
            val starts = map.findAll { _, c -> c == 0 }

            suspend fun SequenceScope<List<LongPoint>>.walk(path: List<LongPoint>, height: Int = 0) {
                if (height == 9) yield(path)
                path.last().neighbors()
                    .filter { map[it] == height + 1 }
                    .forEach { neighbor -> walk(path + neighbor, height + 1) }
            }

//            starts.parMap(Dispatchers.Default) { sequence { walk(listOf(it)) }.toSet() }.sumOf { it.size }
            starts.map { sequence { walk(listOf(it)) }.toSet() }.sumOf { it.size }
        }
    }

    val recursion = solution {
        part2 {
            val map = lines.map { line -> line.map { it.digitToInt() } }.toGrid()
            val starts = map.findAll { _, c -> c == 0 }

            fun walk(path: List<LongPoint>, height: Int = 0): Set<List<LongPoint>> {
                if (height == 9) return setOf(path)
                return path.last().neighbors()
                    .filter { map[it] == height + 1 }
                    .fold(emptySet()) { acc, neighbor -> acc + walk(path + neighbor, height + 1) }
            }

            starts.map { walk(listOf(it)) }.sumOf { it.size }
        }
    }


    val loop = solution {
        part1 {
            val map = lines.map { line -> line.map { it.digitToInt() } }.toGrid()
            val starts = map.findAll { _, c -> c == 0 }

            starts.sumOf { start ->
                val seen = mutableSetOf<LongPoint>()
                val q = mutableListOf(start)
                var count = 0
                while (q.isNotEmpty()) {
                    val n = q.removeFirst()
                    if (n in seen) continue
                    seen.add(n)
                    val height = map[n]!!
                    if (height == 9) {
                        count += 1
                        continue
                    }
                    q += n.neighbors().filter { map[it] == height + 1 }

                }
                count
            }
        }

        part2 {
            val map = lines.map { it.map { it.digitToInt() } }.toGrid()
            val starts = map.findAll { point, c -> c == 0 }

            starts.sumOf { start ->
                val q = mutableListOf(listOf(start))
                val paths = mutableSetOf<List<LongPoint>>()
                while (q.isNotEmpty()) {
                    val path = q.removeFirst()
                    val n = path.last()
                    val height = map[n]!!
                    if (height == 9) {
                        paths.add(path)
                        continue
                    }
                    q += n.neighbors().filter { map[it] == height + 1 }.map { neighbor -> path + neighbor }
                }
                paths.size
            }
        }
    }
}