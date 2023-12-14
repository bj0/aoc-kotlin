package util

fun List<String>.transpose() =
    first().indices.map { i -> buildString { this@transpose.forEach { line -> append(line[i]) } } }

fun List<String>.rotate() = asReversed().transpose()
