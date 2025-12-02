package year2025

import util.InputProvider
import util.Solutions
import util.puzzle
import util.solveAll


fun main() {
    Day2.solveAll(
        InputProvider.raw(
            """11-22,95-115,998-1012,1188511880-1188511890,222220-222224,1698522-1698528,446443-446449,38593856-38593862,565653-565659,824824821-824824827,2121212118-2121212124"""
        )
    )
    Day2.solveAll()
}

object Day2 : Solutions {
    val first = puzzle {
        val parser = parser { input.split(",").map { it.split("-").map { it.toLong() }.let { it.first()..it[1] } } }
        part1(parser) { ranges ->
            ranges.sumOf { r ->
                r.sumOf {
                    val s = it.toString()
                    val n = s.length / 2
                    if (s.length % 2 == 0 && (s.take(n) == s.drop(n))) it else 0
                }
            }
        }

        part2(parser) { ranges ->
            ranges.sumOf { r ->
                r.sumOf {
                    val s = it.toString()
                    if ((1..s.length / 2).any { i ->
                            val p = s.take(i)
                            var left = s.drop(i)
                            while (left.isNotEmpty() && (left.take(i) == p)) {
                                left = left.drop(i)
                            }
                            left.isEmpty()
                        }) it else 0
                }
            }
        }
    }

    val chunk = puzzle {
        val parser = parser { input.split(",").map { it.split("-").map { it.toLong() }.let { it.first()..it[1] } } }
        part2(parser) { ranges ->
            ranges.sumOf { r ->
                r.sumOf {
                    val s = it.toString()
                    if ((1..s.length / 2).any { i ->
                            val p = s.take(i)
                            s.drop(i).chunked(i).all { it == p }
                        }) it else 0
                }
            }
        }
    }

    val recursive = puzzle {
        val parser = parser { input.split(",").map { it.split("-").map { it.toLong() }.let { it.first()..it[1] } } }
        part2(parser) { ranges ->
            ranges.sumOf { r ->
                r.sumOf {
                    val s = it.toString()
                    if ((1..s.length / 2).any { i ->
                            val p = s.take(i)
                            tailrec fun check(left: String): Boolean {
                                return when {
                                    left.isEmpty() -> true
                                    left.take(i) != p -> false
                                    else -> check(left.drop(i))
                                }
                            }
                            check(s.drop(i))
                        }) it else 0
                }
            }
        }
    }

}

