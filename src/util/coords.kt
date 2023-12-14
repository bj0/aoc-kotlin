package util

import kotlin.math.absoluteValue


data class Point<T : Number>(val x: T, val y: T)

infix fun Point<Int>.mdist(other: Point<Int>) = (other.x - x).absoluteValue + (other.y - y).absoluteValue
infix fun Point<Long>.mdist(other: Point<Long>) = (other.x - x).absoluteValue + (other.y - y).absoluteValue

val Point<Int>.up get() = copy(y = (y - 1))
val Point<Int>.down get() = copy(y = (y + 1))
val Point<Int>.left get() = copy(x = (x - 1))
val Point<Int>.right get() = copy(x = (x + 1))

infix fun Int.point(y:Int) = Point(this, y)

fun main() {
    testLcm()
}