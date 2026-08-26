package com.yttv.patcher.patches.test

import com.yttv.patcher.api.*
import com.yttv.patcher.common.ApkInfo
import com.yttv.patcher.common.Version

class TvInterfacePatch : Patch {
    override val id: String = "TvInterfacePatch"
    override val name: String = "TV Interface"
    override val description: String = "Modifies TV interface elements."
    override val supportedPackages: List<String> = listOf(ApkInfo.YOUTUBE_TV_PACKAGE)
    override val supportedVersions: ClosedRange<Version>? = null
    override val dependencies: List<String> = emptyList()
    override val options: List<PatchOption<*>> = emptyList()

    override fun execute(context: PatchContext): PatchResult {
        return PatchResult.Skipped("Patch not implemented yet.")
    }
}
