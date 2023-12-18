@file:Suppress("ObjectPropertyName")

package year2023

import util.*

fun main() {
    listOf(::solution).solveAll()
}

private val solution = puzzle {

    fun String.hash() = fold(0) { acc, c -> (acc + c.code) * 17 % 256 }

    part1 {
        input.split(",").sumOf(String::hash)
    }

    part2 {
        val ops = input.split(",")
        val boxes = List(256) { mutableMapOf<String, Int>() }

        for (op in ops) {
            val (label, fl) = op.split('=', '-')
            if ('-' in op) {
                boxes[label.hash()].remove(label)
            } else {
                boxes[label.hash()][label] = fl.toInt()
            }
        }

        // 271384
        boxes.foldIndexed(0) { i,acc, m -> acc + m.values.foldIndexed(0) { j, acc2, fl -> acc2 + fl * (j + 1) * (i + 1) } }
    }
}