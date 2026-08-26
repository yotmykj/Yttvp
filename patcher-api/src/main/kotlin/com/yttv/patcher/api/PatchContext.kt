package com.yttv.patcher.api

import com.yttv.patcher.common.ApkInfo
import java.io.File

/**
 * Provides context and resources to patches during execution.
 */
interface PatchContext {
    val apkFile: File
    val apkInfo: ApkInfo
    val workingDirectory: File
    val options: Map<String, Any>
    val logger: PatchLogger

    /**
     * Access to the APK's DEX files.
     */
    val dexFiles: List<File>

    /**
     * Access to the APK's resources.
     */
    val resourcesDirectory: File?

    /**
     * Get a typed option value.
     */
    fun <T> getOption(key: String, defaultValue: T): T

    /**
     * Log a message at INFO level.
     */
    fun info(message: String)

    /**
     * Log a message at WARN level.
     */
    fun warn(message: String)

    /**
     * Log a message at ERROR level.
     */
    fun error(message: String)
}

/**
 * Logger interface for patches.
 */
interface PatchLogger {
    fun info(message: String)
    fun warn(message: String)
    fun error(message: String)
    fun debug(message: String)
}
