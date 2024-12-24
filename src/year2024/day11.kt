@file:Suppress("unused")

package year2024

import util.*
import kotlin.math.abs
import kotlin.math.log10

fun main() {
    Day11.solveAll(
        InputProvider.raw(
            """
            125 17
        """.trimIndent()
        )
    )
    Day11.solveAll()
}

object Day11 : Solutions {

    // this method is too slow for part2
    val solution = puzzle {
        part1 {
            val stones = input.getLongList().asSequence()

            generateSequence(stones) { sts ->
                sequence {
                    sts.forEach { s ->
                        when {
                            s == 0L -> yield(1)
                            s.digits % 2 == 0 -> {
                                val (left, right) = s.split()
                                yield(left)
                                yield(right)
                            }

                            else -> yield(s * 2024)
                        }
                    }
                }
            }.drop(25).first().count()
        }
    }

    val map = puzzle {
        // this is maybe 1ms faster than the shorter one
//        fun blink(stone: Long): List<Long> =
//            when (stone) {
//                0L -> listOf(1L)
//                in (10..99) -> listOf(stone / 10, stone % 10)
//                in (1_000..9_999) -> listOf(stone / 100, stone % 100)
//                in (100_000..999_999) -> listOf(stone / 1_000, stone % 1_000)
//                in (10_000_000..99_999_999) -> listOf(stone / 10_000, stone % 10_000)
//                in (1_000_000_000..9_999_999_999) -> listOf(stone / 100_000, stone % 100_000)
//                in (100_000_000_000..999_999_999_999) -> listOf(stone / 1_000_000, stone % 1_000_000)
//                in (10_000_000_000_000..99_999_999_999_999) -> listOf(stone / 10_000_000, stone % 10_000_000)
//
//                else -> listOf(stone * 2024)
//            }

        // a little faster with lists than sequences
        fun blink(stone: Long): List<Long> =
            when {
                stone == 0L -> listOf(1)
                stone.digits % 2 == 0 -> {
                    val (left, right) = stone.split()
                    listOf(left, right)
                }

                else -> listOf(stone * 2024)
            }
//        fun blink(stone: Long) = sequence {
//            when {
//                stone == 0L -> yield(1)
//                stone.digits % 2 == 0 -> {
//                    val (left, right) = stone.split()
//                    yield(left)
//                    yield(right)
//                }
//
//                else -> yield(stone * 2024)
//            }
//        }

        fun blink(stones: Map<Long, Long>, i: Int) = (0..<i).fold(stones) { acc, _ ->
//            acc.entries.flatMap { (k, v) -> blink(k).map { it to v } }
//                .groupingBy { it.first }.fold(0L) { acc, i -> acc + i.second }
            // a little faster with mutable map
            buildMap {
                acc.forEach { (k, v) ->
                    blink(k).forEach { put(it, getOrDefault(it, 0) + v) }
                }
            }
        }.values.sum()

        part1 {
            val stones = input.getLongList()

            blink(stones.associateWith { 1L }, 25)
        }
        part2 {
            val stones = input.getLongList()

            blink(stones.associateWith { 1L }, 75)
        }

    }

    val recursion = puzzle {
        fun blink(stone: Long, times: Int, seen: MutableMap<Pair<Long, Int>, Long>): Long {
            fun innerBlink(stone: Long, steps: Int): Long {
                seen[stone to steps]?.let { return it }
                if (steps == 0) return 1L

                return when {
                    stone == 0L -> innerBlink(1, steps - 1)
                    stone.digits % 2 == 0 -> {
                        val (left, right) = stone.split()
                        innerBlink(left, steps - 1) + innerBlink(right, steps - 1)
                    }

                    else -> innerBlink(stone * 2024, steps - 1)
                }.also { seen[stone to steps] = it }
            }
            return innerBlink(stone, times)
        }
        part1 {
            val stones = input.getLongList()

            val seen = mutableMapOf<Pair<Long, Int>, Long>()
            stones.sumOf { blink(it, 25, seen) }
        }
        part2 {
            val stones = input.getLongList()

            val seen = mutableMapOf<Pair<Long, Int>, Long>()
            stones.sumOf { blink(it, 75, seen) }
        }
    }

