package com.yttv.patcher.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import com.yttv.patcher.api.PatchRegistry
import com.yttv.patcher.core.DefaultPatchLogger
import com.yttv.patcher.core.Pipeline
import com.yttv.patcher.patches.test.*
import java.io.File

class YttvPatcher : CliktCommand(name = "yttv-patcher") {
    override fun run() = Unit
}

class InfoCommand : CliktCommand(name = "info", help = "Show APK information") {
    private val apk by argument("APK").file(mustExist = true, canBeDir = false)

    override fun run() {
        echo("APK: ${apk.absolutePath}")
        echo("Size: ${apk.length()} bytes")
    }
}

class ListPatchesCommand : CliktCommand(name = "list-patches", help = "List available patches") {
    override fun run() {
        val registry = createRegistry()
        echo("Available patches:")
        echo("")
        registry.getAll().forEach { patch ->
            echo("  ${patch.id}")
            echo("    Name: ${patch.name}")
            echo("    Description: ${patch.description}")
            echo("    Packages: ${patch.supportedPackages.joinToString()}")
            echo("")
        }
    }
}

class PatchCommand : CliktCommand(name = "patch", help = "Patch an APK") {
    private val input by argument("input.apk").file(mustExist = true, canBeDir = false)
    private val output by option("-o", "--output", help = "Output APK path").required()
    private val enable by option("--enable", help = "Enable specific patch").multiple()

    override fun run() {
        val logger = DefaultPatchLogger()
        val pipeline = Pipeline(logger)
        val registry = createRegistry()

        val enabledSet = enable.toSet()
        val outputFile = File(output)

        val result = pipeline.run(input, outputFile, registry, enabledSet)

        if (!result.success) {
            echo("Error: ${result.message}", err = true)
            throw com.github.ajalt.clikt.core.ProgramResult(1)
        }
    }
}

fun createRegistry(): PatchRegistry {
    return PatchRegistry().apply {
        register(TestPatch())
        register(AdBlockPatch())
        register(DpadFixPatch())
        register(FocusFixPatch())
        register(TvInterfacePatch())
        register(PlaybackFixPatch())
    }
}

fun main(args: Array<String>) {
    YttvPatcher().subcommands(InfoCommand(), ListPatchesCommand(), PatchCommand()).main(args)
}
