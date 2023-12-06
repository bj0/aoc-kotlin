package util

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isReadable
import kotlin.io.path.readText

sealed interface PuzzleInput {
    val input: String
    val lines: List<String>
    val lineSeq: Sequence<String>

    companion object {
        fun of(input: String): PuzzleInput = Impl(input)
        fun of(file: Path): PuzzleInput = of(file.readText().trimEnd())
        fun of(year: Int, day: Int, fileName: String = ""): PuzzleInput =
            findInputFile(year, day, fileName)?.let(::of)
                ?: error("No input $fileName for $year, $day in $searchPaths")
    }

    private data class Impl(override val input: String) : PuzzleInput {
        override val lines = input.lines()
        override val lineSeq = input.lineSequence()
    }
}

private fun findDirectoryInAncestors(name: String) = generateSequence(Paths.get(name).toAbsolutePath()) {
    it.parent?.parent?.resolve(name)
}.firstOrNull { Files.isDirectory(it) }

private fun findInputFile(path: String) =
    searchPaths.firstNotNullOfOrNull { it.resolve(path).takeIf(Path::isReadable) }

private fun findInputFile(year: Int, day: Int, name: String = "") =
    findInputFile(inputFilePath(year, day, name))

fun inputFilePath(year: Int, day: Int, name: String) = "%d/day%02d%s.txt".format(year, day, name)

private val searchPaths by lazy {
    listOfNotNull(
        findDirectoryInAncestors("input"),
        findDirectoryInAncestors("aoc-input")
    ).also { require(it.isNotEmpty()) { "Could not locate input directory" } }
}

@JvmInline
value class FixedInput(private val input: PuzzleInput) : InputProvider {
    override fun forPuzzle(year: Int, day: Int) = input
    override fun toString() = "Fixed($input)"
}

private fun fileInputProvider(file: String, name: String = file.removeSuffix(".txt")) =
    InputProvider(name) { year, day -> PuzzleInput.of(year, day, file) }

fun InputProvider(name: String, provider: InputProvider) = object : InputProvider by provider {
    override fun toString() = name
}

fun interface InputProvider {
    fun forPuzzle(year: Int, day: Int): PuzzleInput

    companion object : InputProvider {
        val Example = fileInputProvider("_test")
        val Default = this
        override fun toString() = "puzzle"
        override fun forPuzzle(year: Int, day: Int) =
            PuzzleInput.of(year, day)

        fun raw(content: String) = FixedInput(PuzzleInput.of(content))
        fun forFile(name: String) = fileInputProvider(name)
    }

    fun <R> withInput(content: String, block: context(PuzzleInput)() -> R) = PuzzleInput.of(content).run(block)


}