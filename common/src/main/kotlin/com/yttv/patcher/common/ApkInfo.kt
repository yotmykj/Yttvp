package com.yttv.patcher.common

/**
 * Represents basic information extracted from an APK.
 */
data class ApkInfo(
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val minSdkVersion: Int? = null,
    val targetSdkVersion: Int? = null
) {
    companion object {
        const val YOUTUBE_TV_PACKAGE = "com.google.android.youtube.tv"
    }
}
