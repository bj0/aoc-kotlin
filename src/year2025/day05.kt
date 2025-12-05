package year2025

import util.InputProvider
import util.Solutions
import util.puzzle
import util.solveAll
import kotlin.math.max

fun main() {
    Day5.solveAll(
        InputProvider.raw(
            """
                3-5
                10-14
                16-20
                12-18

                1
                5
                8
                11
                17
                32
            """.trimIndent()
        )
    )
    Day5.solveAll()
}

object Day5 : Solutions {


    val first = puzzle {
        part1 {
            val (freshRanges, idList) = input.split("\n\n")
            val fresh = freshRanges.lines().map { line -> line.split("-").map { it.toLong() }.let { (a, b) -> a..b } }
            idList.lines().map { it.toLong() }.count { id -> fresh.any { id in it } }
        }

        part2 {
            val (freshRanges, idList) = input.split("\n\n")
            val fresh = freshRanges.lines().map { line -> line.split("-").map { it.toLong() }.let { (a, b) -> a..b } }
                .sortedBy { it.first }

            var list = fresh
            var count = 0L
            var end = 0L
            while (list.isNotEmpty()) {
                val first = list.first()
                val start = max(first.first, end + 1)
                end = list.takeWhile { it.first <= first.last }.maxOf { it.last }
                count += (end - start) + 1
                list = list.filter { it.last > end }
            }
            count
        }
    }

    val tailrec = puzzle {

        part2 {
            tailrec fun countFresh(ranges: List<LongRange>, oldCount: Long = 0, lastEnd: Long = 0): Long {
                val first = ranges.first()
                val start = max(first.first, lastEnd + 1)
                val end = ranges.takeWhile { it.first <= first.last }.maxOf { it.last }
                val nextRanges =
                    ranges.filter { it.last > end }//.debug{"count: $count => ${count + (end-start)+1}start:$start, end:$end, $this"}
                val count = oldCount + (end - start) + 1
                return if (nextRanges.isEmpty()) count else countFresh(nextRanges, count, end)
            }

            val (fstring, idstring) = input.split("\n\n")
            val fresh = fstring.lines().map { line -> line.split("-").map { it.toLong() }.let { (a, b) -> a..b } }
                .sortedBy { it.first }


            countFresh(fresh)
        }
    }
}
