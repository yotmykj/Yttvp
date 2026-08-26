package com.yttv.patcher.patches.test

import com.yttv.patcher.api.PatchResult
import com.yttv.patcher.common.ApkInfo
import com.yttv.patcher.core.DefaultPatchContext
import java.io.File
import kotlin.test.*

class TestPatchTest {
    @Test
    fun `test patch returns success`() {
        val patch = TestPatch()
        val context = DefaultPatchContext(
            apkFile = File("test.apk"),
            apkInfo = ApkInfo("com.google.android.youtube.tv", "1.0.0", 1),
            workingDirectory = File(System.getProperty("java.io.tmpdir"), "test-patch-${System.currentTimeMillis()}")
        )
        context.workingDirectory.mkdirs()

        val result = patch.execute(context)
        assertTrue(result is PatchResult.Success, "Expected success but got: ${result.message}")

        val markerFile = File(context.workingDirectory, "META-INF/yttv-patcher-test")
        assertTrue(markerFile.exists(), "Marker file should exist")
        assertTrue(markerFile.readText().contains("YTTV-Patcher test patch applied"))
    }
}
