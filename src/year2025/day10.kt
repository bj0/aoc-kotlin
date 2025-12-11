package year2025

import com.microsoft.z3.*
import util.*

fun main() {
    Day10.solveAll(
        InputProvider.raw(
            """
                [.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
                [...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}
                [.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}
            """.trimIndent()
        )
    )
    Day10.solveAll()
}

object Day10 : Solutions {

    data class Machine(val target: String, val buttons: List<List<Int>>, val jolts: String)


    fun Char.flip() = if (this == '.') '#' else '.'
    val first = solution {

        part1 {
            data class State(val leds: String, val press: Long)

            val machines = lines.map { line ->
                val a = line.indexOf(' ')
                val b = line.indexOfLast { it == ' ' }
                Machine(
                    line.substring(0, a).trim(']', '['),
                    line.substring(a + 1, b).split(" ").map { it.getIntList() },
                    line.substring(b + 1, line.length)
                )
            }

            fun doMachine(machine: Machine): Long {
                val seen = mutableMapOf<String, Long>()
                val start = machine.target.map { '.' }.joinToString("")
                val q = ArrayDeque(listOf(State(start, 0)))

//                debug { "start: ${machine.target}" }
                while (q.isNotEmpty()) {
                    val (leds, p) = q.removeFirst()
                    if (leds == machine.target) return p
                    if (leds in seen && seen[leds]!! <= p) continue
                    seen[leds] = p
                    machine.buttons.map { switch ->
                        buildString {
                            leds.forEachIndexed { i, c ->
                                append(if (i in switch) c.flip() else c)

                            }
                        }//.debug()
                    }.forEach { q += State(it, p + 1) }
                }
                error("no sol")
            }

            machines.sumOf { doMachine(it) }

        }

        part2 {
//            data class State(val jolts: List<Long>, val press: Long)
//
//            val machines = lines.map { line ->
//                val a = line.indexOf(' ')
//                val b = line.indexOfLast { it == ' ' }
//                Machine(
//                    line.substring(0, a).trim(']', '['),
//                    line.substring(a + 1, b).split(" ").map { it.getIntList() },
//                    line.substring(b + 1, line.length)
//                )
//            }

//            fun doMachine(machine: Machine): Long {
//                val target = machine.jolts.getLongList()
//
//                val N = target.size
//                val A = mk.ndarray(machine.buttons.map { idx -> (0..<N).map { i -> if (i in idx) 1f else 0f } }).debug()
//                val b = mk.ndarray(machine.jolts.getLongList().map { it.toFloat() }).debug()
//                return mk.linalg.qr(A, b).debug().let { 0 }//.sum().roundToLong()
//            }


//            fun doMachine(machine: Machine): Long {
//                var seen = mutableMapOf<List<Long>, Long>()
//                val target = machine.jolts.getLongList()
//                val start = target.map { 0L }
//                var best = Long.MAX_VALUE
//                return DeepRecursiveFunction<State, Long> { state ->
//
////                debug { "start: ${machine.target}" }
//                    val (jolts, p) = state
//                    if (best <= p) return@DeepRecursiveFunction p
//                    if (jolts == target) {
//                        best = p.debug()
//                        seen = seen.filterValues { it < p }.toMutableMap()
//                        return@DeepRecursiveFunction p
//                    }
//                    if (jolts.withIndex().any { (i, j) -> j > target[i] }) return@DeepRecursiveFunction Long.MAX_VALUE
//                    if (jolts in seen && seen[jolts]!! <= p) return@DeepRecursiveFunction Long.MAX_VALUE
//                    seen[jolts] = p
//                    machine.buttons.map { inc ->
//                        buildList {
//                            jolts.forEachIndexed { i, c ->
//                                add(if (i in inc) c + 1 else c)
//
//                            }
//                        }//.debug()
//                    }.minOf { callRecursive(State(it, p + 1)) }
//
//                }(State(start, 0))
//            }

//            fun doMachine(machine: Machine): Long {
//                val seen = mutableMapOf<List<Long>, Long>()
//                val target = machine.jolts.getLongList()
//                val start = target.map { 0L }
//                val q = ArrayDeque(listOf(State(start, 0)))
////                debug { "target:$target, start:$start" }
//
////                debug { "start: ${machine.target}" }
//                val sol = mutableSetOf<Long>()
//                while (q.isNotEmpty()) {
//                    val (jolts, p) = q.removeLast()
//                    if (jolts == target) {
//                        sol.add(p)
//                        continue
//                    }
//                    if (jolts.withIndex().any { (i, j) -> j > target[i] }) continue
//                    if (jolts in seen && seen[jolts]!! <= p) continue
//                    seen[jolts] = p
//                    machine.buttons.map { inc ->
//                        buildList {
//                            jolts.forEachIndexed { i, c ->
//                                add(if (i in inc) c + 1 else c)
//
//                            }
//                        }//.debug()
//                    }.forEach { q += State(it, p + 1) }
//                }
//                return sol.min()
//            }

//            machines.sumOf { doMachine(it) }
        }
    }

    inline fun <A> withZ3(f: Context.() -> A): A = Context().use { f(it) }
    inline fun Context.minimizeInt(target: Expr<IntSort>, constraints: Optimize.() -> Unit): Int = with(mkOptimize()) {
        val result = MkMinimize(target)
        constraints()
        check(Check() == Status.SATISFIABLE) { "constraints not satisfiable" }
        (result.value as IntNum).int
    }

    val z3 = solution {

        val machines = lineParser { line ->
            val parts = line.split(" ")
            val lights = parts.first().removeSurrounding("[", "]").reversed()
                .fold(0L) { acc, ch -> acc shl 1 or if (ch == '#') 1 else 0 }
            val buttons = parts.subList(1, parts.lastIndex)
                .map { s -> s.removeSurrounding("(", ")").getInts().fold(0L) { acc, i -> acc or (1L shl i) } }
            val joltage = parts.last().removeSurrounding("{", "}").getIntList()
            Triple(lights, buttons, joltage)
        }

        part2 {
            machines().sumOf { (_, buttons, joltages) ->
                withZ3 {
                    val buttonPresses = buttons.withIndex().associate { (idx, button) -> button to mkIntConst("p$idx") }
                    val totalPresses = mkAdd(*buttonPresses.values.toTypedArray())
                    fun sumPresses(idx: Int) =
                        mkAdd(*buttonPresses.filterKeys { it ushr idx and 1L == 1L }.values.toTypedArray())

                    minimizeInt(totalPresses) {
                        // such that each joltage is the sum of presses of matching buttons
                        for ((i, joltage) in joltages.withIndex()) Add(mkEq(sumPresses(i), mkInt(joltage)))
                        // and that all presses are non-negative
                        for (it in buttonPresses) Add(mkLe(mkInt(0), it.value))
                    }
                }
            }

        }
    }

}
