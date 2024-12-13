package year2024

import util.*

fun main() {
    Day13.solveAll(
        InputProvider.raw(
            """
        Button A: X+94, Y+34
        Button B: X+22, Y+67
        Prize: X=8400, Y=5400

        Button A: X+26, Y+66
        Button B: X+67, Y+21
        Prize: X=12748, Y=12176

        Button A: X+17, Y+86
        Button B: X+84, Y+37
        Prize: X=7870, Y=6450

        Button A: X+69, Y+23
        Button B: X+27, Y+71
        Prize: X=18641, Y=10279
    """.trimIndent()
        )
    )
    Day13.solveAll()
}

object Day13 : Solutions {

    data class Machine(val a: LongPoint, val b: LongPoint, val prize: LongPoint)

    private fun solve(machine: Machine) = with(machine) {
        val aPushes = ((prize.x * b.y - b.x * prize.y) / (a.x * b.y - a.y * b.x).toDouble()).toLong()
        // this didn't work, but it should be the same
//        val A = ((prize.x - b.x * prize.y / b.y.toDouble()) / (a.x - a.y * b.x / b.y.toDouble())).toLong()

        val bPushes = ((prize.y - aPushes * a.y) / b.y.toDouble()).toLong()
        if (aPushes > 0 && bPushes > 0 && (aPushes * a + bPushes * b == prize)) aPushes * 3 + bPushes * 1 else 0
    }

    val solution = puzzle {
        part1 {
            val machines = input.split("\n\n")
                .map { m ->
                    val (a, b, prize) = m.lines().map { it.getLongList() }.map { LongPoint(it.first(), it.last()) }
                    Machine(a, b, prize)
                }

            machines.sumOf { solve(it) }
        }
        part2 {
            val machines = input.split("\n\n")
                .map { m ->
                    val (a, b, prize) = m.lines().map { it.getLongList() }.map { LongPoint(it.first(), it.last()) }
                    Machine(a, b, prize + 10_000_000_000_000)
                }

            machines.sumOf { solve(it) }
        }
    }
}