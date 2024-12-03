package util

import space.kscience.kmath.operations.foldIndexed
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.asBuffer
import space.kscience.kmath.structures.first
import util.Direction.*
import kotlin.math.absoluteValue

typealias Point = Buffer<Long>

val Point.x get() = first()
val Point.y get() = get(1)
val Point.z get() = get(2)

//val Point<T>.up get() = mapIndexed { i, v -> if (i == 1) v - 1 else v }
//val Point<T>.down get() = mapIndexed { i, v -> if (i == 1) v + 1 else v }
//val Point<T>.left get() = mapIndexed { i, v -> if (i == 0) v - 1 else v }
//val Point<T>.right get() = mapIndexed { i, v -> if (i == 0) v + 1 else v }
val Point.up get() = move(Up)
val Point.down get() = move(Down)
val Point.left get() = move(Left)
val Point.right get() = move(Right)

fun <T> point(vararg vals: T) = vals.asBuffer()

fun Point.move(dir: Direction, steps: Int = 1) = when (dir) {
    Left -> point(x - steps, y)
    Up -> point(x, y - steps)
    Right -> point(x + steps, y)
    Down -> point(x, y + steps)
}

fun Point.mdist(other: Point) {
    require(size == other.size) { "points are not the same size" }
    foldIndexed(0L) { i, acc, v -> acc + (v - other[i]).absoluteValue }
}

//fun Point.neighbors