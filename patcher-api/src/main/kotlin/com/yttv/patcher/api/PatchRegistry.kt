package com.yttv.patcher.api

import com.yttv.patcher.common.ApkInfo

/**
 * Registry for managing available patches.
 */
class PatchRegistry {
    private val patches = mutableMapOf<String, Patch>()

    fun register(patch: Patch) {
        require(patch.id.isNotBlank()) { "Patch ID must not be blank" }
        require(patch.id !in patches) { "Patch with ID '${patch.id}' is already registered" }
        patches[patch.id] = patch
    }

    fun unregister(patchId: String) {
        patches.remove(patchId)
    }

    fun get(patchId: String): Patch? = patches[patchId]

    fun getAll(): List<Patch> = patches.values.toList()

    fun getCompatible(apkInfo: ApkInfo): List<Patch> {
        return patches.values.filter { it.isCompatible(apkInfo) }
    }

    fun getEnabled(enabledIds: Set<String>): List<Patch> {
        return enabledIds.mapNotNull { patches[it] }
    }

    fun resolveDependencies(patchIds: Set<String>): List<Patch> {
        val result = mutableListOf<Patch>()
        val visited = mutableSetOf<String>()

        fun visit(id: String) {
            if (id in visited) return
            visited.add(id)
            val patch = patches[id] ?: return
            patch.dependencies.forEach { visit(it) }
            result.add(patch)
        }

        patchIds.forEach { visit(it) }
        return result
    }

    fun clear() {
        patches.clear()
    }

    val size: Int get() = patches.size
}
