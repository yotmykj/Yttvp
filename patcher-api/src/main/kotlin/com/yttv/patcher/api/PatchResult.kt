package com.yttv.patcher.api

/**
 * Represents the result of applying a patch.
 */
sealed class PatchResult {
    abstract val message: String

    data class Success(override val message: String = "Patch applied successfully") : PatchResult()
    data class Skipped(override val message: String) : PatchResult()
    data class Failed(override val message: String, val cause: Throwable? = null) : PatchResult()

    val isSuccess: Boolean get() = this is Success
    val isSkipped: Boolean get() = this is Skipped
    val isFailed: Boolean get() = this is Failed
}
