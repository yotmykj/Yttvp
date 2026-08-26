package com.yttv.patcher.patches.test

import com.yttv.patcher.api.*
import com.yttv.patcher.common.ApkInfo
import com.yttv.patcher.common.Version

class DpadFixPatch : Patch {
    override val id: String = "DpadFixPatch"
    override val name: String = "DPad Fix"
    override val description: String = "Fixes D-pad navigation issues."
    override val supportedPackages: List<String> = listOf(ApkInfo.YOUTUBE_TV_PACKAGE)
    override val supportedVersions: ClosedRange<Version>? = null
    override val dependencies: List<String> = emptyList()
    override val options: List<PatchOption<*>> = emptyList()

    override fun execute(context: PatchContext): PatchResult {
        return PatchResult.Skipped("Patch not implemented yet.")
    }
}
