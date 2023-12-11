package year2023

import util.PuzDSL
import util.solveAll

fun main() {
    listOf(Day07, Day07AoK).solveAll(
//        InputProvider.Example
    )
}

object Day07 : PuzDSL({
    part1 {

        val order = "23456789TJQKA"

        fun rank(hand: String): Int {
//            val counted = hand.asIterable().distinct()
//                .map { hand.count { c -> c == it } }
//                .sortedDescending()
            val counted = hand.groupingBy { it }
                .eachCount().values
                .sortedDescending()
            if (counted.size < 2) return 6
            val (first, second) = counted
            return when {
                first == 4 -> 5
                first == 3 && second == 2 -> 4
                first == 3 -> 3
                first == 2 && second == 2 -> 2
                first == 2 -> 1
                else -> 0
            }
        }

        lines.map { line ->
            val (hand, bid) = line.split(" ")
            hand to bid
        }.sortedWith(compareBy<Pair<String, String>> { (hand, _) -> rank(hand) }
            .thenComparator { a, b ->
                a.first.zip(b.first).dropWhile { (x, y) -> x == y }
                    .first().let { (x, y) -> order.indexOf(x).compareTo(order.indexOf(y)) }
            }
        )
            .mapIndexed { i, (_, bid) -> (i + 1) * bid.toInt() }.sum()
    }

    part2 {
        fun type(hand: String): Int {
            val counted = hand.filter { it != 'J' }.groupingBy { it }
                .eachCount().values
                .sortedDescending()
            val j = hand.count { it == 'J' }
            if (counted.size < 2) return 6
            val (first, second) = counted
            return when {
                first >= 5 - j -> 6
                first >= 4 - j -> 5
                first >= 3 - j && second == 2 -> 4
                first >= 3 - j -> 3
                first == 2 && second == 2 -> 2
                first >= 2 - j -> 1
                else -> 0
            }
        }

        val order = "J23456789TQKA"

        fun String.toValue() = fold(0) { acc, c -> acc * order.length + order.indexOf(c) }

        lines.map { line ->
            val (hand, bid) = line.split(" ")
            hand to bid
        }.sortedWith(compareBy<Pair<String, String>> { (hand, _) -> type(hand) }
            .thenBy { (hand, _) -> hand.toValue() })
            .mapIndexed { i, (_, bid) -> (i + 1) * bid.toInt() }.sum()
    }
})

object Day07AoK : PuzDSL({
    fun parse(ctor: (String) -> Hand) = lineParser {
        val hand = ctor(it.substringBefore(' '))
        val bid = it.substringAfter(' ').toInt()
        hand to bid
    }

    fun List<Pair<Hand, Int>>.winnings() = sortedWith(compareBy(compareBy(Hand::type, Hand::value)) { it.first })
        .mapIndexed { index, (_, bid) -> (index + 1) * bid }
        .sum()

    part1(parse(::Hand)) { it.winnings() }
    part2(parse(Hand::withJokers)) { it.winnings() }
}) {
    data class Hand(val value: Int, val type: Type) {
        constructor(hand: String) : this(hand.toValue(), hand.toHandType())

        companion object {
            fun withJokers(hand: String) = Hand(hand.replace('J', '*'))

            private fun String.toHandType() = Type.fromCounts(
                groupingBy { it }.eachCountTo(mutableMapOf()).apply {
                    remove('*')?.let { wildcards ->
                        val strongest = maxWithOrNull(compareBy({ it.value }, { indexOf(it.key) }))?.key ?: 'A'
                        merge(strongest, wildcards, Int::plus)
                    }
                }.values
            )

            private const val CARD_ORDER = "*23456789TJQKA"
            private fun String.toValue(): Int =
                fold(0) { acc, c -> acc * CARD_ORDER.length + CARD_ORDER.indexOf(c) }
        }

        enum class Type {
            High,
            OnePair,
            TwoPair,
            ThreeOfAKind,
            FullHouse,
            FourOfAKind,
            FiveOfAKind;

            companion object {
                fun fromCounts(counts: Collection<Int>) = when (counts.size) {
                    1 -> FiveOfAKind
                    2 -> if (counts.max() == 4) FourOfAKind else FullHouse
                    3 -> if (counts.max() == 3) ThreeOfAKind else TwoPair
                    4 -> OnePair
                    else -> High
                }
            }
        }
    }
}
