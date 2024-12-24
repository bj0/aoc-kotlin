package util

import kotlinx.serialization.Serializable
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isReadable


private fun findDirectoryInAncestors(name: String) = generateSequence(Paths.get(name).toAbsolutePath()) {
    it.parent?.parent?.resolve(name)
}.firstOrNull { Files.isDirectory(it) }

private val searchPaths by lazy {
    listOfNotNull(
        findDirectoryInAncestors("output"),
    ).also { require(it.isNotEmpty()) { "Could not locate input directory" } }
}

private fun findFile(path: String) =
    searchPaths.firstNotNullOfOrNull { it.resolve(path).takeIf(Path::isReadable) }

@Serializable
data class Answer(val result:String, val timestamp: String)

@Serializable
data class Record(val key: PuzKey, val answer: List<Answer>)

