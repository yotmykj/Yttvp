package com.yttv.patcher.core

import com.yttv.patcher.api.*
import com.yttv.patcher.common.ApkInfo

/**
 * Executes patches against an APK context.
 */
class PatchEngine(private val logger: PatchLogger) {

    private val compatibilityChecker = CompatibilityChecker()

    data class EngineResult(
        val results: Map<String, PatchResult>,
        val successCount: Int,
        val skippedCount: Int,
        val failedCount: Int
    ) {
        val allSuccessful: Boolean get() = failedCount == 0
    }

    fun execute(
        patches: List<Patch>,
        context: PatchContext,
        apkInfo: ApkInfo
    ): EngineResult {
        val results = mutableMapOf<String, PatchResult>()
        var successCount = 0
        var skippedCount = 0
        var failedCount = 0

        logger.info("Loading ${patches.size} patch(es)")

        for (patch in patches) {
            logger.info("Applying ${patch.name}...")

            // Check compatibility
            val compatResult = compatibilityChecker.checkCompatibility(patch, apkInfo)
            if (compatResult is CompatibilityResult.Incompatible) {
                val result = PatchResult.Skipped("Incompatible: ${compatResult.reason}")
                results[patch.id] = result
                skippedCount++
                logger.warn("${patch.name}: ${result.message}")
                continue
            }

            // Check dependencies
            val missingDeps = patch.dependencies.filter { it !in results.keys }
            if (missingDeps.isNotEmpty()) {
                val result = PatchResult.Failed("Missing dependencies: ${missingDeps.joinToString()}")
                results[patch.id] = result
                failedCount++
                logger.error("${patch.name}: ${result.message}")
                continue
            }

            // Execute patch
            val result = try {
                patch.execute(context)
            } catch (e: Exception) {
                PatchResult.Failed("Execution error: ${e.message}", e)
            }

            results[patch.id] = result
            when (result) {
                is PatchResult.Success -> {
                    successCount++
                    logger.info("[SUCCESS] ${patch.name}: ${result.message}")
                }
                is PatchResult.Skipped -> {
                    skippedCount++
                    logger.warn("[SKIPPED] ${patch.name}: ${result.message}")
                }
                is PatchResult.Failed -> {
                    failedCount++
                    logger.error("[FAILED] ${patch.name}: ${result.message}")
                }
            }
        }

        return EngineResult(results, successCount, skippedCount, failedCount)
    }
}
