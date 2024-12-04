package util

import space.kscience.kmath.operations.foldIndexed
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.asBuffer
import space.kscience.kmath.structures.first
import util.Direction.*
import kotlin.math.absoluteValue

typealias KPoint = Buffer<Long>

val KPoint.x get() = first()
val KPoint.y get() = get(1)
val KPoint.z get() = get(2)

//val Point<T>.up get() = mapIndexed { i, v -> if (i == 1) v - 1 else v }
//val Point<T>.down get() = mapIndexed { i, v -> if (i == 1) v + 1 else v }
//val Point<T>.left get() = mapIndexed { i, v -> if (i == 0) v - 1 else v }
//val Point<T>.right get() = mapIndexed { i, v -> if (i == 0) v + 1 else v }
val KPoint.up get() = move(Up)
val KPoint.down get() = move(Down)
val KPoint.left get() = move(Left)
val KPoint.right get() = move(Right)

fun <T : Number> point(vararg vals: T): KPoint = vals.map { it.toLong() }.asBuffer()

fun KPoint.move(dir: Direction, steps: Int = 1) = when (dir) {
    Left -> point(x - steps, y)
    Up -> point(x, y - steps)
    Right -> point(x + steps, y)
    Down -> point(x, y + steps)
}

fun KPoint.mdist(other: KPoint) {
    require(size == other.size) { "points are not the same size" }
    foldIndexed(0L) { i, acc, v -> acc + (v - other[i]).absoluteValue }
}

// direct neighbors not including diagonals
fun KPoint.neighbors() = sequenceOf(up, left, down, right)

// neighbors including diagonals
fun KPoint.allNeighbors() = sequenceOf(up, up.right, right, right.down, down, down.left, left, left.up)