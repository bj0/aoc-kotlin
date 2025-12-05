package year2024

import arrow.fx.coroutines.parMap
import kotlinx.coroutines.Dispatchers
import util.*

fun main() {
    Day22.solveAll(
//        InputProvider.raw(
//            """
//1
//10
//100
//2024
//    """.trimIndent()
//        )
        InputProvider.raw(
            """
            1
            2
            3
            2024
        """.trimIndent()
        )
    )

    Day22.solveAll()
}


object Day22 : Solutions {

    inline fun Long.step(calc: (Long) -> Long) = calc(this).mix(this).prune()
    fun Long.evolve() = step { it * 64 }.step { it / 32 }.step { it * 2048 }
    fun Long.mix(secret: Long) = this xor secret
    fun Long.prune() = mod(16777216L)


    // also 20x faster
    val cleaner = solution {
        // needs to be local for function reference?
        fun Long.evolve() = step { it * 64 }.step { it / 32 }.step { it * 2048 }
        fun String.genSecrets() = generateSequence(toLong(), Long::evolve).take(2001)

        part1 {
            lines.sumOf { it.genSecrets().last() }
        }
        part2 {
            lines.flatMap { it ->
                it.genSecrets().map { it.mod(10) }
                    .zipWithNext { a, b -> b to b - a }
                    .windowed(4) { it.map { it.second } to it.last().first }
                    .distinctBy { (seq, _) -> seq }
            }.groupingBy { (seq, _) -> seq }
                .fold(0L) { acc, (_, value) -> acc + value }.maxOf { it.value }
        }
    }


    val solution = solution {
        part1 {
            lines.parMap(Dispatchers.Default) {
                var n = it.toLong()
                repeat(2000) {
                    n = n.evolve()
                }
                n
            }.toList().sum()
        }

        part2 {
            val buyers =
                lines.map { generateSequence(it.toLong()) { it.evolve() }.take(2000).map { it.mod(10L) }.toList() }
            val diffs = buyers.map { it.zipWithNext { a, b -> b - a }.toList() }
            val c = buyers.zip(diffs)

            val maps = c.map { (b, d) ->
                buildMap {
                    for ((i, s) in d.windowed(4).withIndex()) {
                        if (s !in this)
                            put(s, b[(i + 4)])
                    }
                }
            }

            maps.flatMap { it.keys }.toSet()
                .maxOf { s -> maps.sumOf { it[s] ?: 0 } }
        }
    }
}
