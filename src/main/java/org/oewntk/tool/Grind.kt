/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.tool

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import org.oewntk.tool.Args.SerializationMode
import org.oewntk.tool.Args.YamlDumpMode
import org.oewntk.tool.Args.jsonMethodArg
import org.oewntk.tool.Args.serializationModeArg
import org.oewntk.tool.Args.yamlDumpModeArg
import org.oewntk.tool.Tracing.progress
import org.oewntk.tool.Tracing.start
import org.oewntk.tool.Utils.getModel
import org.oewntk.json.out.JsonMethod
import org.oewntk.model.ModelInfo
import org.oewntk.tool.Args.formatArg
import org.oewntk.wndb.out.Flags
import java.io.File
import org.oewntk.json.out.data.ModelConsumer as DataJsonModelConsumer
import org.oewntk.json.out.model.ModelConsumer as ModelJsonModelConsumer
import org.oewntk.json.out.oewn.ModelConsumer as OEWNJsonModelConsumer
import org.oewntk.ser.out.ModelConsumer as SerModelConsumer
import org.oewntk.sql.out.ModelConsumer as SqlModelConsumer
import org.oewntk.wndb.out.ModelConsumer as WndbModelConsumer
import org.oewntk.yaml.out.data.ModelConsumer as DataYamlModelConsumer
import org.oewntk.yaml.out.oewn.ModelConsumer as OEWNYamlModelConsumer

/**
 * Main class that chains suppliers and consumers
 *
 * @author Bernard Bou
 * @see "https://sqlunet.sourceforge.net/schema.html"
 */
object Grind {

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
        val in1 by parser.argument(            ArgType.String,                                                           description = "Input dir or file")
        val out by parser.argument(            ArgType.String,                                                           description = "Output dir or file")
        val in2 by parser.option(              ArgType.String,        shortName = "i2", fullName = "in2",                description = "Extra input dir or file")         .default("")
        val inFormat by parser.option(         formatArg,             shortName = "if", fullName = "in_format",          description = "In format")                       .default("yaml")
        val inSerialization by parser.option(  serializationModeArg,  shortName = "is", fullName = "in_serialization",   description = "Serialization mode")              .default(SerializationMode.OEWN)
        val inJson by parser.option(           jsonMethodArg,         shortName = "ij", fullName = "in_json",            description = "JSON input method")               .default(JsonMethod.ANY_SERIALIZER)
        val inOne by parser.option(            ArgType.Boolean,       shortName = "i1", fullName = "in_one",             description = "Input one file")                  .default(false)
        val inPlus by parser.option(           ArgType.Boolean,       shortName = "p",  fullName = "plus",               description = "Plus input")                      .default(false)
        val outNone by parser.option(          ArgType.Boolean,       shortName = "on", fullName = "out_none",           description = "No output")                       .default(false)
        val outFormat by parser.option(        formatArg,             shortName = "of", fullName = "out_format",         description = "Output format")                   .default("yaml")
        val out2 by parser.option(             ArgType.String,        shortName = "o2", fullName = "out2",               description = "Extra output dir or file")        .default("")
        val outOne by parser.option(           ArgType.Boolean,       shortName = "o1", fullName = "out_one",            description = "Output one file")                 .default(false)
        val outMerge by parser.option(         ArgType.Boolean,       shortName = "om", fullName = "out_merge",          description = "Do not group generated entries")  .default(false)
        val outSerialization by parser.option( serializationModeArg,  shortName = "os", fullName = "out_serialization",  description = "Serialization mode")              .default(SerializationMode.OEWN)
        val outYaml by parser.option(          yamlDumpModeArg,       shortName = "oy", fullName = "out_yaml",           description = "YAML output format")              .default(YamlDumpMode.AUTO)
        val outJson by parser.option(          jsonMethodArg,         shortName = "oj", fullName = "out_json",           description = "JSON output method")              .default(JsonMethod.ANY_SERIALIZER)
        val outInfo by parser.option(          ArgType.String,        shortName = "oi", fullName = "out_info",           description = "Info output")                     .default("")
        val outPretty by parser.option(        ArgType.Boolean,       shortName = "op", fullName = "out_pretty",         description = "JSON pretty print")               .default(true)
        val verbose by parser.option(          ArgType.Boolean,       shortName = "v",  fullName = "verbose",            description = "Verbose output")                  .default(false)

