package year2024

import util.*

fun main() {
    Day07.solveAll(
        InputProvider.raw(
            """
            190: 10 19
            3267: 81 40 27
            83: 17 5
            156: 15 6
            7290: 6 8 6 15
            161011: 16 10 13
            192: 17 8 14
            21037: 9 7 18 13
            292: 11 6 16 20
        """.trimIndent()
        )
    )

    Day07.solveAll()
}

object Day07 : Solutions {

    val solution = puzzle {
        val parser = lineParser { it.split(":").let { (a, b) -> a.toLong() to b.getLongList() } }

        part1(parser) { lines ->
            fun check(test: Long, acc: Long = 0L, nums: List<Long>): Boolean {
                if (nums.isEmpty()) return acc == test
                val n = nums.first()
                return check(test, acc + n, nums.subList(1, nums.size)) ||
                        check(test, acc * n, nums.subList(1, nums.size))
            }

            lines.filter { (test, nums) -> check(test, 0L, nums) }.sumOf { it.first }
        }

        part2(parser) { lines ->
            fun check(test: Long, acc: Long, nums: List<Long>): Boolean {
                if (nums.isEmpty()) return acc == test
                val n = nums.first()
                return check(test, acc + n, nums.subList(1, nums.size)) ||
                        check(test, acc * n, nums.subList(1, nums.size)) ||
                        check(test, "$acc$n".toLong(), nums.subList(1, nums.size))

            }

            lines.filter { (test, nums) -> check(test, 0L, nums) }.sumOf { it.first }
        }

    }
}

