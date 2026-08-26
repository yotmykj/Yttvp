package com.yttv.patcher.core

import com.yttv.patcher.api.PatchLogger
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Rebuilds an APK from modified contents.
 */
class ApkBuilder(private val logger: PatchLogger) {

    fun build(
        sourceApk: File,
        workingDir: File,
        outputFile: File
    ) {
        logger.info("Building APK: ${outputFile.name}")

        if (outputFile.exists()) {
            outputFile.delete()
        }

        outputFile.parentFile?.mkdirs()

        ZipOutputStream(FileOutputStream(outputFile).buffered()).use { zos ->
            // Copy entries from source APK, allowing working dir to override
            val overrideFiles = workingDir.walkTopDown()
                .filter { it.isFile }
                .associateBy { it.relativeTo(workingDir).path.replace("\", "/") }

            // First add overridden files
            overrideFiles.forEach { (entryName, file) ->
                zos.putNextEntry(ZipEntry(entryName))
                file.inputStream().use { it.copyTo(zos) }
                zos.closeEntry()
            }

            // Then copy remaining entries from source
            java.util.zip.ZipFile(sourceApk).use { sourceZip ->
                sourceZip.entries().asSequence().forEach { entry ->
                    if (entry.name !in overrideFiles) {
                        zos.putNextEntry(ZipEntry(entry.name))
                        sourceZip.getInputStream(entry).use { it.copyTo(zos) }
                        zos.closeEntry()
                    }
                }
            }
        }

        logger.info("APK built successfully")
    }
}
