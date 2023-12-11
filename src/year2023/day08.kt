package year2023

import util.*

fun main() {
    listOf(Day08).solveAll(
//        InputProvider.Example
    )
}

object Day08 : PuzDSL({

    data class GhostMap(val directions: String, val map: Map<String, Map<Char, String>>)

    val parser = parser {
        val dirs = lines.first()
        val map = lines.drop(2).associate { line ->
            val (frm, to) = line.split(" = ")
            val (left, right) = to.trim('(', ')').split(", ")
            frm to mapOf('L' to left, 'R' to right)
        }
        GhostMap(dirs, map)
    }

    //18113
    part1(parser) { map ->
        map.directions.asSequence().repeat().runningFold("AAA") { cur, dir ->
            map.map[cur]!![dir]!!
        }.indexOfFirst { it == "ZZZ" }
    }

    //12315788159977
    part2(parser) {map ->

        val starts = map.map.keys.filter { it.endsWith('A') }

        starts.map { start ->
            map.directions.asSequence().repeat().runningFold(start) {cur, dir ->
                map.map[cur]!![dir]!!
            }.indexOfFirst { it.endsWith("Z") }.toLong()
        }.lcm()
    }
})
