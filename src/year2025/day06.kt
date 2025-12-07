package year2025

import util.InputProvider
import util.Solutions
import util.getLongList
import util.part1
import util.part2
import util.solution
import util.solveAll
import util.transpose

fun main() {
    Day6.solveAll(
        InputProvider.raw(
            """
                |123 328  51 64 
                | 45 64  387 23 
                |  6 98  215 314
                |*   +   *   +  
            """.trimMargin("|")
        )
    )
    Day6.solveAll()
}

object Day6 : Solutions {
    val first = solution {
        part1 {
            val nums = lines.dropLast(1).map { it.getLongList() }
            val ops = lines.last().split(" ").filter { it.isNotEmpty() }

            (0..<nums.first().size).sumOf { i ->
                val op = if (ops[i] == "*") { a: Long, b: Long -> a * b } else { a: Long, b: Long -> a + b }
//                ops[i].debug()
                nums.map { it[i] }.reduce { acc, lng -> op(acc, lng) }
            }
        }

        part2 {
            val nums = lines.dropLast(1)
            val ops = lines.last().split(" ").filter { it.isNotEmpty() }
            var opi = 0
            val x = mutableListOf<Long>()
            var tot = (0..<nums.first().length).sumOf { i ->
                val op = if (ops[opi] == "*") { a: Long, b: Long -> a * b } else { a: Long, b: Long -> a + b }
                val digs = nums.map { it[i] }.filter { it != ' ' }
                if (digs.isEmpty()) {
                    opi++
                    x.reduce { acc, lng -> op(acc, lng) }.also { x.clear() }
                } else {
                    x.add(digs.joinToString("").toLong())
                    0
                }
            }
            if (x.isNotEmpty()) {
                val op = if (ops[opi] == "*") { a: Long, b: Long -> a * b } else { a: Long, b: Long -> a + b }
                tot += x.reduce { acc, lng -> op(acc, lng) }.also { x.clear() }
            }
            tot
        }
    }

    val clean = solution {
        part2 {
            val transpose = lines.dropLast(1).transpose()
            val ops = lines.last().split(" ").filter { it.isNotEmpty() }
            val groups = transpose.fold(listOf(emptyList<Long>())) { acc, string ->
                if (string.isBlank()) {
                    acc.plusElement(emptyList())
                } else {
                    acc.dropLast(1).plusElement(acc.last() + string.trim().toLong())
                }
            }

            groups.zip(ops).sumOf { (group, op) ->
                if (op == "+") group.reduce { acc, lng -> acc + lng } else group.reduce { acc, lng -> acc * lng }
            }
        }
    }
}
