package com.yttv.patcher.core

import com.yttv.patcher.api.PatchLogger
import java.io.File
import java.util.zip.ZipFile

/**
 * Loads and validates an APK file.
 */
class ApkLoader(private val logger: PatchLogger) {

    data class LoadedApk(
        val file: File,
        val manifest: ByteArray?,
        val dexEntries: List<String>,
        val resourceEntries: List<String>
    ) {
        fun hasManifest(): Boolean = manifest != null && manifest.isNotEmpty()
        fun hasDexFiles(): Boolean = dexEntries.isNotEmpty()
    }

    fun load(apkFile: File): LoadedApk {
        if (!apkFile.exists()) {
            throw ApkLoadException("APK file does not exist: ${apkFile.absolutePath}")
        }
        if (!apkFile.isFile) {
            throw ApkLoadException("Path is not a file: ${apkFile.absolutePath}")
        }
        if (!apkFile.canRead()) {
            throw ApkLoadException("Cannot read APK file: ${apkFile.absolutePath}")
        }

        logger.info("Loading APK: ${apkFile.name}")

        ZipFile(apkFile).use { zip ->
            val manifestEntry = zip.getEntry("AndroidManifest.xml")
            val manifest = manifestEntry?.let { zip.getInputStream(it).readBytes() }

            val dexEntries = mutableListOf<String>()
            val resourceEntries = mutableListOf<String>()

            zip.entries().asSequence().forEach { entry ->
                when {
                    entry.name.endsWith(".dex") -> dexEntries.add(entry.name)
                    entry.name.startsWith("res/") -> resourceEntries.add(entry.name)
                }
            }

            if (manifest == null || manifest.isEmpty()) {
                throw ApkLoadException("APK is missing or has empty AndroidManifest.xml")
            }

            if (dexEntries.isEmpty()) {
                throw ApkLoadException("APK contains no DEX files")
            }

            logger.info("Found ${dexEntries.size} DEX file(s) and ${resourceEntries.size} resource(s)")

            return LoadedApk(apkFile, manifest, dexEntries, resourceEntries)
        }
    }
}

class ApkLoadException(message: String) : Exception(message)
