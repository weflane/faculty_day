import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ZipMaker {
    private val goodExtensions = listOf(".txt", ".log")
    fun makeZip(fromFolder: String, toFile: String) {
        val folder = File(fromFolder)
        val zipFile = File(toFile)
        if (!folder.exists()) {
            println("Folder not found: $fromFolder")
            return
        }
        if (!folder.isDirectory) {
            println("This is not a folder: $fromFolder")
            return
        }
        if (!zipFile.name.endsWith(".zip")) {
            println("File should be named something.zip")
            return
        }
        println("Creating archive: $toFile")
        println("Taking files from: $fromFolder")
        var fileOut: FileOutputStream? = null
        var zipOut: ZipOutputStream? = null
        try {
            fileOut = FileOutputStream(zipFile)
            zipOut = ZipOutputStream(fileOut)
            val allFiles = mutableListOf<File>()
            findFiles(folder, allFiles)
            if (allFiles.isEmpty()) {
                println("No suitable files in the folder")
                return
            }
            for (file in allFiles) {
                putFileInZip(file, folder, zipOut)
            }
            println("Done! Archive created: ${zipFile.absolutePath}")
            println("Files in archive: ${allFiles.size}")
        } catch (e: Exception) {
            println("Error: ${e.message}")
        } finally {
            zipOut?.close()
            fileOut?.close()
        }
    }
    private fun findFiles(currentFolder: File, result: MutableList<File>) {
        val filesHere = currentFolder.listFiles() ?: return
        for (item in filesHere) {
            if (item.isDirectory) {
                findFiles(item, result)
            } else if (item.isFile) {
                val name = item.name.lowercase()
                if (goodExtensions.any { name.endsWith(it) }) {
                    result.add(item)
                }
            }
        }
    }
    private fun putFileInZip(file: File, mainFolder: File, zipOut: ZipOutputStream) {
        var fileIn: FileInputStream? = null
        try {
            val pathInZip = file.relativeTo(mainFolder).path
            val entry = ZipEntry(pathInZip)
            zipOut.putNextEntry(entry)
            fileIn = FileInputStream(file)
            val buffer = ByteArray(1024)
            var read: Int
            var totalBytes = 0L
            while (fileIn.read(buffer).also { read = it } > 0) {
                zipOut.write(buffer, 0, read)
                totalBytes += read
            }
            zipOut.closeEntry()
            println("Added: $pathInZip ($totalBytes bytes)")
        } catch (e: Exception) {
            println("Could not add ${file.name}: ${e.message}")
        } finally {
            fileIn?.close()
        }
    }
}

fun main() {
    val zipper = ZipMaker()
    // Example usage:
    // zipper.makeZip("C:/my files", "C:/archive.zip")
}