    val other = puzzle {
        fun Long.blink(times: Long) =
//            when {
//                this == 0L -> listOf(1L to times)
//                this.digits % 2 == 0 -> {
//                    val (left, right) = this.split()
//                    listOf(left to times, right to times)
//                }
//
//                else -> listOf(this * 2024 to times)
//            }
            when (this) {
                0L -> listOf(1L to times)
                in (10..99) -> listOf(this / 10 to times, this % 10 to times)
                in (1_000..9_999) -> listOf(this / 100 to times, this % 100 to times)
                in (100_000..999_999) -> listOf(this / 1_000 to times, this % 1_000 to times)
                in (10_000_000..99_999_999) -> listOf(this / 10_000 to times, this % 10_000 to times)
                in (1_000_000_000..9_999_999_999) -> listOf(this / 100_000 to times, this % 100_000 to times)
                in (100_000_000_000..999_999_999_999) -> listOf(this / 1_000_000 to times, this % 1_000_000 to times)
                in (10_000_000_000_000..99_999_999_999_999) -> listOf(
                    this / 10_000_000 to times,
                    this % 10_000_000 to times
                )

                else -> listOf(this * 2024 to times)
            }

        fun Map<Long, Long>.blink() = entries.flatMap { (s, times) -> s.blink(times) }
            .groupingBy { it.first }.fold(0L) { acc, (_, times) -> acc + times }

        fun blinks(input: Map<Long, Long>, number: Int): Long {
            var x = input
            repeat(number) { x = x.blink() }
            return x.values.sum()
        }

        val parser = parser { input.getLongList().associateWith { 1L } }

        part1(parser) { blinks(it, 25) }

        part2(parser) { blinks(it, 75) }
    }

    private inline fun <R> Long.evolve(f: (Long, Long) -> R): R = when (this) {
        0L -> f(1L, -1L)
        in 10L..99L -> f(this / 10, this % 10)
        in 1000L..9999L -> f(this / 100, this % 100)
        in 100000L..999999L -> f(this / 1000, this % 1000)
        in 10000000L..99999999L -> f(this / 10000, this % 10000)
        in 1000000000L..9999999999L -> f(this / 100000, this % 100000)
        in 100000000000L..999999999999L -> f(this / 1000000, this % 1000000)
        in 10000000000000L..99999999999999L -> f(this / 10000000, this % 10000000)
        in 1000000000000000L..9999999999999999L -> f(this / 100000000, this % 100000000)
        in 100000000000000000L..999999999999999999L -> f(this / 1000000000, this % 1000000000)
        else -> f(this * 2024, -1L)
    }

    val aok = puzzle {
        fun Map<Long, Long>.evolve(): Map<Long, Long> = buildMap(size * 2) {
            for ((stone, count) in this@evolve) {
                fun add(next: Long) = put(next, getOrDefault(next, 0L) + count)
                stone.evolve { a, b ->
                    add(a)
                    if (b != -1L) add(b)
                }
            }
        }

        fun List<Long>.countStones(steps: Int): Long {
            var stones = groupingBy { it }.eachCount().mapValues { it.value.toLong() }
            repeat(steps) { stones = stones.evolve() }
            return stones.values.sum()
        }

        part1 { input.getLongList().countStones(25) }
        part2 { input.getLongList().countStones(75) }
    }
}

// number of digits
val Long.digits
    get() = when (this) {
        0L -> 1
        else -> log10(abs(toDouble())).toInt() + 1
    }

// split an integer with even digits into 2 integers (ie: 6090 -> 60 to 90)
fun Long.split() = (1..<(digits / 2)).fold(10L) { acc, _ -> acc * 10L }.let { q ->
    this / q to this % q
}