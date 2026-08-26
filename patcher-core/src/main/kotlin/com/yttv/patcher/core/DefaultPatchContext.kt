package com.yttv.patcher.core

import com.yttv.patcher.api.PatchContext
import com.yttv.patcher.api.PatchLogger
import com.yttv.patcher.common.ApkInfo
import java.io.File

/**
 * Default implementation of PatchContext.
 */
class DefaultPatchContext(
    override val apkFile: File,
    override val apkInfo: ApkInfo,
    override val workingDirectory: File,
    override val options: Map<String, Any> = emptyMap(),
    override val dexFiles: List<File> = emptyList(),
    override val resourcesDirectory: File? = null
) : PatchContext {

    override val logger: PatchLogger = DefaultPatchLogger()

    @Suppress("UNCHECKED_CAST")
    override fun <T> getOption(key: String, defaultValue: T): T {
        return options[key] as? T ?: defaultValue
    }

    override fun info(message: String) = logger.info(message)
    override fun warn(message: String) = logger.warn(message)
    override fun error(message: String) = logger.error(message)
}

class DefaultPatchLogger : PatchLogger {
    override fun info(message: String) = println("[INFO] $message")
    override fun warn(message: String) = println("[WARN] $message")
    override fun error(message: String) = println("[ERROR] $message")
    override fun debug(message: String) = println("[DEBUG] $message")
}
