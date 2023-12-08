package year2023

import util.*
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

fun main() {
    listOf(Day06, Day06WithMaxStart, Day06WithSearch, Day06WithMath).solveAll()
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

        (times zip dists).fold(1) { acc, (t, d) ->
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

/**
 * faster p1, slower p2
 */
object Day06WithMaxStart : PuzDSL({
    part1 {
        fun validDists(time: Int, minDist: Int) = sequence {
            val start = time / 2
            (start..<time).asSequence()
                .map { it * (time - it) }
                .takeWhile { it > minDist }
                .forEach { yield(it) }
            (start - 1 downTo 1).asSequence()
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

/**
 * binary search is 4-5 orders of magnitude faster on p2
 */
object Day06WithSearch : PuzDSL({
    fun findExtrema(time: Long, minDist: Long): Pair<Long, Long> {
        (time to minDist)
        tailrec fun search(low: Long, high: Long, block: (Long) -> Long): Long {
            val mid = (low + high) / 2
            if (low == mid) return mid + 1
            val d = block(mid)
            val (a, b) = when {
                d > 0 -> low to mid
                d < 0 -> mid to high
                else -> return mid
            }
            if (a == low && b == high) return high
            return search(a, b, block)
        }
        return search(1, time / 2) { it * (time - it) - (minDist + 1) } to
                search(time / 2, time) { (minDist - 1) - it * (time - it) } - 1
    }

    part1 {

        val times = lines.first().getLongList()
        val dists = lines.last().getLongList()

        times.zip(dists).fold(1L) { acc, (t, d) ->
            acc * findExtrema(t, d).let { (a, b) -> (b - a) + 1 }
        }
    }

    part2 {
        val time = lines.first().substringAfter(":").replace(" " to "").toLong()
        val d = lines.last().substringAfter(":").replace(" " to "").toLong()

        findExtrema(time, d).let { (a, b) -> (b - a) + 1 }
    }
})


object Day06WithMath : PuzDSL({
    fun winCount(time: Long, dist: Long): Long {
        // x*(time-x) < dist
        // x**2 - time*x > -dist
        // x**2 - time*x + (time/2)**2 > (time/2)**2 - dist
        // |x - time| > sqrt((time/2)**2 - dist)
        val b = time/2.0
        val d = sqrt(b*b-dist)
        return (ceil(b+d-1) - floor(b-d+1) + 1).toLong()
    }

    part1 {

        val times = lines.first().getLongList()
        val dists = lines.last().getLongList()

        times.zip(dists).fold(1L) { acc, (t, d) ->
            acc * winCount(t, d)
        }
    }

    part2 {
        val time = lines.first().substringAfter(":").replace(" " to "").toLong()
        val d = lines.last().substringAfter(":").replace(" " to "").toLong()

        winCount(time, d)
    }
})