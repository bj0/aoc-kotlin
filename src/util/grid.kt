package util

interface Grid<T> {
    val xRange: LongRange
    val yRange: LongRange

    operator fun get(point: LongPoint): T?

    fun findAll(block: (LongPoint, T) -> Boolean): List<LongPoint>
}

data class MapGrid<T>(val data: Map<LongPoint, T>, override val xRange: LongRange, override val yRange: LongRange) :
    Grid<T>, Map<LongPoint, T> by data {
    override operator fun get(point: LongPoint): T? = data[point]

    override fun findAll(block: (LongPoint, T) -> Boolean) = entries.filter { (k, v) -> block(k, v) }.map { it.key }
}

@JvmInline
value class StringGrid(val data: List<String>) :
    Grid<Char> {
    override val xRange get() = 0L..<data.maxOf { it.length }
    override val yRange get() = 0L..<data.size

    override fun get(point: LongPoint): Char? {
        return data.getOrNull(point.y.toInt())?.getOrNull(point.x.toInt())
    }

    override fun findAll(block: (LongPoint, Char) -> Boolean): List<LongPoint> = buildList {
        data.forEachIndexed { y, row ->
            row.forEachIndexed { x, char ->
                val p = x.toLong() point y.toLong()
                if (block(p, char))
                    add(p)
            }
        }
    }
}

fun String.toGrid(delimiter: String = "\n") = split(delimiter).toGrid()

fun List<String>.toGrid(): Grid<Char> = StringGrid(this)

fun List<String>.toMapGrid(): Grid<Char> = MapGrid(
    data = buildMap {
        forEachIndexed { r, row ->
            row.forEachIndexed { c, char ->
                put(c.toLong() point r.toLong(), char)
            }
        }
    },
    yRange = 0L..<size,
    xRange = 0L..<maxOf { it.length })


fun <T> Grid<T>.walk(start: LongPoint, direction: GridDirection) =
    start.walk(direction).map { get(it) }.takeWhile { it != null }