package year2024

import util.*


fun main() {
    Day5.solveAll(
        InputProvider.raw(
            """
                47|53
                97|13
                97|61
                97|47
                75|29
                61|13
                75|53
                29|13
                97|29
                53|29
                61|53
                97|53
                61|29
                47|13
                75|47
                97|75
                47|61
                75|61
                47|29
                75|13
                53|13

                75,47,61,53,29
                97,61,53,29,13
                75,29,13
                75,97,47,61,53
                61,13,29
                97,13,75,29,47
            """.trimIndent()
        )
    )
    Day5.solveAll()
}

object Day5 : Solutions {

    // fastest
    val comparator = puzzle {
        val parser = parser {
            val (order, pages) = input.split("\n\n")
            buildMap {
                order.lines().forEach {
                    val (a, b) = it.split("|")
                    getOrPut(a) { mutableSetOf<String>() }.add(b)
                }
            } to pages.lines().map { it.split(",") }
        }

        part1(parser) { (order, pages) ->
            fun List<String>.isValid() =
                all { a -> order[a]?.all { b -> b !in this || indexOf(a) < indexOf(b) } ?: true }
            pages.filter { it.isValid() }.sumOf { it[it.size / 2].toInt() }
        }

        part2(parser) { (order, pages) ->
            fun List<String>.isValid() =
                all { a -> order[a]?.all { b -> b !in this || indexOf(a) < indexOf(b) } ?: true }

            val comparator = Comparator<String> { left, right ->
                val leftRule = order[left]
                val rightRule = order[right]
                when {
                    rightRule != null && left in rightRule -> 1
                    leftRule != null && right in leftRule -> -1
                    else -> 0
                }
            }

            pages.filterNot { it.isValid() }
                .map { line ->
                    line.sortedWith(comparator)
                }.sumOf { it[it.size / 2].toInt() }

        }
    }

    val recursion = puzzle {
        val parser = parser {
            val (order, pages) = input.split("\n\n")
            order.lines().map { it.split("|") } to pages.lines().map { it.split(",") }
        }

        part1(parser) { (order, pages) ->
            fun List<String>.isValid() = order.filter { it.all { it in this } }.all { (a, b) ->
                indexOf(a) < indexOf(b)
            }
            pages.filter { it.isValid() }.sumOf { it[it.size / 2].toInt() }
        }

        part2(parser) { (order, pages) ->
            fun List<String>.isValid() = order.filter { it.all { it in this } }.all { (a, b) ->
                indexOf(a) < indexOf(b)
            }

            tailrec fun order(sorted: List<String>, left: List<String>): List<String> {
                if (left.isEmpty()) return sorted
                val next = left.find { n -> order.none { (a, b) -> b == n && a in left } }!!
                return order(sorted + next, left - next)
            }
            pages.filterNot { it.isValid() }
                .map { line ->
                    order(emptyList(), line)
                }.sumOf { it[it.size / 2].toInt() }
        }

    }

    // original solution
    val solution = puzzle {
        val parser = parser {
            val (order, pages) = input.split("\n\n")
            order.lines().map { it.split("|").let { (a, b) -> a to b } } to pages.lines().map { it.split(",") }
        }

        part1(parser) { (order, pages) ->
            pages.filter {
                val line = it.reversed()
                line.withIndex().toList().dropLast(1).all { (i, n) ->
                    val after = order.filter { it.first == n }.map { it.second }.toSet()
                    val before = line.subList(i + 1, line.size).toSet()
                    after intersect before == emptySet<String>()
                }
            }.sumOf { it[it.size / 2].toInt() }
        }

        part2 {
            val (order, pages) = input.split("\n\n")

            val pairs = order.lines().map { it.split("|").let { it[0] to it[1] } }
            pages.lines().map { it.split(",") }.filter {
                val line = it.reversed()
                line.withIndex().toList().dropLast(1).any { (i, n) ->
                    val after = pairs.filter { it.first == n }.map { it.second }.toSet()
                    val before = line.subList(i + 1, line.size).toSet()
                    after intersect before != emptySet<String>()
                }
            }.map { line ->
                fun order(line: List<String>): List<String> = if (line.isEmpty()) emptyList() else {
                    val next = line.find { n -> pairs.none { it.second == n && line.contains(it.first) } }!!
                    listOf(next) + order(line - next)
                }
                order(line)
            }.sumOf { it[it.size / 2].toInt() }
        }
    }
}