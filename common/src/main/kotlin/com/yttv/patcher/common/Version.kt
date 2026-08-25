package com.yttv.patcher.common

/**
 * Represents a semantic version for compatibility checking.
 * Supports formats like "1.2.3", "1.2.3-beta", "1.2".
 */
data class Version(val major: Int, val minor: Int, val patch: Int = 0, val suffix: String? = null) :
    Comparable<Version> {

    override fun compareTo(other: Version): Int {
        var result = major.compareTo(other.major)
        if (result != 0) return result
        result = minor.compareTo(other.minor)
        if (result != 0) return result
        result = patch.compareTo(other.patch)
        if (result != 0) return result
        // Suffix comparison: no suffix > any suffix (release > pre-release)
        return when {
            suffix == null && other.suffix == null -> 0
            suffix == null -> 1
            other.suffix == null -> -1
            else -> suffix.compareTo(other.suffix)
        }
    }

    fun isAtLeast(other: Version): Boolean = this >= other
    fun isAtMost(other: Version): Boolean = this <= other

    companion object {
        fun parse(versionString: String): Version {
            val trimmed = versionString.trim()
            val suffixIndex = trimmed.indexOfFirst { !it.isDigit() && it != '.' }
            val numericPart = if (suffixIndex >= 0) trimmed.substring(0, suffixIndex) else trimmed
            val suffix = if (suffixIndex >= 0) trimmed.substring(suffixIndex) else null

            val parts = numericPart.split(".")
            val major = parts.getOrNull(0)?.toIntOrNull() ?: 0
            val minor = parts.getOrNull(1)?.toIntOrNull() ?: 0
            val patch = parts.getOrNull(2)?.toIntOrNull() ?: 0
            return Version(major, minor, patch, suffix?.ifEmpty { null })
        }
    }

    override fun toString(): String = buildString {
        append(major)
        append(".")
        append(minor)
        append(".")
        append(patch)
        suffix?.let { append(it) }
    }
}
