/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.grind

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import org.oewntk.grind.Tracing.progress
import org.oewntk.grind.Tracing.start
import org.oewntk.model.ModelInfo
import org.oewntk.wndb.out.Flags
import org.oewntk.yaml.`in`.FactoryPlus
import org.oewntk.yaml.out.YamlDump
import org.yaml.snakeyaml.DumperOptions
import java.io.File
import org.oewntk.json.out.data.ModelConsumer as DataJsonModelConsumer
import org.oewntk.json.out.model.ModelConsumer as ModelJsonModelConsumer
import org.oewntk.json.out.oewn.ModelConsumer as OEWNJsonModelConsumer
import org.oewntk.ser.`in`.Factory as SerFactory
import org.oewntk.ser.out.ModelConsumer as SerModelConsumer
import org.oewntk.sql.out.ModelConsumer as SqlModelConsumer
import org.oewntk.wndb.`in`.Factory as WndbFactory
import org.oewntk.wndb.out.ModelConsumer as WndbModelConsumer
import org.oewntk.xml.`in`.Factory as XmlFactory
import org.oewntk.yaml.`in`.Factory as YamlFactory
import org.oewntk.yaml.out.data.ModelConsumer as DataYamlModelConsumer
import org.oewntk.yaml.out.oewn.ModelConsumer as OEWNYamlModelConsumer

/**
 * Main class that generates the OEWN plus database
 *
 * @author Bernard Bou
 * @see "https://sqlunet.sourceforge.net/schema.html"
 */
object Grind {

    enum class YamlDumpMode(val options: DumperOptions) {
        AUTO(YamlDump.autoDumperOptions),
        BLOCK(YamlDump.blockDumperOptions),
        FLOW(YamlDump.flowDumperOptions),

        DEFAULT(YamlDump.defaultDumperOptions),
        COMPAT(YamlDump.jsonDumperOptions),
        JSON(YamlDump.jsonDumperOptions),
    }

    val yamlDumpModeArg = ArgType.Choice(
        choices = YamlDumpMode.entries,
        variantToString = { it.name.lowercase() },
        toVariant = { raw ->
            when (raw.lowercase()) {
                "a", "auto" -> YamlDumpMode.BLOCK
                "b", "block" -> YamlDumpMode.BLOCK
                "f", "flow" -> YamlDumpMode.FLOW
                "j", "json" -> YamlDumpMode.JSON
                "d", "default" -> YamlDumpMode.DEFAULT
                "c", "compat" -> YamlDumpMode.COMPAT
                else -> error("Unknown yaml dump mode: $raw")
            }
        }
    )

    enum class SerializationMode {
        OEWN,
        DATA,
        MODEL
    }

    val serializationModeArg = ArgType.Choice(
        choices = SerializationMode.entries,
        variantToString = { it.name.lowercase() },
        toVariant = { raw ->
            when (raw.lowercase()) {
                "o", "oewn" -> SerializationMode.OEWN
                "d", "data" -> SerializationMode.DATA
                "m", "model" -> SerializationMode.MODEL
                else -> error("Unknown mode: $raw")
            }
        }
    )

