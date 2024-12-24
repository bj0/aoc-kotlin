package util

interface Grid<T> {
    val xRange: LongRange
    val yRange: LongRange

    val indices get() = yRange.asSequence().flatMap { y -> xRange.asSequence().map { x -> LongPoint(x, y) } }

    operator fun get(point: LongPoint): T?

    operator fun contains(point: LongPoint): Boolean

    fun copy(new: Map<LongPoint, T>): Grid<T>

    fun find(block: (LongPoint, T) -> Boolean): LongPoint?

    fun findAll(block: (LongPoint, T) -> Boolean): Set<LongPoint>
}

data class MapGrid<T>(val data: Map<LongPoint, T>, override val xRange: LongRange, override val yRange: LongRange) :
    Grid<T>, Map<LongPoint, T> by data {
    override operator fun get(point: LongPoint): T? = data[point]

    override fun contains(point: LongPoint): Boolean = point in data

    override fun copy(new: Map<LongPoint, T>): MapGrid<T> {
        return this.copy(data = data + new)
    }

    override fun find(block: (LongPoint, T) -> Boolean): LongPoint? =
        entries.firstOrNull { (k, v) -> block(k, v) }?.key


    override fun findAll(block: (LongPoint, T) -> Boolean) = filter { (k, v) -> block(k, v) }.keys
}

@JvmInline
value class StringGrid(val data: List<String>) :
    Grid<Char> {
    override val xRange get() = 0L..<data.maxOf { it.length }
    override val yRange get() = 0L..<data.size

    override fun get(point: LongPoint): Char? {
        return data.getOrNull(point.y.toInt())?.getOrNull(point.x.toInt())
    }

    override fun contains(point: LongPoint): Boolean = point.x in xRange && point.y in yRange

    override fun copy(new: Map<LongPoint, Char>): Grid<Char> = buildList {
        yRange.forEach { y ->
            add(buildString {
                xRange.forEach { x ->
                    append(new[x point y] ?: get(x point y))
                }
            })
        }
    }.toStringGrid()

    override fun find(block: (LongPoint, Char) -> Boolean): LongPoint? {
        data.forEachIndexed { y, row ->
            row.forEachIndexed { x, char ->
                val p = x.toLong() point y.toLong()
                if (block(p, char)) return p
            }
        }
        return null
    }

    override fun findAll(block: (LongPoint, Char) -> Boolean): Set<LongPoint> = buildSet {
        data.forEachIndexed { y, row ->
            row.forEachIndexed { x, char ->
                val p = x.toLong() point y.toLong()
                if (block(p, char))
                    add(p)
            }
        }
    }
}

fun String.toGrid(delimiter: String = "\n") = split(delimiter).toStringGrid()

fun <T> List<List<T>>.toGrid(): Grid<T> = MapGrid(
    data = buildMap {
        forEachIndexed { r, row ->
            row.forEachIndexed { c, item ->
                put(c.toLong() point r.toLong(), item)
            }
        }
    },
    yRange = 0L..<size,
    xRange = 0L..<maxOf { it.size })


fun List<String>.toStringGrid(): Grid<Char> = StringGrid(this)

fun List<String>.toMapGrid(): MapGrid<Char> = MapGrid(
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