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

    // this one takes about 30s
//    val sets = puzzle {
//        part2 {
//            val map = lines.flatMap { it.split("-").let { (a, b) -> listOf(a to b, b to a) } }.groupBy { it.first }
//                .mapValues { it.value.map { it.second }.toSet() + it.key }
//
//            fun Set<String>.fullyConnected() = map { map.getValue(it) }.reduce(Set<String>::intersect)
//            fun Set<Set<String>>.next(): Set<Set<String>> {
//                val n = mutableSetOf<Set<String>>()
//                for (s in this) {
//                    for (candidate in s.fullyConnected()) {
//                        n.add(s + candidate)
//                    }
//                }
//                return n
//            }
//
//            generateSequence(map.keys.map { setOf(it) }.toSet()) {
//                val n = it.next()
//                if (n != it) n else null
//            }.last().maxBy { it.size }.sorted().joinToString(",")
//        }
//    }

    // this one is very slow
//    val cleaner = puzzle {
//        part2 {
//            val map = lines.flatMap { it.split("-").let { (a, b) -> listOf(a to b, b to a) } }.groupBy { it.first }
//                .mapValues { it.value.map { it.second }.toSet() + it.key }
//
//            val cache = mutableMapOf<Set<String>, Set<String>>()
//            fun findLargestSubnet(group: Set<String>): Set<String> = cache.getOrPut(group) {
//                val trouble = group.filter { cpu -> (group - cpu).any { cpu !in map.getValue(it) } }
//                if (trouble.isEmpty()) return@getOrPut group
//                return@getOrPut trouble.map { findLargestSubnet(group - it) }.maxBy { it.size }
//            }
//
//            val seen = mutableSetOf<Set<String>>()
//            map.keys.mapNotNull { k ->
//                if (seen.none { k in it })
//                    findLargestSubnet(map.getValue(k))
//                else null
//            }.maxBy { it.size }.sorted().joinToString(",")
//        }
//    }

    val solution = solution {
        part1 {
            val map = lines.flatMap { it.split("-").let { (a, b) -> listOf(a to b, b to a) } }.groupBy { it.first }
                .mapValues { it.value.map { it.second }.toSet() }

            map.keys.flatMap { a ->
                val set = map.getValue(a)
                set.map { b -> b to map.getValue(b).filter { c -> a in map.getValue(c) } }
                    .filter { it.second.isNotEmpty() }
                    .flatMap { (b, cl) -> cl.map { c -> setOf(a, b, c) } }
            }.distinct().count { it.any { it.startsWith('t') } }
        }

        part2 {
//            val lines = """
//                aa-ab
//                aa-ba
//                ba-ca
//                ca-aa
//            """.trimIndent().lines()
//            val lines = """
//                aa-ab
//                aa-ac
//                aa-ba
//                aa-cb
//                ab-ac
//                ba-ca
//                cb-ca
//                cb-ba
//                ca-aa
//            """.trimIndent().lines()//.solve().debug("fix2?")
            val map = lines.asSequence().flatMap { it.split("-").let { (a, b) -> listOf(a to b, b to a) } }
                .groupBy { it.first }
                .mapValues { it.value.map { it.second }.toSet() }//.debug()

            fun findNetworks(next: String): List<Set<String>> {
                val seen = mutableSetOf<Pair<String, Set<String>>>()
                fun inner(network: Set<String>, acc: Set<String>, cpu: String): List<Set<String>> {
                    if (cpu to acc in seen) return emptyList()
                    seen += cpu to acc
                    val acc2 = when {
                        acc.isEmpty() -> map.getValue(cpu) + cpu
                        else -> map.getValue(cpu) + cpu intersect acc
                    }
                    val grp = acc2 - network
                    return when (grp.size) {
                        1 -> listOf(network + cpu)
                        else -> grp.flatMap { inner(network + cpu, acc2, it) }
                    }
                }
                return inner(emptySet(), emptySet(), next)
            }

            // not sure if this cachign assumption is valid, but it works
            val seen = mutableSetOf<Set<String>>()
            map.keys.mapNotNull { k ->
                if (seen.none { k in it }) {
                    findNetworks(k).also { seen.addAll(it) }
                } else null
            }.flatten().distinct().maxBy { it.size }.sorted().joinToString(",")
        }
    }

    val eph = solution {
        part2 {
            // use proper ordering to avoid loops (caching is still faster)
            val map =
                buildMap<String, MutableSet<String>> {
                    for (line in input.lineSequence()) {
                        if ('-' !in line) continue
                        val (a, b) = line.split('-', limit = 2)
                        getOrPut(minOf(a, b), ::mutableSetOf).add(maxOf(a, b))
                        getOrPut(maxOf(a, b), ::mutableSetOf)
                    }
                }

            fun walk(used: Set<String>, left: Set<String>): Set<String> =
                left.maxOfWithOrNull(compareBy(Set<String>::size)) {
                    walk(used + it, left intersect map.getValue(it))
                } ?: used



            walk(emptySet(), map.keys).joinToString(",")
        }
    }

    // misses an edge case
    val aok = solution {
        part2 {
            fun String.network() = buildMap {
                fun add(k: String, v: String) =
                    compute(k) { _, s: MutableList<String>? -> s?.also { it += v } ?: mutableListOf(v) }

                for (line in lines()) {
                    val (a, b) = line.split("-")
                    if (a < b) add(a, b) else add(b, a)
                }
            }.also { it.values.forEach { it.sort() } }

            fun String.solve() = network().let { network ->
                network.map { (first, connected) ->
                    buildSet {
                        add(first)
                        for (next in connected) {
                            if (all { next in network[it].orEmpty() }) add(next)
                        }
                    }
                }.maxBy { it.size }.joinToString(",")
            }
//            """
//                aa-ab
//                aa-ba
//                ba-ca
//                ca-aa
//            """.trimIndent().solve().debug()

            input.solve()
        }
    }

    val aok2 = solution {
        part2 {
            fun String.network() = buildMap {
                fun add(k: String, v: String) =
                    compute(k) { _, s: MutableList<String>? -> s?.also { it += v } ?: mutableListOf(v) }

                for (line in lines()) {
                    val (a, b) = line.split("-")
                    if (a < b) add(a, b) else add(b, a)
                }
            }.also { it.values.forEach { it.sort() } }

            fun String.solve() = network().let { network ->
                network.map { (first, connected) ->
                    buildSet {
                        val visited = mutableSetOf<String>()
                        for (second in connected) {
                            add((buildSet {
                                add(first)
                                add(second)
                                for (next in connected - visited) {
                                    if (all { next in network[it].orEmpty() }) add(next)
                                }
                            }).also { visited += it })
                        }
                    }
                }.flatten().maxBy { it.size }.joinToString(",")
            }
//            """
//                aa-ab
//                aa-ba
//                ba-ca
//                ca-aa
//            """.trimIndent().solve().debug("fix?")
//            """
//                aa-ab
//                aa-ac
//                aa-ba
//                aa-cb
//                ab-ac
//                ba-ca
//                cb-ca
//                cb-ba
//                ca-aa
//            """.trimIndent().solve().debug("fix2?")

            input.solve()
        }
    }
}