@file:Suppress("EnumEntryName")

package year2023

import util.*


fun main() {
    listOf(Day20::solution).solveAll(
//        InputProvider.Example
//        InputProvider.raw(
//            """broadcaster -> a
//%a -> inv, con
//&inv -> b
//%b -> con
//&con -> output"""
//        )
    )
}


object Day20 {


    val solution = solution {


        part1(Machines) { machines ->

            var low = 0
            var high = 0
            repeat(1000) {
                machines.pushButton { sig -> if (sig.pulse.isLow) low += sig.to.size else high += sig.to.size }
            }
            low * high
        }

        part2(Machines) { machines ->

            // these are the inputs to the conjuction that signals rx, need to find when they all get high at the same time
            val names = mutableListOf("kd", "zf", "vg", "gs")
            val cycles = mutableMapOf<String, Pair<Long, Long>>()
            var i = 1
            while (names.isNotEmpty()) {
                machines.pushButton { (from, pulse, to) ->
                    if ("rg" in to && pulse.isHigh) {
                        val seen = cycles[from]
                        when {
                            seen == null -> cycles[from] = i.toLong() to 0
                            seen.second == 0L -> {
                                cycles[from] = seen.first to (i - seen.first)
                                names.remove(from)
                            }

                            else -> {}
                        }
                    }
                }
                i++
            }

            cycles.values.crt()
        }
    }

    enum class Pulse {
        High, Low;

        val isHigh get() = this == High
        val isLow get() = !isHigh
    }

    enum class State { On, Off }

    fun State.flip(): State = when (this) {
        State.On -> State.Off
        State.Off -> State.On
    }

    data class Signal(val from: String, val pulse: Pulse, val to: List<String>)

    @JvmInline
    value class Machines(private val modules: Map<String, Module>) {

        init {
            // init conjuction memory
            modules.forEach { (name, module) ->
                module.kids.mapNotNull { modules[it] }
                    .filterIsInstance<Conjunction>()
                    .forEach { kid -> kid.receive(name, Pulse.Low) }
            }
        }

        companion object : Parser<Machines> by (mapParser { line ->
            val (module, out) = line.split(" -> ")
            val kids = out.split(", ")
            when (module.first()) {
                '%' -> put(module.drop(1), FlipFlop(kids))
                '&' -> put(module.drop(1), Conjunction(kids))
                else -> put(module, Broadcast(kids))
            }
        }.map(::Machines))

        fun pushButton(signal: (Signal) -> Unit) {
            val q = ArrayDeque(listOf(Signal("button", Pulse.Low, listOf("broadcaster"))))

            while (q.isNotEmpty()) {
                val (from, pulse, kids) = q.removeFirst().also(signal)
                for (kid in kids) {
                    modules[kid]?.let { mod ->
                        mod.receive(from, pulse)?.let {
                            q += Signal(kid, it, mod.kids)
                        }
                    }
                }
            }
        }

        sealed class Module(val kids: List<String>) {
            abstract fun receive(from: String, pulse: Pulse): Pulse?
        }


        class FlipFlop(kids: List<String>) : Module(kids) {
            private var state = State.Off
            override fun receive(from: String, pulse: Pulse): Pulse? =
                if (pulse.isLow) {
                    if (state.flip().also { state = it } == State.On) Pulse.High else Pulse.Low
                } else null
        }

        class Broadcast(kids: List<String>) : Module(kids) {
            override fun receive(from: String, pulse: Pulse) = pulse
        }

        class Conjunction(kids: List<String>) : Module(kids) {
            private val memory = mutableMapOf<String, Pulse>()
            override fun receive(from: String, pulse: Pulse): Pulse {
                return if (memory.apply { put(from, pulse) }.values.any { it.isLow }) Pulse.High else Pulse.Low
            }
        }
    }
}