@file:Suppress("EnumEntryName")

package year2023

import arrow.core.fold
import util.*


fun main() {
    listOf(Day19::solution, Day19::alt).solveAll(
//        InputProvider.Example
    )
}


object Day19 {


    private fun String.parseRules(): List<Rule> = split(',').map { r ->
        when {
            ':' !in r -> Always(Destination.of(r))
            else -> {
                val field = Field.valueOf(r[0].toString())
                val dest = Destination.of(r.substringAfter(":"))
                val value = r.drop(2).substringBefore(":").toInt()
                when (r[1]) {
                    '<' -> LessThan(field, value, dest)
                    '>' -> Greater(field, value, dest)
                    else -> error("bad op")
                }
            }
        }
    }


    val solution = solution {

        val parser = parser {
            input.split("\n\n")
                .let { split ->
                    split.first().lines().associate { line ->
                        val (name, rules) = line.split("{", "}")
                        name to rules.parseRules()
                    } to split.last().lines().map { line ->
                        val (x, m, a, s) = line.getInts().toList()
                        Part(x, m, a, s)
                    }
                }
        }

        part1(parser) { (rules, parts) ->
            tailrec fun follow(part: Part, dest: String): Int {
                return when (val next = rules.getValue(dest).firstNotNullOf { it.check(part) }) {
                    Accept -> part.sum()
                    Reject -> 0
                    is Jump -> follow(part, next.workflow)
                }
            }

            parts.sumOf { follow(it, "in") }
        }

        part2(parser) { (rules, _) ->

            fun countParts(): Long {
                val bounds = MachineRange(Field.entries.associateWith { _ -> 1L..4000L })
                fun follow(
                    dest: Destination,
                    bounds: MachineRange
                ): Long = when (dest) {
                    Reject -> 0
                    Accept -> bounds.combos()
                    is Jump -> {
                        tailrec fun unwrap(bounds: MachineRange, rules: List<Rule>, acc: Long): Long {
                            if (rules.isEmpty() || !bounds.isValid()) return acc
                            val (add, next) = when (val rule = rules.first()) {
                                is Always -> follow(rule.dest, bounds) to bounds
                                is Greater -> {
                                    val (good, bad) = bounds.split(rule.field) { r ->
                                        (rule.value + 1..r.last) to (r.first..rule.value)
                                    }
                                    (if (good.isValid()) follow(rule.dest, good) else 0) to bad
                                }

                                is LessThan -> {
                                    val (good, bad) = bounds.split(rule.field) { r ->
                                        (r.first..<rule.value) to (rule.value..r.last)
                                    }
                                    (if (good.isValid()) follow(rule.dest, good) else 0) to bad
                                }
                            }
                            return unwrap(next, rules.drop(1), add + acc)
                        }

                        unwrap(bounds, rules.getValue(dest.workflow), 0)
                    }
                }
                return follow(Destination.of("in"), bounds)
            }

            countParts()
        }
    }

    val alt = solution {
        val parser = parser {
            input.split("\n\n")
                .let { split ->
                    split.first().lines().associate { line ->
                        val (name, rules) = line.split("{", "}")
                        name to rules.parseRules()
                    } to split.last().lines().map { line ->
                        val (x, m, a, s) = line.getInts().toList()
                        Part(x, m, a, s)
                    }
                }
        }

        part2(parser) { (rules, _) ->
            fun countParts(): Long {
                val bounds = MachineRange(Field.entries.associateWith { _ -> 1L..4000L })
                fun follow(
                    dest: Destination,
                    bounds: MachineRange
                ): Long = when (dest) {
                    Reject -> 0
                    Accept -> bounds.combos()
                    is Jump -> {
                        var next = bounds
                        var sum = 0L
                        for (rule in rules.getValue(dest.workflow)) {
                            when (rule) {
                                is Always -> {
                                    sum += follow(rule.dest, next)
                                    break
                                }

                                is Greater -> {
                                    val (good, bad) = next.split(rule.field) { r ->
                                        (rule.value + 1..r.last) to (r.first..rule.value)
                                    }
                                    if (good.isValid())
                                        sum += follow(rule.dest, good)
                                    next = bad
                                }

                                is LessThan -> {
                                    val (good, bad) = next.split(rule.field) { r ->
                                        (r.first..<rule.value) to (rule.value..r.last)
                                    }
                                    if (good.isValid())
                                        sum += follow(rule.dest, good)
                                    next = bad
                                }
                            }
                            if (!next.isValid())
                                break
                        }
                        sum
                    }
                }
                return follow(Destination.of("in"), bounds)
            }

            countParts()
        }

    }

    private fun MachineRange.replace(field: Field, range: LongRange) = MachineRange(map.toMutableMap().apply {
        put(field, range)
    })

    fun MachineRange.split(
        field: Field,
        split: (LongRange) -> Pair<LongRange, LongRange>
    ): Pair<MachineRange, MachineRange> {
        val (good, bad) = split(map.getValue(field))
        return replace(field, good) to replace(field, bad)
    }

    data class Part(val x: Int, val m: Int, val a: Int, val s: Int)
    enum class Field(val get: (Part) -> Int) {
        x(Part::x), m(Part::m), a(Part::a), s(Part::s)
    }

    sealed interface Destination {
        companion object {
            fun of(dest: String) = when (dest) {
                "A" -> Accept
                "R" -> Reject
                else -> Jump(dest)
            }
        }
    }

    data object Accept : Destination
    data object Reject : Destination
    data class Jump(val workflow: String) : Destination

    sealed interface Rule {
        fun check(part: Part): Destination?
    }

    data class Greater(val field: Field, val value: Int, val dest: Destination) : Rule {
        override fun check(part: Part) = dest.takeIf { field.get(part) > value }
    }

    data class LessThan(val field: Field, val value: Int, val dest: Destination) : Rule {
        override fun check(part: Part) = dest.takeIf { field.get(part) < value }
    }

    data class Always(val dest: Destination) : Rule {
        override fun check(part: Part) = dest
    }

    private fun Part.sum() = Field.entries.sumOf { it.get(this) }
    private fun MachineRange.isValid() = map.values.all { !it.isEmpty() }

    private fun MachineRange.combos() = map.fold(1L) { acc, (_, r) -> acc * (r.last - r.first + 1) }

    @JvmInline
    value class MachineRange(val map: Map<Field, LongRange>)
}

