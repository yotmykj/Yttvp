package com.yttv.patcher.api

/**
 * Represents a typed option that can be configured for a patch.
 */
sealed class PatchOption<T>(
    val key: String,
    val description: String,
    val defaultValue: T,
    val required: Boolean = false
) {
    class BooleanOption(
        key: String,
        description: String,
        defaultValue: Boolean = false,
        required: Boolean = false
    ) : PatchOption<Boolean>(key, description, defaultValue, required)

    class StringOption(
        key: String,
        description: String,
        defaultValue: String = "",
        required: Boolean = false
    ) : PatchOption<String>(key, description, defaultValue, required)

    class IntOption(
        key: String,
        description: String,
        defaultValue: Int = 0,
        required: Boolean = false
    ) : PatchOption<Int>(key, description, defaultValue, required)

    class FloatOption(
        key: String,
        description: String,
        defaultValue: Float = 0f,
        required: Boolean = false
    ) : PatchOption<Float>(key, description, defaultValue, required)

    class ListOption(
        key: String,
        description: String,
        defaultValue: List<String> = emptyList(),
        required: Boolean = false
    ) : PatchOption<List<String>>(key, description, defaultValue, required)
}
