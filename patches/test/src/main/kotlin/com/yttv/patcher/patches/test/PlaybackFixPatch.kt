package com.yttv.patcher.patches.test

import com.yttv.patcher.api.*
import com.yttv.patcher.common.ApkInfo
import com.yttv.patcher.common.Version

class PlaybackFixPatch : Patch {
    override val id: String = "PlaybackFixPatch"
    override val name: String = "Playback Fix"
    override val description: String = "Fixes playback-related issues."
    override val supportedPackages: List<String> = listOf(ApkInfo.YOUTUBE_TV_PACKAGE)
    override val supportedVersions: ClosedRange<Version>? = null
    override val dependencies: List<String> = emptyList()
    override val options: List<PatchOption<*>> = emptyList()

    override fun execute(context: PatchContext): PatchResult {
        return PatchResult.Skipped("Patch not implemented yet.")
    }
}
