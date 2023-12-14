package util

fun Sequence<String>.mapIntsNotNull() = mapNotNull(String::toIntOrNull)
fun Iterable<String>.mapIntsNotNull() = mapNotNull(String::toIntOrNull)
fun String.splitIntsNotNull(vararg delimiters: String = arrayOf(" ")) = split(*delimiters).mapIntsNotNull()

fun String.replace(vararg replacements: Pair<String, String>) = replacements.fold(this) { s, (a, b) -> s.replace(a, b) }

object Parsers {
    object Ints : Parser<List<Int>> by (lineParser(String::toIntOrNull).map { it.filterNotNull() })
    object Longs : Parser<List<Long>> by (lineParser(String::toLongOrNull).map { it.filterNotNull() })
}

/**
 * Finds all numbers in a string and returns them as a Sequence of a number.
 */
//inline fun <N : Number> String.getNumbers(crossinline transform: String.() -> N?): Sequence<N> =
//    Regex("""(?<!\d)-?\d+""")
//        .findAll(this)
//        .mapNotNull { it.value.transform() }


inline fun <N : Number> String.getNumbers(crossinline transform: String.() -> N?): Sequence<N> = sequence {
    var startPosition = -1
    for (position in indices) {
        val c = this@getNumbers[position]
        if (c.isDigit() || (c == '-' && this@getNumbers.getOrNull(position - 1)?.isDigit() != true)) {
            if (startPosition == -1) startPosition = position
        } else {
            if (startPosition != -1) {
                substring(startPosition, position).transform()?.let { yield(it) }
                startPosition = -1
            }
        }
    }
    if (startPosition != -1) {
        substring(startPosition).transform()?.let { yield(it) }
    }
}

/**
 * Finds all numbers in a string and returns them as a Sequence of Int.
 */
fun String.getInts(): Sequence<Int> = getNumbers(String::toIntOrNull)

/**
 * Finds all numbers in a string and returns them as a List of Int.
 */
fun String.getIntList() = getInts().toList()

/**
 * Finds all numbers in a string and returns them as a Sequence of Long.
 */
fun String.getLongs(): Sequence<Long> = getNumbers(String::toLongOrNull)

/**
 * Finds all numbers in a string and returns them as a List of Long.
 */
fun String.getLongList() = getLongs().toList()