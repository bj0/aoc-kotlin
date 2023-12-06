package year2023

import util.println
import util.readInput
import util.timedResult

fun main() {
    data class Data(val seeds: List<Long>, val maps: List<List<Pair<LongRange, Long>>>)

    fun part1(input: Data): Long = input.seeds.minOf { s ->
        var x = s
        input.maps.forEach { x = it.find { (r, _) -> x in r }?.let { (r, d0) -> d0 + (x - r.first) } ?: x }
        x
    }

    fun part2(input: Data): Long {
        var seedRange = input.seeds.chunked(2).map { (s, d) -> s to d }
        // translate ranges through each map
        input.maps.forEach { map ->
            // new seed range
            seedRange = sequence {
                // from old seed range
                seedRange.forEach { (s, d) ->
                    val sr = s until s + d
                    var x = sr.first
                    while (x < sr.last + 1) {
                        val m = map.find { (r, _) -> x in r }
                        if (m == null) {
                            // not in map range
                            val x0 = x;
                            while ((x < sr.last) && !map.any { (r, _) -> (x + 1) in r }) {
                                x += 1
                            }
                            yield(x0 to (x - x0))
                            x += 1
                        } else {
                            if (sr.last > m.first.last) {
                                // range extends past map, split
                                val (r, d0) = m
                                yield((d0 + (x - r.first)) to (r.last - x + 1L))
                                x = m.first.last + 1
                            } else {
                                // range inside map
                                val (r, d0) = m
                                yield((d0 + (x - r.first)) to (sr.last - x + 1L))
                                break
                            }
                        }
                    }
                }
            }.toList()
        }
        return seedRange.minOf { it.first }
    }

    fun List<String>.parse(): Data {
        val seeds = this[0].split(":")[1].strip().split(" ").map { it.toLong() }
        val maps = this.drop(2).joinToString("\n").split("\n\n").map { block ->
            block.split("\n").drop(1).map { row ->
                val (d0, s0, n) = row.split(" ").map(String::toLong)
                (s0 until s0 + n) to d0
            }
        }
        return Data(seeds, maps)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day05_test")
    check(part1(testInput.parse()).also { it.println() } == 35L)

    val input = readInput("day05")
    timedResult("part 1") {
        part1(input.parse())
    }

    // 47909639 from brute force, took forever
    timedResult("part 2") {
        part2(input.parse())
    }
}
