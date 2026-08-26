package com.yttv.patcher.core

import com.yttv.patcher.api.*
import com.yttv.patcher.common.ApkInfo
import java.io.File

/**
 * Orchestrates the complete APK patching pipeline.
 */
class Pipeline(private val logger: PatchLogger) {

    private val apkLoader = ApkLoader(logger)
    private val apkAnalyzer = ApkAnalyzer(logger)
    private val patchEngine = PatchEngine(logger)
    private val apkBuilder = ApkBuilder(logger)
    private val apkSigner = ApkSigner(logger)

    data class PipelineResult(
        val success: Boolean,
        val outputFile: File?,
        val apkInfo: ApkInfo?,
        val patchResults: Map<String, PatchResult>,
        val message: String
    )

    fun run(
        inputApk: File,
        outputApk: File,
        patchRegistry: PatchRegistry,
        enabledPatches: Set<String> = emptySet()
    ): PipelineResult {
        val workingDir = File(System.getProperty("java.io.tmpdir"), "yttv-patcher-${System.currentTimeMillis()}")
        workingDir.mkdirs()

        return try {
            // Step 1: Load APK
            logger.info("Loading APK...")
            val loadedApk = apkLoader.load(inputApk)

            // Step 2: Analyze APK
            logger.info("Analyzing APK...")
            val manifestBytes = loadedApk.manifest
                ?: return PipelineResult(false, null, null, emptyMap(), "Failed to read manifest")
            val apkInfo = apkAnalyzer.analyze(manifestBytes)

            // Validate it's a supported package
            if (apkInfo.packageName != ApkInfo.YOUTUBE_TV_PACKAGE) {
                return PipelineResult(
                    false, null, apkInfo, emptyMap(),
                    "Unsupported package: ${apkInfo.packageName}. Only ${ApkInfo.YOUTUBE_TV_PACKAGE} is supported."
                )
            }

            // Step 3: Determine patches to apply
            val patchesToApply = if (enabledPatches.isEmpty()) {
                patchRegistry.getCompatible(apkInfo)
            } else {
                patchRegistry.resolveDependencies(enabledPatches)
                    .filter { it.isCompatible(apkInfo) }
            }

            if (patchesToApply.isEmpty()) {
                logger.warn("No patches to apply")
            }

            // Step 4: Create patch context
            val context = DefaultPatchContext(
                apkFile = inputApk,
                apkInfo = apkInfo,
                workingDirectory = workingDir,
                dexFiles = emptyList(), // Would be populated by DEX loader in full implementation
                resourcesDirectory = null // Would be populated by resource decoder in full implementation
            )

            // Step 5: Execute patches
            val engineResult = patchEngine.execute(patchesToApply, context, apkInfo)

            if (engineResult.failedCount > 0) {
                return PipelineResult(
                    false, null, apkInfo, engineResult.results,
                    "${engineResult.failedCount} patch(es) failed"
                )
            }

            // Step 6: Build APK
            logger.info("Building APK...")
            val builtApk = File(workingDir, "built.apk")
            apkBuilder.build(inputApk, workingDir, builtApk)

            // Step 7: Sign APK
            logger.info("Signing APK...")
            apkSigner.sign(builtApk, outputApk)

            logger.info("[SUCCESS] Output: ${outputApk.absolutePath}")

            PipelineResult(true, outputApk, apkInfo, engineResult.results, "Success")

        } catch (e: ApkLoadException) {
            PipelineResult(false, null, null, emptyMap(), "APK load error: ${e.message}")
        } catch (e: ApkAnalysisException) {
            PipelineResult(false, null, null, emptyMap(), "APK analysis error: ${e.message}")
        } catch (e: Exception) {
            logger.error("Pipeline error: ${e.message}")
            e.printStackTrace()
            PipelineResult(false, null, null, emptyMap(), "Pipeline error: ${e.message}")
        } finally {
            // Cleanup working directory
            workingDir.deleteRecursively()
        }
    }
}
