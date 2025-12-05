package year2024

import util.*

fun main() {
//    Day24.solveAll(
//        InputProvider.raw(
//            """
//                x00: 0
//                x01: 1
//                x02: 0
//                x03: 1
//                x04: 0
//                x05: 1
//                y00: 0
//                y01: 0
//                y02: 1
//                y03: 1
//                y04: 0
//                y05: 1
//
//                x00 AND y00 -> z05
//                x01 AND y01 -> z02
//                x02 AND y02 -> z01
//                x03 AND y03 -> z03
//                x04 AND y04 -> z04
//                x05 AND y05 -> z00
//            """.trimIndent()
////            """
////        x00: 1
////        x01: 0
////        x02: 1
////        x03: 1
////        x04: 0
////        y00: 1
////        y01: 1
////        y02: 1
////        y03: 1
////        y04: 1
////
////        ntg XOR fgs -> mjb
////        y02 OR x01 -> tnw
////        kwq OR kpj -> z05
////        x00 OR x03 -> fst
////        tgd XOR rvg -> z01
////        vdt OR tnw -> bfw
////        bfw AND frj -> z10
////        ffh OR nrd -> bqk
////        y00 AND y03 -> djm
////        y03 OR y00 -> psh
////        bqk OR frj -> z08
////        tnw OR fst -> frj
////        gnj AND tgd -> z11
////        bfw XOR mjb -> z00
////        x03 OR x00 -> vdt
////        gnj AND wpb -> z02
////        x04 AND y00 -> kjc
////        djm OR pbm -> qhw
////        nrd AND vdt -> hwm
////        kjc AND fst -> rvg
////        y04 OR y02 -> fgs
////        y01 AND x02 -> pbm
////        ntg OR kjc -> kwq
////        psh XOR fgs -> tgd
////        qhw XOR tgd -> z09
////        pbm OR djm -> kpj
////        x03 XOR y03 -> ffh
////        x00 XOR y04 -> ntg
////        bfw OR bqk -> z06
////        nrd XOR fgs -> wpb
////        frj XOR qhw -> z04
////        bqk OR frj -> z07
////        y03 OR x01 -> nrd
////        hwm AND bqk -> z03
////        tgd XOR rvg -> z12
////        tnw OR pbm -> gnj
////    """.trimIndent()
//        )
//    )

    Day24.solveAll()
}


object Day24 : Solutions {

    data class Gate(val a: String, val b: String, val op: String, val out: String)

    fun do_op(a: Int, b: Int, op: String): Int = when (op) {
        "OR" -> a or b
        "AND" -> a and b
        "XOR" -> a xor b
        else -> error("bad op $op")
    }

