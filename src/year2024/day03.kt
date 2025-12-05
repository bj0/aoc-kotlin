package year2024

import util.*


fun main() {
    // fold is fastest, recursive is ~40x slower
//    listOf(Day3::solution, Day3::fold, Day3::recursive).solveAll()
    Day3.solveAll()
}

object Day3 : Solutions {
    private const val MUL = """mul\(\d+,\d+\)"""
    private const val DO = """do\(\)"""
    private const val DONT = """don't\(\)"""


    val solution = solution {
        part1 {
            MUL.toRegex().findAll(input)
                .sumOf { it.value.getIntList().product() }
        }

        part2 {
            var on = true
            """($DONT|$DO|$MUL)""".toRegex().findAll(input)
                .sumOf {
                    when (it.groupValues[1]) {
                        "don't()" -> 0.also { on = false }
                        "do()" -> 0.also { on = true }
                        else -> if (on) it.value.getIntList().product() else 0
                    }
                }
        }
    }

    val fold = solution {
        part2 {
            """($MUL|$DONT|$DO)""".toRegex().findAll(input)
                .fold(true to 0) { (on, s), m ->
                    when (m.groupValues[1]) {
                        "don't()" -> false to s
                        "do()" -> true to s
                        else -> on to if (on) s + m.value.getIntList().product() else s
                    }
                }.second
        }
    }

    val recursive = solution {
        part2 {
            tailrec fun munch(matches: List<MatchResult>, on: Boolean = true, sum: Int = 0): Int {
                val res = matches.firstOrNull() ?: return sum
                val next = matches.subList(1, matches.size)
                return when (res.groupValues[1]) {
                    "don't()" -> munch(next, false, sum)
                    "do()" -> munch(next, true, sum)
                    else -> munch(next, on, if (on) sum + res.value.getIntList().product() else sum)
                }
            }
            munch("""($MUL|$DONT|$DO)""".toRegex().findAll(input).toList())
        }
    }

    val trick = solution {
        part2 {
            """$DONT.*?($DO|$)|($MUL)""".toRegex(RegexOption.DOT_MATCHES_ALL).findAll(input)
                .sumOf { m -> m.groupValues[2].getIntList().product() }
        }
    }

    val trick2 = solution {
        part2 {
            """$DONT.*?($DO|$)|mul\((\d+),(\d+)\)""".toRegex(RegexOption.DOT_MATCHES_ALL).findAll(input)
                .sumOf { m -> (m.groupValues[2].toIntOrNull() ?: 0) * (m.groupValues[3].toIntOrNull() ?: 0) }
        }
    }
}