    /**
     * Main entry point
     *
     * @param args command-line arguments
     * ```
     * yamlDir [outputDir]
     * ```
     */
    @JvmStatic
    fun main(args: Array<String>) {
        val parser = ArgParser("grind")

        // Options (start with - or --)
        // @formatter:off
        val in1 by parser.argument(            ArgType.String,                                                         description = "Input dir or file")
        val out by parser.argument(            ArgType.String,                                                         description = "Output dir or file")
        val in2 by parser.option(              ArgType.String,        shortName = "i2", fullName = "in2",              description = "Extra input dir or file")         .default("")
        val inFormat by parser.option(         ArgType.String,        shortName = "if", fullName = "in_format",        description = "In format")                       .default("yaml")
        val inPlus by parser.option(           ArgType.Boolean,       shortName = "p",  fullName = "plus",             description = "Plus input")                      .default(false)
        val outFormat by parser.option(        ArgType.String,        shortName = "of", fullName = "out_format",       description = "Output format")                   .default("yaml")
        val out2 by parser.option(             ArgType.String,        shortName = "o2", fullName = "out2",             description = "Extra output dir or file")        .default("")
        val outOne by parser.option(           ArgType.Boolean,       shortName = "o1", fullName = "out_one",          description = "Output one file")                 .default(false)
        val outMerge by parser.option(         ArgType.Boolean,       shortName = "m",  fullName = "merge",            description = "Do not group generated entries")  .default(false)
        val outYaml by parser.option(          yamlDumpModeArg,       shortName = "y",  fullName = "yaml",             description = "YAML format")                     .default(YamlDumpMode.AUTO)
        val outPretty by parser.option(        ArgType.Boolean,       shortName = "op", fullName = "pretty",           description = "JSON pretty print")               .default(true)
        val outSerialization by parser.option( serializationModeArg,  shortName = "os", fullName = "serialization",    description = "Serialization mode")              .default(SerializationMode.OEWN)
        val verbose by parser.option(          ArgType.Boolean,       shortName = "v",  fullName = "verbose",          description = "Verbose output")                  .default(false)

        val wndCompatPointers by parser.option(ArgType.Boolean,       shortName = "wp", fullName = "compat:pointer",   description = "WNDB pointer compat")             .default(false)
        val wndCompatLexId by parser.option(   ArgType.Boolean,       shortName = "wl", fullName = "compat:lexid",     description = "WNDB lexid compat")               .default(false)
        val wndCompatVFrames by parser.option( ArgType.Boolean,       shortName = "wv", fullName = "compat:verbframe", description = "WNDB vframe compat")              .default(false)

        val traceTime by parser.option(        ArgType.Boolean,       shortName = "tt", fullName = "trace:time",       description = "trace time")                      .default(false)
        val traceHeap by parser.option(        ArgType.Boolean,       shortName = "th", fullName = "trace:heap",       description = "trace heap")                      .default(false)
        // @formatter:on
        parser.parse(args)
        if (verbose) {
            System.err.println("in: $in1")
            System.err.println("in2: $in2")
            System.err.println("out: $out")
            System.err.println("out2: $out2")
            System.err.println("plus: $inPlus")
            System.err.println("in format: $inFormat")
            System.err.println("out format: $outFormat")
            System.err.println("out merge: $outMerge")
            System.err.println("out one: $outOne")
            System.err.println("out serialization: $outSerialization")
        }

        // Tracing
        Tracing.traceTime = traceTime
        Tracing.traceHeap = traceHeap

        val startTime = start()

        // Input
        val input = File(in1)
        Tracing.psInfo.println("[Input] " + input.absolutePath)

        // Input2
        val input2 = File(in2)
        Tracing.psInfo.println("[Input2] " + input2.absolutePath)

        // Output
        val outFile = File(out)
        if (outFile.exists() && !outFile.isDirectory) {
            outFile.delete()
        }
        Tracing.psInfo.println("[Output] " + outFile.absolutePath)

        // Supply model
        progress("before model is supplied,", startTime)
        val model = if (inPlus)
            FactoryPlus(input, input2).get()!!
        else when (inFormat) {
            "ser" -> SerFactory(input).get()!!
            "yaml" -> YamlFactory(input, input2, verbose = verbose).get()!!
            "xml" -> XmlFactory(input, input2, verbose = verbose).get()!!
            "wndb" -> WndbFactory(input, input2, verbose = verbose).get()!!
            else -> throw IllegalArgumentException("Unsupported input format")
        }
        progress("after model is supplied,", startTime)

        // Consume model
        progress("before model is consumed,", startTime)

        when (outFormat) {
            "ser" -> SerModelConsumer(outFile).accept(model)
            "sql" -> SqlModelConsumer(outFile).accept(model)
            "wndb" -> WndbModelConsumer(outFile, wndbFlags(wndCompatPointers, wndCompatLexId, wndCompatVFrames)).accept(model)
            "yaml" -> {
                if (outMerge)
                    File(outFile, "entries-generated.yaml").delete()
                when (outSerialization) {
                    SerializationMode.OEWN -> OEWNYamlModelConsumer(outFile, split = !outOne, dumperOptions = outYaml.options, generated = !outMerge, verbose = verbose).accept(model)
                    SerializationMode.DATA -> DataYamlModelConsumer(outFile, split = !outOne, dumperOptions = outYaml.options, verbose = verbose).accept(model)
                    else -> throw IllegalArgumentException("Unsupported output format")
                }
            }

            "json" -> {
                when (outSerialization) {
                    SerializationMode.OEWN -> OEWNJsonModelConsumer(outFile, split = !outOne, prettyPrint = outPretty, generated = !outMerge, verbose = verbose).accept(model)
                    SerializationMode.DATA -> DataJsonModelConsumer(outFile, split = !outOne, prettyPrint = outPretty, verbose = verbose).accept(model)
                    SerializationMode.MODEL -> ModelJsonModelConsumer(outFile, prettyPrint = outPretty, verbose = verbose).accept(model)
                }
            }

            else -> throw IllegalArgumentException("Unsupported output format")
        }
        progress("after model is consumed,", startTime)

        // End
        progress("total,", startTime)

        // info
        val modelInfo = model.info()
        val modelCounts = ModelInfo.counts(model)
        val modelInfo2 = "$modelInfo\n$modelCounts"
        Tracing.psInfo.println(modelInfo2)
        if (out2.isNotEmpty()) {
            val file = File(out2)
            val outDir = file.parentFile
            if (!outDir.exists()) {
                outDir.mkdirs()
            }
            file.writeText(modelInfo2)
        }
    }

    private fun wndbFlags(wndCompatPointers: Boolean, wndCompatLexId: Boolean, wndCompatVFrames: Boolean): Int {
        var flags = 0
        if (wndCompatPointers)
            flags = flags or Flags.POINTER_COMPAT
        if (wndCompatLexId)
            flags = flags or Flags.LEXID_COMPAT
        if (wndCompatVFrames)
            flags = flags or Flags.VERBFRAME_COMPAT
        return flags
    }
}
