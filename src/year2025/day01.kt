package year2025

import util.*


fun main() {
    Day1.solveAll(
        InputProvider.raw(
            """
        L68
        L30
        R48
        L5
        R60
        L55
        L1
        L99
        R14
        L82
    """.trimIndent()
        )
    )
    Day1.solveAll(
    )
}

object Day1 : Solutions {
    val first = solution {
        part1 { ->
            lines.runningFold(50) { s, op ->
                (s + when (op[0]) {
                    'L' -> -op.drop(1).toInt()
                    'R' -> op.drop(1).toInt()
                    else -> error("bad input")
                }).mod(100)
            }.count { it == 0 }
        }

        part2 {
            lines.runningFold(50 to 0) { (s, s0), op ->
                (s.mod(100) + when (op[0]) {
                    'L' -> -op.drop(1).toInt()
                    'R' -> op.drop(1).toInt()
                    else -> error("bad input")
                }) to s.mod(100)
            }.sumOf { (news, s) ->
                when {
                    s == 0 && news <= -100 -> (news..0 step 100).count() - 1
                    (s > 0 && news <= 0) -> (news..0 step 100).count()
                    news >= 100 -> (news downTo 100 step 100).count()
                    else -> 0
                }
            }
        }
    }

    val first2 = solution {
        part2 {
            lines.fold(50 to 0) { (pos, oldCount), op ->
                val newPos = (pos + when (op[0]) {
                    'L' -> -op.drop(1).toInt()
                    'R' -> op.drop(1).toInt()
                    else -> error("bad input")
                })

                newPos.mod(100) to oldCount + when {
                    pos == 0 && newPos <= -100 -> (newPos..0 step 100).count() - 1
                    (pos > 0 && newPos <= 0) -> (newPos..0 step 100).count()
                    newPos >= 100 -> (newPos downTo 100 step 100).count()
                    else -> 0
                }
            }.second
        }
    }

    val brute = solution {
        part2 {
            lines.runningFold(50 to 0) { (s, s0), op ->
                (s.mod(100) + when (op[0]) {
                    'L' -> -op.drop(1).toInt()
                    'R' -> op.drop(1).toInt()
                    else -> error("bad input")
                }) to s.mod(100)
            }.sumOf { (news, s) ->
                when {
                    news > s -> (s + 1..news).count { it.mod(100) == 0 }
                    news < s -> (news..<s).count { it.mod(100) == 0 }
                    else -> 0
                }
            }
        }
    }

    val pd = solution {
        part2 {
            var dial = 50
            var password = 0
            input.lineSequence().forEach {
                val dir = it[0]
                var count = it.drop(1).toInt()
                val click = if (dir == 'L') -1 else 1
                repeat(count) {
                    dial = (dial + click) % 100
                    if (dial == 0) password++
                }
            }
            password
        }
    }
}

