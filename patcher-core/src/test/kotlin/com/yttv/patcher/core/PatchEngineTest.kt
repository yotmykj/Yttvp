package com.yttv.patcher.core

import com.yttv.patcher.api.*
import com.yttv.patcher.common.ApkInfo
import java.io.File
import kotlin.test.*

class PatchEngineTest {
    private val logger = DefaultPatchLogger()
    private val engine = PatchEngine(logger)

    @Test
    fun `execute successful patch`() {
        val patch = object : Patch {
            override val id = "success-patch"
            override val name = "Success"
            override val description = "Always succeeds"
            override val supportedPackages = emptyList<String>()
            override val supportedVersions = null
            override val dependencies = emptyList<String>()
            override val options = emptyList<PatchOption<*>>()
            override fun execute(context: PatchContext) = PatchResult.Success()
        }

        val context = createContext()
        val apkInfo = ApkInfo("com.google.android.youtube.tv", "1.0.0", 1)
        val result = engine.execute(listOf(patch), context, apkInfo)

        assertEquals(1, result.successCount)
        assertEquals(0, result.failedCount)
        assertTrue(result.allSuccessful)
    }

    @Test
    fun `skip incompatible patch`() {
        val patch = object : Patch {
            override val id = "incompat-patch"
            override val name = "Incompat"
            override val description = "Incompatible"
            override val supportedPackages = listOf("other.package")
            override val supportedVersions = null
            override val dependencies = emptyList<String>()
            override val options = emptyList<PatchOption<*>>()
            override fun execute(context: PatchContext) = PatchResult.Success()
        }

        val context = createContext()
        val apkInfo = ApkInfo("com.google.android.youtube.tv", "1.0.0", 1)
        val result = engine.execute(listOf(patch), context, apkInfo)

        assertEquals(0, result.successCount)
        assertEquals(1, result.skippedCount)
    }

    @Test
    fun `fail on missing dependency`() {
        val patch = object : Patch {
            override val id = "dep-patch"
            override val name = "Dep"
            override val description = "Needs dep"
            override val supportedPackages = emptyList<String>()
            override val supportedVersions = null
            override val dependencies = listOf("missing-dep")
            override val options = emptyList<PatchOption<*>>()
            override fun execute(context: PatchContext) = PatchResult.Success()
        }

        val context = createContext()
        val apkInfo = ApkInfo("com.google.android.youtube.tv", "1.0.0", 1)
        val result = engine.execute(listOf(patch), context, apkInfo)

        assertEquals(0, result.successCount)
        assertEquals(1, result.failedCount)
    }

    private fun createContext(): PatchContext {
        return DefaultPatchContext(
            apkFile = File("test.apk"),
            apkInfo = ApkInfo("com.google.android.youtube.tv", "1.0.0", 1),
            workingDirectory = File(System.getProperty("java.io.tmpdir"))
        )
    }
}
