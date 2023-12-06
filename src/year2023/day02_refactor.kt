package year2023

import util.InputProvider
import util.PuzDSL
import util.solveAll
import kotlin.math.max

object Day02 : PuzDSL({
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

    part1(parser) { games ->
        val limits = mapOf("red" to 12, "green" to 13, "blue" to 14)
        games.filter { game ->
            game.shown.all { it.all { (color, n) -> n <= (limits[color] ?: 0) } }
        }.sumOf { it.id }
    }

    part2(parser) { games ->
        fun Game.minBag() = buildMap<String, Int> {
            shown.flatMap { it.entries }.forEach { (color, n) ->
                merge(color, n) { a, b -> max(a, b) }
            }
        }

        fun Map<String, Int>.power() = values.fold(1, Int::times)

        games.sumOf { it.minBag().power() }
    }
})

fun main() {
    Day02.solveAll(
        input = InputProvider.Example
    )

    Day02.solveAll()
}
