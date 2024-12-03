package year2024

import util.*


fun main() {
    // fold is fastest, recursive is ~40x slower
    listOf(Day3::solution, Day3::fold, Day3::recursive).solveAll()
}

object Day3 : Solutions {

    val solution = puzzle {

        part1 {
            """mul\(\d+,\d+\)""".toRegex().findAll(input)
                .sumOf { it.value.getIntList().product() }
        }

        part2 {
            var on = true
            """(mul\(\d+,\d+\)|do\(\)|don't\(\))""".toRegex().findAll(input)
                .sumOf {
                    when {
                        it.value.startsWith("mul") && on -> it.value.getIntList().product()
                        it.value.startsWith("don") -> 0.also { on = false }
                        else -> 0.also { on = true }
                    }
                }
        }
    }

    val fold = puzzle {
        part2 {
            """(mul\(\d+,\d+\)|do\(\)|don't\(\))""".toRegex().findAll(input)
                .fold(true to 0) { (on, s), m ->
                    when {
                        m.value.startsWith("mul") && on -> on to s + m.value.getIntList().product()
                        m.value.startsWith("don") -> false to s
                        else -> true to s
                    }
                }.second
        }
    }

    val recursive = puzzle {
        part2 {
            tailrec fun munch(matches: Sequence<MatchResult>, on: Boolean = true, sum: Int = 0): Int {
                val res = matches.firstOrNull()?.value ?: return sum
                return when {
                    "mul" in res && on -> munch(matches.drop(1), true, sum + res.getIntList().product())
                    "'" in res -> munch(matches.drop(1), false, sum)
                    else -> munch(matches.drop(1), true, sum)
                }
            }
            munch("""(mul\(\d+,\d+\)|do\(\)|don't\(\))""".toRegex().findAll(input))
        }
    }
}