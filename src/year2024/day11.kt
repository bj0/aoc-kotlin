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

    val recursion = puzzle {
        fun blink(stone: Long, times: Int, seen: MutableMap<Pair<Long, Int>, Long>): Long {
            fun innerEvolve(stone: Long, steps: Int): Long {
                seen[stone to steps]?.let { return it }
                if (steps == 0) return 1L

                return when {
                    stone == 0L -> innerEvolve(1, steps - 1)
                    stone.digits % 2 == 0 -> {
                        val (left, right) = stone.split()
                        innerEvolve(left, steps - 1) + innerEvolve(right, steps - 1)
                    }

                    else -> innerEvolve(stone * 2024, steps - 1)
                }.also { seen[stone to steps] = it }
            }
            return innerEvolve(stone, times)
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
}