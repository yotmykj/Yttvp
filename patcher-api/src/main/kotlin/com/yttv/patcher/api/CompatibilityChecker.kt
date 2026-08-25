package com.yttv.patcher.api

import com.yttv.patcher.common.ApkInfo
import com.yttv.patcher.common.Version

/**
 * Checks compatibility between patches and APKs.
 */
class CompatibilityChecker {

    fun checkCompatibility(patch: Patch, apkInfo: ApkInfo): CompatibilityResult {
        if (patch.supportedPackages.isNotEmpty() && apkInfo.packageName !in patch.supportedPackages) {
            return CompatibilityResult.Incompatible(
                "Package '${apkInfo.packageName}' is not supported. Supported: ${patch.supportedPackages.joinToString()}"
            )
        }

        if (patch.supportedVersions != null) {
            val version = try {
                Version.parse(apkInfo.versionName)
            } catch (e: Exception) {
                return CompatibilityResult.Incompatible(
                    "Cannot parse version '${apkInfo.versionName}': ${e.message}"
                )
            }
            if (version !in patch.supportedVersions) {
                return CompatibilityResult.Incompatible(
                    "Version $version is not in supported range ${patch.supportedVersions}"
                )
            }
        }

        return CompatibilityResult.Compatible
    }

    fun findCompatiblePatches(patches: List<Patch>, apkInfo: ApkInfo): List<Patch> {
        return patches.filter { checkCompatibility(it, apkInfo) is CompatibilityResult.Compatible }
    }
}

sealed class CompatibilityResult {
    data object Compatible : CompatibilityResult()
    data class Incompatible(val reason: String) : CompatibilityResult()
}
