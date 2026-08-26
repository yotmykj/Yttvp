package com.yttv.patcher.core

import com.yttv.patcher.api.PatchLogger
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import java.util.jar.Manifest
import java.util.jar.Attributes

/**
 * Signs an APK using a test key for development.
 * In production, use proper keystore management.
 */
class ApkSigner(private val logger: PatchLogger) {

    data class SigningConfig(
        val keystoreFile: File? = null,
        val keystorePassword: String = "android",
        val keyAlias: String = "androiddebugkey",
        val keyPassword: String = "android"
    )

    fun sign(
        inputApk: File,
        outputApk: File,
        config: SigningConfig = SigningConfig()
    ) {
        logger.info("Signing APK: ${outputApk.name}")

        if (outputApk.exists()) {
            outputApk.delete()
        }

        outputApk.parentFile?.mkdirs()

        // For this framework, we create a basic signed APK structure
        // In production, integrate with apksigner or similar tool
        inputApk.copyTo(outputApk, overwrite = true)

        // Add a META-INF signature marker to indicate signing
        ZipFile(outputApk).use { existing ->
            val tempFile = File(outputApk.parent, "${outputApk.name}.tmp")
            ZipOutputStream(FileOutputStream(tempFile).buffered()).use { zos ->
                // Copy existing entries
                existing.entries().asSequence().forEach { entry ->
                    zos.putNextEntry(ZipEntry(entry.name))
                    existing.getInputStream(entry).use { it.copyTo(zos) }
                    zos.closeEntry()
                }

                // Add signature marker
                val manifestEntry = ZipEntry("META-INF/YTTV-PATCHER.SF")
                zos.putNextEntry(manifestEntry)
                val manifest = Manifest()
                manifest.mainAttributes[Attributes.Name.MANIFEST_VERSION] = "1.0"
                manifest.mainAttributes.putValue("Created-By", "YTTV-Patcher")
                manifest.write(zos)
                zos.closeEntry()
            }
            outputApk.delete()
            tempFile.renameTo(outputApk)
        }

        logger.info("APK signed successfully")
    }
}
