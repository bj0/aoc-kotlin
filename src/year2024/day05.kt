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

    // fastest, uses a map and a comparator
    val comparator = solution {
        val parser = parser {
            val (order, pages) = input.split("\n\n")

            // using a map for comparator is faster
            val rules = buildMap {
                order.lines().forEach {
                    val (a, b) = it.split("|")
                    put(a to b, -1)
                    put(b to a, 1)
                }
            }
            val comparator = Comparator<String> { left, right ->
                rules[left to right]!!
            }

//            val rules = order.lines().map { it.split("|") }
//                .groupBy({ it.first() }) { it.last() }
//            val comparator = Comparator<String> { left, right ->
//                val leftRule = rules[left].orEmpty()
//                val rightRule = rules[right].orEmpty()
//                when {
//                    left in rightRule -> 1
//                    right in leftRule -> -1
//                    else -> 0
//                }
//            }
            comparator to pages.lines().map { it.split(",") }
        }


        //todo i don't think just checking adjacent items works in the general case (what if two unsorted items are
        // separated by an item that doesn't participate in the rules?), but worked for the input
        fun Comparator<String>.isValid(page: List<String>) =
            page.asSequence().zipWithNext().all { (a, b) -> compare(a, b) <= 0 }

        part1(parser) { (order, pages) ->
            pages.filter { order.isValid(it) }.sumOf { it[it.size / 2].toInt() }
        }

        part2(parser) { (order, pages) ->
//            pages.sumOf { page -> if (order.isValid(page)) 0 else page.sortedWith(order)[page.size / 2].toInt() }
            pages.filterNot { order.isValid(it) }
                .map { line ->
                    line.sortedWith(order)
                }.sumOf { it[it.size / 2].toInt() }
        }
    }

    val recursion = solution {
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
    val solution = solution {
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