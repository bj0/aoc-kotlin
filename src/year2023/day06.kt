package year2023

import util.PuzDSL
import util.getIntList
import util.replace
import util.solveAll

fun main() {
//    listOf(Day06, Day06WithMaxStart).map { it.resolvePuzzle() }.warmup(10).solveAll()
    listOf(Day06, Day06WithMaxStart).solveAll()
}

object Day06 : PuzDSL({
    part1 {
        fun dist(time: Int) = sequence {
            (1..<time).forEach {
                yield(it * (time - it))
            }
        }

        val times = lines.first().getIntList()
        val dists = lines.last().getIntList()

        times.zip(dists).fold(1) { acc, (t, d) ->
            acc * dist(t).filter { it > d }.count()
        }
    }

    part2 {
        fun dist(time: Long) = sequence {
            (1..<time).forEach {
                yield(it * (time - it))
            }
        }

        val time = lines.first().substringAfter(":").replace(" " to "").toLong()
        val d = lines.last().substringAfter(":").replace(" " to "").toLong()

        dist(time).filter { it > d }.count()
    }
})

object Day06WithMaxStart : PuzDSL({
    part1 {
        fun validDists(time: Int, minDist: Int) = sequence {
            val start = time / 2
            (start..<time)
                .map { it * (time - it) }
                .takeWhile { it > minDist }
                .forEach { yield(it) }
            (start - 1 downTo 1)
                .map { it * (time - it) }
                .takeWhile { it > minDist }
                .forEach { yield(it) }
        }

        val times = lines.first().getIntList()
        val dists = lines.last().getIntList()

        times.zip(dists).fold(1) { acc, (t, d) ->
            acc * validDists(t, d).count()
        }
    }

    part2 {
        fun validDists(time: Long, minDist: Long) = sequence {
            val start = time / 2
            (start..<time)
                .map { it * (time - it) }
                .takeWhile { it > minDist }
                .forEach { yield(it) }
            (start - 1 downTo 1)
                .map { it * (time - it) }
                .takeWhile { it > minDist }
                .forEach { yield(it) }
        }

        val time = lines.first().substringAfter(":").replace(" " to "").toLong()
        val d = lines.last().substringAfter(":").replace(" " to "").toLong()

        validDists(time, d).count()
    }
})