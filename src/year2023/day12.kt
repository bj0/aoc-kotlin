package year2023

import arrow.core.MemoizedDeepRecursiveFunction
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import util.InputProvider
import util.PuzDSL
import util.solveAll

fun main() {
//    "#.#....###".split('.').filter { it.isNotEmpty() }.debug()
    listOf(Day12, Day12DRF).solveAll(
//        InputProvider.Example
//        InputProvider.raw("""#????????.#?#?????? 2,1,1,5,1""")
    )
    Day12MDRF.solveAll(
//        InputProvider.Example
//        InputProvider.raw("""#????????.#?#?????? 2,1,1,5,1""")
    )

}

object Day12 : PuzDSL({

    fun countPossible(cond: String, ecc: List<Int>): Long {
        data class Input(val cond: String, val ecc: List<Int>)

        buildMap {
            fun innie(cond: String, ecc: List<Int>): Long = getOrPut(Input(cond, ecc)) {
                val m = ecc.sum()
                when {
                    m + ecc.size - 1 > cond.length || m < cond.count { it == '#' } || m > cond.count { it != '.' } -> 0

                    ecc.isEmpty() -> if (cond.any { it == '#' }) 0 else 1

                    else -> {
                        val n = ecc.first()
                        if (cond.take(n).any { it == '.' } || cond.getOrNull(n) == '#') 0 else {
                            innie(cond.drop(n + 1).trimStart('.'), ecc.drop(1))
                        } + if (cond.startsWith('#')) 0 else {
                            innie(cond.drop(1).trimStart('.'), ecc)
                        }
                    }
                }
            }
            return innie(cond, ecc)
        }
    }

    fun countPossible(vararg input: String): Long {
        return countPossible(input[0].trim('.'), input[1].split(",").map { it.toInt() })
    }

    part1 {
        lines.sumOf { line ->
            countPossible(*line.split(' ').toTypedArray())
        }
    }

    part2 {
        lines.sumOf { line ->
            val (cond, ecc) = line.split(' ')
            val unfoldedCond = buildList { repeat(5) { add(cond) } }.joinToString("?")
            val unfoldecEcc = buildList { repeat(5) { add(ecc) } }.joinToString(",")
            countPossible(unfoldedCond, unfoldecEcc)
        }
    }
})

object Day12DRF : PuzDSL({

    data class Input(val cond: String, val ecc: List<Int>)

    val cache = mutableMapOf<Input, Long>()
    val countPossible = DeepRecursiveFunction<Input, Long> { input ->
        cache.getOrPut(input) {
//    val countPossible = MemoizedDeepRecursiveFunction<Input, Long> { input ->
            val (cond, ecc) = input
            val m = ecc.sum()
            when {
                m + ecc.size - 1 > cond.length || m < cond.count { it == '#' } || m > cond.count { it != '.' } -> 0

                ecc.isEmpty() -> if (cond.any { it == '#' }) 0 else 1

                else -> {
                    val n = ecc.first()
                    if (cond.take(n).any { it == '.' } || cond.getOrNull(n) == '#') 0 else {
                        callRecursive(Input(cond.drop(n + 1).trimStart('.'), ecc.drop(1)))
                    } + if (cond.startsWith('#')) 0 else {
                        callRecursive(Input(cond.drop(1).trimStart('.'), ecc))
                    }
                }
            }
        }
    }

    fun countPossible(vararg input: String): Long {
        return countPossible(Input(input[0].trim('.'), input[1].split(",").map { it.toInt() }))
    }

    part1 {
        lines.sumOf { line ->
            countPossible(*line.split(' ').toTypedArray())
        }
    }

    part2 {
        lines.sumOf { line ->
            val (cond, ecc) = line.split(' ')
            val unfoldedCond = buildList { repeat(5) { add(cond) } }.joinToString("?")
            val unfoldecEcc = buildList { repeat(5) { add(ecc) } }.joinToString(",")
            countPossible(unfoldedCond, unfoldecEcc)
        }
    }
})

object Day12MDRF : PuzDSL({

    data class Input(val cond: String, val ecc: List<Int>)

    val countPossible = MemoizedDeepRecursiveFunction<Input, Long> { input ->
        val (cond, ecc) = input
        val m = ecc.sum()
        when {
            m + ecc.size - 1 > cond.length || m < cond.count { it == '#' } || m > cond.count { it != '.' } -> 0

            ecc.isEmpty() -> if (cond.any { it == '#' }) 0 else 1

            else -> {
                val n = ecc.first()
                if (cond.take(n).any { it == '.' } || cond.getOrNull(n) == '#') 0 else {
                    callRecursive(Input(cond.drop(n + 1).trimStart('.'), ecc.drop(1)))
                } + if (cond.startsWith('#')) 0 else {
                    callRecursive(Input(cond.drop(1).trimStart('.'), ecc))
                }
            }
        }
    }

    fun countPossible(vararg input: String): Long {
        return countPossible(Input(input[0].trim('.'), input[1].split(",").map { it.toInt() }))
    }

    part1 {
        lines.sumOf { line ->
            countPossible(*line.split(' ').toTypedArray())
        }
    }

    part2 {
        lines.sumOf { line ->
            val (cond, ecc) = line.split(' ')
            val unfoldedCond = buildList { repeat(5) { add(cond) } }.joinToString("?")
            val unfoldecEcc = buildList { repeat(5) { add(ecc) } }.joinToString(",")
            countPossible(unfoldedCond, unfoldecEcc)
        }
    }
})

object Day12TooSlow : PuzDSL({

    fun String.inc(i: Int): String? = when {
        (i >= length) -> null
        (this[i] == '#') -> {
            buildString {
                append(this@inc.take(i)).append('.').append(this@inc.drop(i + 1))
            }.inc(i + 1)
        }

        else -> {
            buildString {
                append(this@inc.take(i)).append('#').append(this@inc.drop(i + 1))
            }
        }
    }

    fun valid(cond: String, ecc: List<Int>): Boolean {
        val conds = cond.trim('.').split('.').filter { it.isNotEmpty() }
        return (conds.size == ecc.size) && (conds zip ecc).all { (c, e) -> c.length == e }


    }

    fun check(cond: String, ecc: String): Sequence<String> {
        val cond = cond.trim('.')
        val ecc = ecc.split(',').map { it.toInt() }
        val qs = cond.indices.filter { cond[it] == '?' }
        return sequence {
            var cur: String? = qs.joinToString("") { "." }
            while (cur != null) {
                yield(cur!!)
                cur = cur?.inc(0)
            }
        }.mapNotNull { trial ->
            buildString {
                append(cond)
                (trial.toList() zip qs).forEach { (c, i) ->
                    setCharAt(i, c)
                }
            }.let { s -> if (valid(s, ecc)) s else null }
        }
    }

    part1 {
        lines.sumOf { line ->
            val (cond, ecc) = line.split(' ')
            check(cond, ecc).count()
        }
    }

    part2 {
        runBlocking {

            val n = lines.count() / 4
            lines.chunked(6).map { lines2 ->
                async {
                    lines2.sumOf { line ->
                        val (cond, ecc) = line.split(' ')
                        val cond2 = buildList { repeat(5) { add(cond) } }.joinToString("?")
                        val ecc2 = buildList { repeat(5) { add(ecc) } }.joinToString(",")
                        check(cond2, ecc2).count()
                    }
                }
            }.awaitAll().sum()
        }
    }

})
