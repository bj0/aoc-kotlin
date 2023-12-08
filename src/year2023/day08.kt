package year2023

import util.PuzDSL
import util.lcm
import util.repeat
import util.solveAll

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
        }.takeWhile { it != "ZZZ" }.count()
    }

    //12315788159977
    part2(parser) {map ->

        val starts = map.map.keys.filter { it.endsWith('A') }

        starts.map { s ->
            map.directions.asSequence().repeat().runningFold(s) {cur, dir ->
                map.map[cur]!![dir]!!
            }.takeWhile { !it.endsWith("Z") }.count().toLong()
        }.lcm()
    }
})
