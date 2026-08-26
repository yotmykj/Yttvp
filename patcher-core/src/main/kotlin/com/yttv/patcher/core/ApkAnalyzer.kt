package com.yttv.patcher.core

import com.yttv.patcher.api.PatchLogger
import com.yttv.patcher.common.ApkInfo
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Analyzes an APK's AndroidManifest.xml to extract package information.
 * Uses a simplified binary XML parser for demonstration.
 * In production, use AXMLParser or similar library.
 */
class ApkAnalyzer(private val logger: PatchLogger) {

    fun analyze(manifestBytes: ByteArray): ApkInfo {
        logger.info("Analyzing APK manifest...")

        val packageName = extractStringAttribute(manifestBytes, "package")
            ?: throw ApkAnalysisException("Could not extract package name from manifest")

        val versionName = extractStringAttribute(manifestBytes, "android:versionName")
            ?: throw ApkAnalysisException("Could not extract version name from manifest")

        val versionCode = extractIntAttribute(manifestBytes, "android:versionCode")
            ?: throw ApkAnalysisException("Could not extract version code from manifest")

        val minSdk = extractIntAttribute(manifestBytes, "android:minSdkVersion")
        val targetSdk = extractIntAttribute(manifestBytes, "android:targetSdkVersion")

        logger.info("Package: $packageName")
        logger.info("Version: $versionName ($versionCode)")

        return ApkInfo(
            packageName = packageName,
            versionName = versionName,
            versionCode = versionCode.toLong(),
            minSdkVersion = minSdk,
            targetSdkVersion = targetSdk
        )
    }

    /**
     * Simplified binary XML string attribute extraction.
     * This is a basic implementation for the test pipeline.
     * For production, integrate a proper AXML parser library.
     */
    private fun extractStringAttribute(manifestBytes: ByteArray, attrName: String): String? {
        // Look for the attribute name in the string pool
        // This is a simplified heuristic approach
        val searchBytes = attrName.toByteArray(Charsets.UTF_16LE)
        val idx = manifestBytes.indexOfSubArray(searchBytes)
        if (idx < 0) return null

        // Try to find a value after the attribute name
        // In binary XML, string values typically follow attribute names
        // This is a very basic heuristic
        return tryFindStringValue(manifestBytes, idx + searchBytes.size)
    }

    private fun extractIntAttribute(manifestBytes: ByteArray, attrName: String): Int? {
        val searchBytes = attrName.toByteArray(Charsets.UTF_16LE)
        val idx = manifestBytes.indexOfSubArray(searchBytes)
        if (idx < 0) return null

        // Look for an integer value (4 bytes) after the attribute
        val valueIdx = idx + searchBytes.size + 8 // Skip some padding
        if (valueIdx + 4 <= manifestBytes.size) {
            return ByteBuffer.wrap(manifestBytes, valueIdx, 4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .int
                .takeIf { it > 0 && it < 100000 }
        }
        return null
    }

    private fun tryFindStringValue(manifestBytes: ByteArray, startIdx: Int): String? {
        // Look for UTF-16 string pattern after the attribute name
        for (i in startIdx until manifestBytes.size - 2) {
            if (manifestBytes[i] != 0.toByte()) continue
            // Try to read a UTF-16LE string
            val endIdx = (i + 2 until manifestBytes.size step 2)
                .firstOrNull { j ->
                    manifestBytes[j] == 0.toByte() && manifestBytes[j + 1] == 0.toByte()
                } ?: continue

            if (endIdx > i + 4) {
                val strBytes = manifestBytes.copyOfRange(i + 2, endIdx)
                val str = String(strBytes, Charsets.UTF_16LE).trim()
                if (str.isNotEmpty() && str.all { it.isPrintableOrDot() }) {
                    return str
                }
            }
        }
        return null
    }

    private fun Char.isPrintableOrDot(): Boolean =
        isLetterOrDigit() || this == '.' || this == '_' || this == '-' || this == ':'

    private fun ByteArray.indexOfSubArray(subArray: ByteArray): Int {
        outer@ for (i in 0..size - subArray.size) {
            for (j in subArray.indices) {
                if (this[i + j] != subArray[j]) continue@outer
            }
            return i
        }
        return -1
    }
}

class ApkAnalysisException(message: String) : Exception(message)
