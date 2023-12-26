package year2023

import util.puzzle
import util.solveAll

fun main() {
    listOf(Day25::solution).solveAll(
//        InputProvider.raw(
//            """
//            jqt: rhn xhk nvd
//            rsh: frs pzl lsr
//            xhk: hfx
//            cmg: qnr nvd lhk bvb
//            rhn: xhk bvb hfx
//            bvb: xhk hfx
//            pzl: lsr hfx nvd
//            qnr: nvd
//            ntq: jqt hfx bvb xhk
//            nvd: lhk
//            lsr: lhk
//            rzs: qnr cmg lsr rsh
//            frs: qnr lhk lsr""".trimIndent()
//        )
    )
}

object Day25 {

    data class Edge(val from: String, val to: String) {
        override fun toString(): String = "$from/$to"
    }

    operator fun Edge.contains(it: String) = it == from || it == to

    infix fun String.edge(other: String) = if (this < other) Edge(this, other) else Edge(other, this)

    val solution = puzzle {
        part1 {
            val edges = mutableListOf<Edge>()
            val wires = buildMap {
                for (line in lines) {
                    val (frm, to) = line.split(": ")
                    for (t in to.split(" ")) {
                        edges.add(frm edge t)
                        put(frm, (get(frm) ?: emptyList<String>()) + t)
                        put(t, (get(t) ?: emptyList<String>()) + frm)
                    }
                }
            }

            fun sizeWithout( edges: List<Edge>): Int {
                val seen = mutableSetOf<String>()
                val start = wires.keys.first()
                val q = ArrayDeque(listOf(start))
                while (q.isNotEmpty()) {
                    val here = q.removeFirst()
                    seen.add(here)
                    q += wires.getValue(here)
//                        .debug("wtf:${edges.toList()},$seen")
                        .filter {
                            (here edge it !in edges)
//                            .debug("inedge:")
                                    && (it !in seen)
//                            .debug("insen,$here,$seen:")
                        }
//                        .debug("good:")

                }
                return seen.size
            }

//            sizeWithout().debug("wat:")
//            wires.keys.size.debug("sz:")
//            var min = wires.keys.size
//            loop@ for ((i, e1) in edges.withIndex()) {
//                for ((j, e2) in edges.drop(i).withIndex()) {
//                    for (e3 in edges.drop(i + j)) {
//                        val q = sizeWithout(e1, e2, e3)//.debug("pewp:")
//                        if (q < min) {
//                            min = q
//                            break@loop
//                        }
//                    }
//                }
//            }

//            (wires.keys.size - min) * min

//            LabeledGraph { "abcde ace"}.show()

//            digraph {
//                wires.forEach { (t, u) -> u.forEach { t - it } }
//            }.dot().debug()

//            fun contract()

            fun flood(start: String, end: String): List<String> {
                val prev = mutableMapOf<String, String?>(start to null)
                val q = ArrayDeque(listOf(start))
                while (q.isNotEmpty()) {
                    val here = q.removeFirst()//.debug("wtf:")
                    if (here == end) return generateSequence(end) { prev[it] }.toList()
                    wires.getValue(here).filter { it !in prev }.forEach { next ->
                        prev[next] = here
                        q.add(next)
                    }
                }
                error("no path")
            }

            val counts = edges.associateWith { 0 }.toMutableMap()
            repeat(500) {
                val end = wires.keys.random()
                val start = (wires.keys - end).random()
                flood(start, end).zipWithNext { a, b -> counts[a edge b] = counts.getValue(a edge b) + 1 }
            }


            val remove = counts.toList().sortedBy { (_, v) -> v }.takeLast(3).map { it.first }

            val small = sizeWithout(remove)
            (wires.keys.size - small) * small
        }
    }
}

/**
 * todo: try JGraphT
 *
 *
 * val clusters = GirvanNewmanClustering(graph, 2).clustering.clusters
 * println(clusters[0].size * clusters[1].size)
 */
