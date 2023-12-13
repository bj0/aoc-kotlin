package util

fun List<String>.transpose() =
    first().indices.map { i -> buildString { this@transpose.forEach { line -> append(line[i]) } } }
