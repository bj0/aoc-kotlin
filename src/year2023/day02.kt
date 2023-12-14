package year2023

import util.*

fun main() {
    ::solution.solve(InputProvider.Example)

    listOf(::solution, ::old).solveAll()
}

private val solution = puzzle {
    data class Game(val id: Int, val shown: List<Map<String, Int>>)

    val parser = lineParser { line ->
        val parts = line.split(": ", "; ")
        val id = parts.first().substringAfter(" ").toInt()
        Game(id, parts.drop(1).map { part ->
            part.split(", ").associate {
                it.substringAfter(' ') to it.substringBefore(' ').toInt()
            }
        })
    }

    //2061
    part1(parser) { games ->
        val limits = mapOf("red" to 12, "green" to 13, "blue" to 14)
        games.filter { game ->
            game.shown.all { it.all { (color, n) -> n <= (limits[color] ?: 0) } }
        }.sumOf { it.id }
    }

    // 72596
    part2(parser) { games ->
        fun Game.minBag() = shown.flatMap { it.entries }.groupingBy { it.key }
            .eachMax { it.value }
//            .fold(0) { acc, e -> max(acc, e.value) }
//            .aggregate { _, acc: Int?, el, _ -> max(acc ?: 0, el.value) }
//        fun Game.minBag() = buildMap<String, Int> {
//            shown.flatMap { it.entries }.forEach { (color, n) ->
//                merge(color, n) { a, b -> max(a, b) }
//            }
//        }

        fun Map<String, Int>.power() = values.fold(1, Int::times)

        games.sumOf { it.minBag().power() }
    }
}

private val old = puzzle {
    val parser = parser {
        val pat = """(\d+) (\w+),?""".toRegex()
        lines.mapIndexed { i, line ->
            i + 1 to line.trim().split(";").map { group ->
                pat.findAll(group).map {
                    val (n, col) = it.destructured
                    col to n.toInt()
                }.toMap()
            }
        }.groupBy({ it.first }, { it.second })
    }

    part1(parser) { games ->
        games.filterValues { groups ->
            groups.all { cols ->
                cols.maxOf { it["blue"] ?: 0 } <= 14 &&
                        cols.maxOf { it["red"] ?: 0 } <= 12 &&
                        cols.maxOf { it["green"] ?: 0 } <= 13
            }
        }.keys.sum()
    }

    part2(parser) { games ->
        games.map { (_, groups) ->
            listOf("red", "green", "blue").map { c ->
                groups.maxOf { g -> g.maxOf { col -> col[c] ?: 0 } }
            }.reduce(Int::times)
        }.sum()
    }
}