    val solution = solution {
        part1 {
            val (i0, g0) = input.split("\n\n")
            val io = i0.lines().map { it.split(": ").let { (a, b) -> a to b.toInt() } }.toMap().toMutableMap()
            val outs = mutableSetOf<String>()
            val g = g0.lines().map { line ->
                val (a, op, b, out) = line.split(" -> ", " ")
                if (out.startsWith("z")) outs.add(out)
                Gate(a, b, op, out)
            }
//            io.debug()
//            g.debug()
//            val wires = io.keys + g.map { (a, b, _, out) -> setOf(a,b,out) }
            val on = io.keys.toMutableSet()
            while (outs.any { it !in on }) {
//                println("outs:$outs")
//                println("ons:$on")
//                println(" chk: ${outs-on}")

                val x = g.filter { it.a in on && it.b in on && it.out !in on }
                if (x.isEmpty()) {
                    println("wtf: $on\n$outs\n${io.keys == on}\n${outs - on}")
                    break
                }

                x.forEach { (a, b, op, out) ->
                    val ret = do_op(io.getValue(a), io.getValue(b), op)
                    io[out] = ret
                    on += out
//                        println("adding $out=$ret")
                }
            }

            outs.sortedDescending().map { io.getValue(it) }.joinToString("") { it.toString() }.toLong(2)

        }

        part2 {
            val (i0, g0) = input.split("\n\n")
            val io = i0.lines().map { it.split(": ").let { (a, b) -> a to b.toInt() } }.toMap().toMutableMap()
            val outs = mutableSetOf<String>()
            val g = g0.lines().map { line ->
                val (a, op, b, out) = line.split(" -> ", " ")
                if (out.startsWith("z")) outs.add(out)
                Gate(a, b, op, out)
            }
            val gr = g.associateBy { it.out }.toMutableMap()
            val gf = g.flatMap { (a, b, _, out) -> listOf(a to out, b to out) }.groupBy({ it.first }) { it.second }

            fun findUpstream(wire: String): Set<String> = buildSet {
                val q = mutableListOf(gr.getValue(wire))
                while (q.isNotEmpty()) {
                    val (a, b, _, _) = q.removeFirst()
                    if (a !in this) {
                        add(a)
                        gr[a]?.let { q += it }
                    }
                    if (b !in this) {
                        add(b)
                        gr[b]?.let { q += it }
                    }
                }
            }

            fun findDownstream(wire: String): Set<String> = buildSet {
                val q = mutableListOf(gf.getValue(wire))
                while (q.isNotEmpty()) {
                    val wires = q.removeFirst()
                    q += wires.filter { it !in this }.flatMap {
                        add(it)
                        gf[it].orEmpty()
                    }
                }
            }

            fun cx(gr: Map<String, Gate>, io: MutableMap<String, Int>, out: String, value: Int): Boolean {
                fun inner(out: String): Int = io.getOrPut(out) {
                    val (a, b, op, _) = gr.getValue(out)
                    return@getOrPut do_op(inner(a), inner(b), op)
                }

                return inner(out) == value
            }

            val N = 45
            fun Long.proc(pfx: String): Map<String, Int> = this.toString(2).padStart(N, '0')
                .toCharArray().reversed().mapIndexed { i, v -> pfx + "$i".padStart(2, '0') to v.digitToInt() }.toMap()

            data class Add(val io: Map<String, Int>, val outs: Map<String, Int>)

            fun makeIo(x: Long, y: Long): Add {
                val z = x + y
                return Add(x.proc("x") + y.proc("y"), z.proc("z"))
            }

            val digs = listOf('1', '0')
            val a = buildString { repeat(45) { append(digs.random()) } }.toLong(2)
            val b = buildString { repeat(45) { append(digs.random()) } }.toLong(2)

            //"".padStart(45,'1').toLong(2), "".padStart(45,'0').toLong(2)
            val add = makeIo(a, b)//.debug()

            // swap
            fun swap(a: String, b: String) {
                val sa = gr.getValue(a)
                val sb = gr.getValue(b)
                gr[a] = sb.copy(out = a)
                gr[b] = sa.copy(out = b)
            }
//            swap("dcm","hbq")
            swap("qnw","qff")
            swap("z16","pbv")
            swap("z36","fbq")
            swap("qqp","z23")
//            swap()



            val lio = add.io.toMutableMap()
            val bad = add.outs.filter { (t, u) ->
                !cx(gr, lio, t, u)//.debug("chK:$t=$u:")
            }.debug("bad:")

//            val pool = gr.keys
//
//            fun pairsFrom(pool:Set<String>) = sequence {
//                for (g0 in pool) {
//                    val off = findUpstream(g0) + findDownstream(g0)
//                    for (g1 in (pool - off)) {
//                        yield(g0 to g1)
//                    }
//                }
//            }

            fun Gate.print(pfx: String) =
                println("$pfx wire $out <- ${this.a} $op ${this.b} (${lio[this.a]} $op ${lio[this.b]})")


            fun pgate(wire: String, pfx: String = "", depth: Int = 5) {
                if (depth == 0) return
                gr[wire]?.let {
                    it.print(pfx)
                    pgate(it.a, "$pfx   ", depth - 1)
                    pgate(it.b, "$pfx   ", depth - 1)
                    println("")
                }
            }

            fun p2gate(wire: String) {
                val root = gr.getValue(wire)
                root.print("")
                val a = gr.getValue(root.a)
                a.print("   ")
                val b = gr.getValue(root.b)
                b.print("   ")

                val aa = gr[a.a]?.print("     ")
                gr[a.b]?.print("     ")

                gr[b.a]?.print("     ")
                gr[b.b]?.print("     ")

            }

            println("")
//            pgate("z10")
//            pgate("z11")
//            pgate("z12")
//            pgate("z13")
//            pgate("z14")

            (22..24).forEach{i ->
                pgate("z$i")
            }


            println("")

//            bad.entries.first().let { (wire, v) ->
//                val (a, b, op, _) = gr.getValue(wire)
//                println("wire $wire ($v) <- $a $op $b ${lio[a]},${lio[b]}")
//            }

            // z36, z45, z23, z16
            //bad:{z11=1, z12=1, z13=1, z14=1, z15=1, z16=1, z17=1, z18=1, z19=1, z20=1, z21=1, z22=1, z36=1, z37=1, z38=1, z39=1, z40=1, z41=1, z42=1, z43=1, z44=1}
            //[Gate(a=jdd, b=rbm, op=AND, out=z36), Gate(a=mkv, b=hgp, op=OR, out=z45), Gate(a=wdr, b=jcd, op=OR, out=z23), Gate(a=x16, b=y16, op=AND, out=z16)]
            //bad:{z11=1, z12=1, z13=0, z16=1, z17=1, z18=0, z23=1, z24=1, z25=1, z26=1, z27=1, z28=0, z36=1, z37=0}
            gr.values.filter { it.op != "XOR" && it.out.startsWith("z") }.debug()

            listOf("qnw","qff",
            "z16","pbv",
            "z36","fbq",
            "qqp","z23").sorted().joinToString(",")
        }
    }
}