        val wndCompatPointers by parser.option(ArgType.Boolean,       shortName = "wp", fullName = "compat:pointer",     description = "WNDB pointer compat")             .default(false)
        val wndCompatLexId by parser.option(   ArgType.Boolean,       shortName = "wl", fullName = "compat:lexid",       description = "WNDB lexid compat")               .default(false)
        val wndCompatVFrames by parser.option( ArgType.Boolean,       shortName = "wv", fullName = "compat:verbframe",   description = "WNDB vframe compat")              .default(false)

        val traceTime by parser.option(        ArgType.Boolean,       shortName = "tt", fullName = "trace:time",         description = "trace time")                      .default(false)
        val traceHeap by parser.option(        ArgType.Boolean,       shortName = "th", fullName = "trace:heap",         description = "trace heap")                      .default(false)
        // @formatter:on
        parser.parse(args)
        if (verbose) {
            System.err.println("in: $in1")
            System.err.println("in2: $in2")
            System.err.println("out: $out")
            System.err.println("out2: $out2")
            System.err.println("plus: $inPlus")
            System.err.println("in format: $inFormat")
            System.err.println("in serialization: $inSerialization")
            System.err.println("in JSON: $inJson")
            System.err.println("out format: $outFormat")
            System.err.println("out merge: $outMerge")
            System.err.println("out one: $outOne")
            System.err.println("out serialization: $outSerialization")
            System.err.println("out YAML: $outYaml")
            System.err.println("out JSON: $outJson")
        }

        // Tracing
        Tracing.traceTime = traceTime
        Tracing.traceHeap = traceHeap

        val startTime = start()

        // Inputs
        Tracing.psInfo.println("[Input] " + File(in1).absolutePath)
        if (!in2.isBlank())
            Tracing.psInfo.println("[Input2] " + File(in2).absolutePath)

        // Output
        val outFile = File(out)
        if (outFile.exists() && !outFile.isDirectory) {
            outFile.delete()
        }
        Tracing.psInfo.println("[Output] " + outFile.absolutePath)

        // Supply model
        progress("before model is supplied", startTime, verbose = verbose)
        val model = getModel(
            in1,
            in2,
            inFormat,
            inPlus,
            inSerialization,
            inOne,
            inJson,
            verbose
        )
        progress("after model is supplied", startTime, verbose = verbose)
        if (!outNone) {

            // Consume model
            progress("before model is consumed", startTime, verbose = verbose)

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
                        SerializationMode.OEWN -> OEWNJsonModelConsumer(outFile, split = !outOne, jsonMethod = outJson, prettyPrint = outPretty, generated = !outMerge, verbose = verbose).accept(model)
                        SerializationMode.DATA -> DataJsonModelConsumer(outFile, split = !outOne, jsonMethod = outJson, prettyPrint = outPretty, verbose = verbose).accept(model)
                        SerializationMode.MODEL -> ModelJsonModelConsumer(outFile, prettyPrint = outPretty, verbose = verbose).accept(model)
                    }
                }

                else -> throw IllegalArgumentException("Unsupported output format")
            }
            progress("after model is consumed", startTime, verbose = verbose)
        }

        // End
        progress("end, ", startTime, verbose = verbose)

        // Info
        val modelInfo = model.info()
        val modelCounts = ModelInfo.counts(model)
        val modelInfo2 = "$modelInfo\n$modelCounts"
        Tracing.psInfo.println(modelInfo2)
        if (outInfo.isNotEmpty()) {
            val file = File(outInfo)
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
