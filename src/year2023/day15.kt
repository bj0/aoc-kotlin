@file:Suppress("ObjectPropertyName")

package year2023

import util.*

fun main() {
    listOf(::solution).solveAll()
}

private val solution = puzzle {

    fun String.hash() = fold(0) { acc, c -> (acc + c.code) * 17 % 256 }

    part1 {
        input.replace("\n", "").split(",")
            .map(String::hash).sum()
    }

    part2 {
        val ops = input.split(",")
        val boxes = List(256) { mutableMapOf<String, Int>() }

        for (op in ops) {
            val (label, fl) = op.split('=','-')
            if('-' in op){
                boxes[label.hash()].remove(label)
            } else {
                boxes[label.hash()][label] = fl.toInt()
            }
        }

        boxes.mapIndexed { i, m -> m.values.mapIndexed{ j, l -> l * (j+1)*(i+1)}.sum() }.sum()
    }
}