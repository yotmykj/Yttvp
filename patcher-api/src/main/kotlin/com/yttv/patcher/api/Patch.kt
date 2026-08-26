package com.yttv.patcher.api

import com.yttv.patcher.common.ApkInfo
import com.yttv.patcher.common.Version

/**
 * Interface that all patches must implement.
 */
interface Patch {
    /** Unique identifier for the patch. */
    val id: String

    /** Human-readable name. */
    val name: String

    /** Description of what the patch does. */
    val description: String

    /** List of package names this patch supports. Empty means all packages. */
    val supportedPackages: List<String>

    /** Version range this patch supports. Null means all versions. */
    val supportedVersions: ClosedRange<Version>?

    /** IDs of patches that must be applied before this one. */
    val dependencies: List<String>

    /** Options that can be configured for this patch. */
    val options: List<PatchOption<*>>

    /**
     * Execute the patch with the given context.
     */
    fun execute(context: PatchContext): PatchResult

    /**
     * Check if this patch is compatible with the given APK info.
     */
    fun isCompatible(apkInfo: ApkInfo): Boolean {
        if (supportedPackages.isNotEmpty() && apkInfo.packageName !in supportedPackages) {
            return false
        }
        if (supportedVersions != null) {
            val version = try {
                Version.parse(apkInfo.versionName)
            } catch (_: Exception) {
                return false
            }
            if (version !in supportedVersions) {
                return false
            }
        }
        return true
    }
}
