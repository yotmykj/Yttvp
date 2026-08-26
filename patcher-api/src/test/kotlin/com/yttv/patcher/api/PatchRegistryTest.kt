package com.yttv.patcher.api

import com.yttv.patcher.common.ApkInfo
import com.yttv.patcher.common.Version
import kotlin.test.*

class PatchRegistryTest {
    private val registry = PatchRegistry()

    @BeforeTest
    fun setup() {
        registry.clear()
    }

    @Test
    fun `register and retrieve patch`() {
        val patch = createTestPatch("test-1")
        registry.register(patch)
        assertNotNull(registry.get("test-1"))
    }

    @Test
    fun `duplicate registration throws`() {
        val patch = createTestPatch("test-1")
        registry.register(patch)
        assertFailsWith<IllegalArgumentException> {
            registry.register(patch)
        }
    }

    @Test
    fun `get compatible patches`() {
        val patch = object : Patch {
            override val id = "compat-patch"
            override val name = "Compat"
            override val description = "Test"
            override val supportedPackages = listOf("com.google.android.youtube.tv")
            override val supportedVersions = Version.parse("1.0.0")..Version.parse("2.0.0")
            override val dependencies = emptyList<String>()
            override val options = emptyList<PatchOption<*>>()
            override fun execute(context: PatchContext) = PatchResult.Success()
        }
        registry.register(patch)

        val compatibleApk = ApkInfo("com.google.android.youtube.tv", "1.5.0", 100)
        val incompatibleApk = ApkInfo("com.other.app", "1.5.0", 100)

        assertEquals(1, registry.getCompatible(compatibleApk).size)
        assertEquals(0, registry.getCompatible(incompatibleApk).size)
    }

    @Test
    fun `resolve dependencies in order`() {
        val patchA = createTestPatch("patch-a", deps = emptyList())
        val patchB = createTestPatch("patch-b", deps = listOf("patch-a"))
        registry.register(patchA)
        registry.register(patchB)

        val resolved = registry.resolveDependencies(setOf("patch-b"))
        assertEquals(2, resolved.size)
        assertEquals("patch-a", resolved[0].id)
        assertEquals("patch-b", resolved[1].id)
    }

    private fun createTestPatch(id: String, deps: List<String> = emptyList()): Patch {
        return object : Patch {
            override val id = id
            override val name = id
            override val description = "Test patch"
            override val supportedPackages = emptyList<String>()
            override val supportedVersions = null
            override val dependencies = deps
            override val options = emptyList<PatchOption<*>>()
            override fun execute(context: PatchContext) = PatchResult.Success()
        }
    }
}
