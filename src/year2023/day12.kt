package year2023

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import util.InputProvider
import util.PuzDSL
import util.debug
import util.solveAll

fun main() {
//    "#.#....###".split('.').filter { it.isNotEmpty() }.debug()
    listOf(Day12).solveAll(
//        InputProvider.Example
//        InputProvider.raw("""#????????.#?#?????? 2,1,1,5,1""")
    )

}

object Day12 : PuzDSL({

    data class Input(val cond:String, val ecc:List<Int>)
    val cache = mutableMapOf<Input, Long>()
    fun count(cond: String, ecc: List<Int>): Long = cache.getOrPut(Input(cond, ecc)) {
        if (ecc.isEmpty()) return@getOrPut if(cond.any { it == '#' }) 0L else 1L
        if (ecc.sum() + ecc.size - 1 > cond.length) return@getOrPut 0L
        val n = ecc.first()
        if (cond.first() == '#') {
            if (cond.take(n).any { it == '.' } || cond.drop(n).firstOrNull() == '#') return 0L
            count(cond.drop(n + 1).trimStart('.'), ecc.drop(1))
        }else {
            // must be, ?, try #
            val good = run {
                if (cond.take(n).any { it == '.' } || cond.drop(n).firstOrNull() == '#') 0L
                else count(cond.drop(n + 1).trimStart('.'), ecc.drop(1))
            }
            val bad = run {
                val cond2 = cond.drop(1).trimStart('.')
                count(cond2, ecc)
            }
            good + bad
        }
    }

    fun check(cond: String, ecc: String): Long {
        return count(cond.trim('.'), ecc.split(",").map { it.toInt() })
    }

    part1 {
        lines.sumOf { line ->
            val (cond, ecc) = line.split(' ')
            check(cond, ecc)
        }
    }

    part2 {
        lines.sumOf { line ->
            val (cond, ecc) = line.split(' ')
            val cond2 = buildList { repeat(5) { add(cond) } }.joinToString("?")
            val ecc2 = buildList { repeat(5) { add(ecc) } }.joinToString(",")
            check(cond2, ecc2)
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
