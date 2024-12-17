package year2024

import util.*

fun main() {
    Day17.solveAll()
}

object Day17 : Solutions {
    val recursion = puzzle {
        fun machine(a: Long, ops: List<Pair<Int, Int>>): List<Int> {
            var a = a
            var b = 0L
            var c = 0L
            var ptr = 0
            fun combo(opa: Int): Long = when (opa) {
                in 0..3 -> opa.toLong()
                4 -> a
                5 -> b
                6 -> c
                else -> error("bad opa")
            }

            fun MutableList<Int>.step(op: Int, opa: Int) {
                when (op) {
                    0 -> a = (a shr combo(opa).toInt())
                    1 -> b = b xor opa.toLong()
                    2 -> b = combo(opa) % 8
                    3 -> if (a != 0L) {
                        ptr = opa; return
                    }

                    4 -> b = b xor c
                    5 -> add((combo(opa) % 8).toInt())
                    6 -> b = (a shr combo(opa).toInt())
                    7 -> c = (a shr combo(opa).toInt())
                }
                ptr += 1
            }

            return buildList {
                while (ptr < ops.size) {
                    val (op, opa) = ops[ptr]
                    step(op, opa)
                }
            }
        }

        part2 {
            val (_, prog) = input.split("\n\n").map { it.getIntList() }
            val ops = prog.chunked(2).map { (a, b) -> a to b }

            fun find(targets: List<Int>): Long {
                var a = if (targets.size == 1) 0 else (find(targets.drop(1)) shl 3)
                while (machine(a, ops) != targets) a++
                return a
            }
//            fun find(targets: List<Int>): Long {
//                if (targets.isEmpty()) return 0
//                for (a in count(find(targets.subList(1, targets.size)) shl 3)) {
//                    if (machine(a, ops) == targets)
//                        return a
//                }
//                error("can't get here")
//            }

            find(prog)
        }
    }
    val sequence = puzzle {
        fun machine(a: Long, b: Long, c: Long, ops: List<Pair<Long, Int>>): Sequence<Long> {
            var a = a
            var b = b
            var c = c
            var ptr = 0
            fun combo(opa: Int): Long = when (opa) {
                in 0..3 -> opa.toLong()
                4 -> a
                5 -> b
                6 -> c
                else -> error("bad opa")
            }

            suspend fun SequenceScope<Long>.run(op: Long, opa: Int) {
                when (op) {
                    0L -> a = (a shr combo(opa).toInt())
                    1L -> b = b xor opa.toLong()
                    2L -> b = combo(opa) % 8
                    3L -> if (a != 0L) ptr = opa - 1
                    4L -> b = b xor c
                    5L -> yield(combo(opa) % 8)
                    6L -> b = (a shr combo(opa).toInt())
                    7L -> c = (a shr combo(opa).toInt())
                }
                ptr += 1
            }

            return sequence {
                while (ptr < ops.size) {
                    val (op, opa) = ops[ptr]
                    run(op, opa)
                    listOf(a, b, c)
                }
            }
        }

        part1 {
            val (regs, prog) = input.split("\n\n").map { it.getLongList() }
            val (a, b, c) = regs
            machine(a, b, c, prog.chunked(2).map { (a, b) -> a to b.toInt() }).joinToString(",")
        }

        part2 {
            val (regs, prog) = input.split("\n\n").map { it.getLongList() }
            val (_, b, c) = regs
            val ops = prog.chunked(2).map { (a, b) -> a to b.toInt() }

            prog.asReversed().fold(emptyList<Long>() to 0L) { (targets, base), t ->
                var a = base shl 3
                val check = listOf(t) + targets
                while (machine(a, b, c, ops).toList() != check) a++
                check to a
            }
        }
    }

    val list = puzzle {
        fun machine(a: Long, b: Long, c: Long, ops: List<Pair<Long, Int>>): List<Long> {
            var a = a
            var b = b
            var c = c
            var ptr = 0
            fun combo(opa: Int): Long = when (opa) {
                in 0..3 -> opa.toLong()
                4 -> a
                5 -> b
                6 -> c
                else -> error("bad opa")
            }

            fun MutableList<Long>.run(op: Long, opa: Int) {
                when (op) {
                    0L -> a = (a shr combo(opa).toInt())
                    1L -> b = b xor opa.toLong()
                    2L -> b = combo(opa) % 8
                    3L -> if (a != 0L) ptr = opa - 1
                    4L -> b = b xor c
                    5L -> add(combo(opa) % 8)
                    6L -> b = (a shr combo(opa).toInt())
                    7L -> c = (a shr combo(opa).toInt())
                }
                ptr += 1
            }

            return buildList {
                while (ptr < ops.size) {
                    val (op, opa) = ops[ptr]
                    run(op, opa)
                    listOf(a, b, c)
                }
            }
        }

        part1 {
            val (regs, prog) = input.split("\n\n").map { it.getLongList() }
            val (a, b, c) = regs
            machine(a, b, c, prog.chunked(2).map { (a, b) -> a to b.toInt() }).joinToString(",")
        }

        part2 {
            val (regs, prog) = input.split("\n\n").map { it.getLongList() }
            val (_, b, c) = regs
            val ops = prog.chunked(2).map { (a, b) -> a to b.toInt() }

            prog.asReversed().fold(emptyList<Long>() to 0L) { (targets, base), t ->
                var a = base shl 3
                val check = listOf(t) + targets
                while (machine(a, b, c, ops).toList() != check) a++
                check to a
            }
        }
    }
}


