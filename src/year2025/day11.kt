package year2025

import util.*

fun main() {

    Day11.solveAll(
        InputProvider.raw(
            """
                aaa: you hhh
                you: bbb ccc
                bbb: ddd eee
                ccc: ddd eee fff
                ddd: ggg
                eee: out
                fff: out
                ggg: out
                hhh: ccc fff iii
                iii: out
            """.trimIndent()
        )
    )
    Day11.solveAll()
}

object Day11 : Solutions {

    val first = solution {

        part1 {
            val map = buildMap {
                lines.forEach {
                    val (frm, to) = it.split(" ").let { it.first().dropLast(1) to it.drop(1) }
                    to.forEach { getOrPut(frm) { mutableListOf<String>() }.add(it) }
                }
            }

            var paths = 0L
//            val seen = mutableSetOf<String>()
            val q = ArrayDeque(listOf(listOf("you")))
            while (q.isNotEmpty()) {
                val path = q.removeFirst()
                map[path.last()]!!.forEach { pos ->
                    when (pos) {
                        "out" -> {
                            paths++
                        }

                        !in path -> q += (path + pos)
                        else -> {}
                    }
                }
            }
            paths
        }

        part2 {
            data class State(val pos: String, val dac: Boolean = false, val fft: Boolean = false)

            val map = buildMap {
                lines.forEach {
                    val (frm, to) = it.split(" ").let { it.first().dropLast(1) to it.drop(1) }
                    to.forEach { getOrPut(frm) { mutableListOf<String>() }.add(it) }
                }
            }

            val cache = mutableMapOf<State,Long>()
            fun count(state: State): Long {
                if(state in cache) return cache[state]!!
                val (pos, dac, fft) = state
                return map[pos]!!.sumOf { next ->
                    when (next) {
                        "out" -> if (dac && fft) 1L else 0
                        "dac" -> count(State(next, true, fft))
                        "fft" -> count(State(next, dac, true))
                        else -> count(State(next, dac, fft))
                    }
                }.also { cache[state] = it }
            }

            count(State("svr"))

        }
    }
}
