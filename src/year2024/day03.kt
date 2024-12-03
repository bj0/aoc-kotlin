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
                        it.value.startsWith("mul") -> if (on) it.value.getIntList().product() else 0
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
                        m.value.startsWith("mul") -> on to if (on) s + m.value.getIntList().product() else s
                        m.value.startsWith("don") -> false to s
                        else -> true to s
                    }
                }.second
        }
    }

    val recursive = puzzle {
        part2 {
            tailrec fun munch(matches: List<MatchResult>, on: Boolean = true, sum: Int = 0): Int {
                val res = matches.firstOrNull()?.value ?: return sum
                val next = matches.subList(1, matches.size)
                return when {
                    "mul" in res -> munch(next, on, if (on) sum + res.getIntList().product() else sum)
                    "'" in res -> munch(next, false, sum)
                    else -> munch(next, true, sum)
                }
            }
            munch("""(mul\(\d+,\d+\)|do\(\)|don't\(\))""".toRegex().findAll(input).toList())
        }
    }
}