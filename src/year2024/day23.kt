package year2024

import util.*

fun main() {
    Day23.solveAll(
        InputProvider.raw(
            """
        kh-tc
        qp-kh
        de-cg
        ka-co
        yn-aq
        qp-ub
        cg-tb
        vc-aq
        tb-ka
        wh-tc
        yn-cg
        kh-ub
        ta-co
        de-co
        tc-td
        tb-wq
        wh-td
        ta-ka
        td-qp
        aq-cg
        wq-ub
        ub-vc
        de-ta
        wq-aq
        wq-vc
        wh-yn
        ka-de
        kh-ta
        co-tc
        wh-qp
        tb-vc
        td-yn
    """.trimIndent()
        )
    )

    Day23.solveAll()
}


object Day23 : Solutions {

    val solution = puzzle {
        part1 {
            val map = lines.flatMap { it.split("-").let{(a, b) -> listOf(a to b, b to a) } }.groupBy { it.first }.mapValues { it.value.map { it.second } }

            map.keys.map {
                val set = map.getValue(it).toSet()
                    set.fold(set+it) {acc, next -> acc intersect (map.getValue(next).toSet() + next)} }
                .filter { it.size == 3 }.debug()
                .filter{ it.any { it.contains('t') }}
                .count()
        }
    }
}