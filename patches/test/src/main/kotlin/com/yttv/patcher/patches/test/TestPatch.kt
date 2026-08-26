package com.yttv.patcher.patches.test

import com.yttv.patcher.api.*
import com.yttv.patcher.common.ApkInfo
import com.yttv.patcher.common.Version
import java.io.File

class TestPatch : Patch {
    override val id: String = "TestPatch"
    override val name: String = "Test Patch"
    override val description: String = "A harmless test patch that adds a marker file to verify the pipeline."
    override val supportedPackages: List<String> = listOf(ApkInfo.YOUTUBE_TV_PACKAGE)
    override val supportedVersions: ClosedRange<Version>? = null
    override val dependencies: List<String> = emptyList()
    override val options: List<PatchOption<*>> = emptyList()

    override fun execute(context: PatchContext): PatchResult {
        context.info("Executing test patch...")
        val markerFile = File(context.workingDirectory, "META-INF/yttv-patcher-test")
        markerFile.parentFile?.mkdirs()
        markerFile.writeText("YTTV-Patcher test patch applied at ${System.currentTimeMillis()}")
        context.info("Test marker written to ${markerFile.name}")
        return PatchResult.Success("Test patch applied successfully")
    }
}
