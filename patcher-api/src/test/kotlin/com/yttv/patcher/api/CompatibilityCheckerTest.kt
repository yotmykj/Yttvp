package com.yttv.patcher.api

import com.yttv.patcher.common.ApkInfo
import com.yttv.patcher.common.Version
import kotlin.test.*

class CompatibilityCheckerTest {
    private val checker = CompatibilityChecker()

    @Test
    fun `compatible when no restrictions`() {
        val patch = createPatch(supportedPackages = emptyList(), versions = null)
        val apk = ApkInfo("any.package", "1.0.0", 1)
        assertTrue(checker.checkCompatibility(patch, apk) is CompatibilityResult.Compatible)
    }

    @Test
    fun `incompatible package`() {
        val patch = createPatch(supportedPackages = listOf("com.google.android.youtube.tv"))
        val apk = ApkInfo("com.other.app", "1.0.0", 1)
        val result = checker.checkCompatibility(patch, apk)
        assertTrue(result is CompatibilityResult.Incompatible)
    }

    @Test
    fun `incompatible version`() {
        val patch = createPatch(
            supportedPackages = listOf("com.google.android.youtube.tv"),
            versions = Version.parse("1.0.0")..Version.parse("2.0.0")
        )
        val apk = ApkInfo("com.google.android.youtube.tv", "3.0.0", 1)
        val result = checker.checkCompatibility(patch, apk)
        assertTrue(result is CompatibilityResult.Incompatible)
    }

    @Test
    fun `compatible version`() {
        val patch = createPatch(
            supportedPackages = listOf("com.google.android.youtube.tv"),
            versions = Version.parse("1.0.0")..Version.parse("2.0.0")
        )
        val apk = ApkInfo("com.google.android.youtube.tv", "1.5.0", 1)
        assertTrue(checker.checkCompatibility(patch, apk) is CompatibilityResult.Compatible)
    }

    private fun createPatch(
        supportedPackages: List<String>,
        versions: ClosedRange<Version>? = null
    ): Patch {
        return object : Patch {
            override val id = "test"
            override val name = "Test"
            override val description = "Test"
            override val supportedPackages = supportedPackages
            override val supportedVersions = versions
            override val dependencies = emptyList<String>()
            override val options = emptyList<PatchOption<*>>()
            override fun execute(context: PatchContext) = PatchResult.Success()
        }
    }
}
