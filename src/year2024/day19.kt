package year2024

import util.*

fun main() {
    Day19.solveAll(
        InputProvider.raw(
            """
        r, wr, b, g, bwu, rb, gb, br

        brwrr
        bggr
        gbbr
        rrbgbr
        ubwu
        bwurrg
        brgr
        bbrgwb
    """.trimIndent()
        )
    )

    Day19.solveAll()
}


object Day19 : Solutions {

    fun check(pattern: String, towels: Set<String>): Boolean {
        if (pattern.isEmpty()) return true
        return towels.filter { it.length <= pattern.length && it.startsWith(pattern.substring(0, it.length)) }
            .any { check(pattern.substring(it.length), towels) }

    }

    val solution = puzzle {
        part1 {
            val (first, second) = input.split("\n\n")
            val towels = first.split(", ").toSet()
            val patterns = second.lines()

            patterns.count {
                check(it, towels)
            }
        }

        // too slow
//        part2 {
//            val (first, second) = input.split("\n\n")
//            val towels = first.split(", ").toSet()
//            val patterns = second.lines()
//
////            sequence { patterns.forEach { findAll(it, towels) } }.count()
//
//            patterns.parMap(Dispatchers.Default) {
//                sequence { findAll(it, towels) }.count()
//            }.sum()
//        }
    }

    val cached = puzzle {
        val parser = parser {
            val (first, second) = input.split("\n\n")
            second.lines() to first.split(", ").toList()
        }

        fun findAll(pattern: String, towels: List<String>, cache: MutableMap<String, Long>): Long =
            cache.getOrPut(pattern) {
                when {
                    pattern.isEmpty() -> 1L
                    else -> towels.filter { pattern.startsWith(it) }
                        .sumOf { findAll(pattern.substring(it.length), towels, cache) }
                }
            }

        part1(parser) { (patterns, towels) ->
            patterns.count {
                findAll(it, towels, mutableMapOf()) > 0
            }
        }

        part2(parser) { (patterns, towels) ->
            patterns.sumOf { findAll(it, towels, mutableMapOf()) }
        }
    }
}