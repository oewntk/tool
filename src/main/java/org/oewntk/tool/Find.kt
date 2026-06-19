/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.tool

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.multiple
import org.oewntk.json.out.JsonMethod
import org.oewntk.model.CoreModel
import org.oewntk.model.SerializationMode
import org.oewntk.tool.Args.Format
import org.oewntk.tool.Args.YamlDumpMode
import org.oewntk.tool.Args.formatArg
import org.oewntk.tool.Args.jsonMethodArg
import org.oewntk.tool.Args.serializationModeArg
import org.oewntk.tool.Args.yamlDumpModeArg
import org.oewntk.tool.Tracing.progress
import org.oewntk.tool.Tracing.start
import org.oewntk.tool.Utils.getModel
import java.io.File
import org.oewntk.json.out.ObjectConsumer as JsonObjectConsumer
import org.oewntk.yaml.out.ObjectConsumer as YamlObjectConsumer

/**
 * Main class that chains suppliers and consumers
 *
 * @author Bernard Bou
 * @see "https://sqlunet.sourceforge.net/schema.html"
 */
object Find {

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
        val in2 by parser.option(              ArgType.String,        shortName = "i2", fullName = "in2",                description = "Extra input dir or file")         .default("")
        val inFormat by parser.option(         formatArg,             shortName = "if", fullName = "in_format",          description = "In format")                       .default(Format.YAML)
        val inSerialization by parser.option(  serializationModeArg,  shortName = "is", fullName = "in_serialization",   description = "Serialization mode")              .default(SerializationMode.OEWN)
        val inJson by parser.option(           jsonMethodArg,         shortName = "ij", fullName = "in_json",            description = "JSON input method")               .default(JsonMethod.ANY_SERIALIZER)
        val inOne by parser.option(            ArgType.Boolean,       shortName = "i1", fullName = "in_one",             description = "Input one file")                  .default(false)
        val inPlus by parser.option(           ArgType.Boolean,       shortName = "p",  fullName = "plus",               description = "Plus input")                      .default(false)
        val outFormat by parser.option(        formatArg,             shortName = "of", fullName = "out_format",         description = "Output format")                   .default(Format.JSON)
        val out2 by parser.option(             ArgType.String,        shortName = "o2", fullName = "out2",               description = "Extra output dir or file")        .default("")
        val outOne by parser.option(           ArgType.Boolean,       shortName = "o1", fullName = "out_one",            description = "Output one file")                 .default(false)
        val outMerge by parser.option(         ArgType.Boolean,       shortName = "om", fullName = "out_merge",          description = "Do not group generated entries")  .default(false)
        val outSerialization by parser.option( serializationModeArg,  shortName = "os", fullName = "out_serialization",  description = "Serialization mode")              .default(SerializationMode.OEWN)
        val outYaml by parser.option(          yamlDumpModeArg,       shortName = "oy", fullName = "out_yaml",           description = "YAML output format")              .default(YamlDumpMode.AUTO)
        val outJson by parser.option(          jsonMethodArg,         shortName = "oj", fullName = "out_json",           description = "JSON output method")              .default(JsonMethod.ANY_SERIALIZER)
        val outPretty by parser.option(        ArgType.Boolean,       shortName = "op", fullName = "out_pretty",         description = "JSON pretty print")               .default(true)
        val senses by parser.option(           ArgType.String,        shortName = "s",  fullName = "sense",              description = "Sense ID").multiple()
        val synsets by parser.option(          ArgType.String,        shortName = "y",  fullName = "synset",             description = "Synset ID").multiple()
        val lexes by parser.option(            ArgType.String,        shortName = "l",  fullName = "lex",                description = "Lex ID").multiple()
        val verbose by parser.option(          ArgType.Boolean,       shortName = "v",  fullName = "verbose",            description = "Verbose output")                  .default(false)

        val traceTime by parser.option(        ArgType.Boolean,       shortName = "tt", fullName = "trace:time",         description = "trace time")                      .default(false)
        val traceHeap by parser.option(        ArgType.Boolean,       shortName = "th", fullName = "trace:heap",         description = "trace heap")                      .default(false)
        // @formatter:on
        parser.parse(args)
        if (verbose) {
            System.err.println("in: $in1")
            System.err.println("in2: $in2")
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
            System.err.println("lexes: $lexes")
            System.err.println("synsets: $synsets")
            System.err.println("senses: $senses")
        }

        // Tracing
        Tracing.traceTime = traceTime
        Tracing.traceHeap = traceHeap

        val startTime = start()

        // Inputs
        if (verbose) Tracing.psInfo.println("[Input] " + File(in1).absolutePath)
        if (!in2.isBlank())
            if (verbose) Tracing.psInfo.println("[Input2] " + File(in2).absolutePath)

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

        // Consume model
        progress("before model is consumed", startTime, verbose = verbose)

        lexes.forEach {
            val lex = model.lexFinder(it)
            //lex?.dump(model, outFormat, outSerialization, outJson, outYaml)
        }
        synsets.forEach {
            val synset = model.synsetFinder(it)
            synset?.dump(model, outFormat, outSerialization, outJson, outYaml)
        }
        senses.forEach {
            val sense = model.senseFinder(it)
            sense?.dump(model, outFormat, outSerialization, outJson, outYaml, outPretty)
        }

        progress("after model is consumed", startTime, verbose = verbose)

        // End
        progress("end, ", startTime, verbose = verbose)
    }

    fun Any.dump(model: CoreModel, outFormat: Format, outSerialization: SerializationMode, jsonMethod: JsonMethod, outYaml: YamlDumpMode, prettyPrint: Boolean = true) {
        when (outFormat) {
            Format.YAML -> YamlObjectConsumer(mode = outSerialization, dumperOptions = outYaml.options, ps = System.out).accept(this, model)
            Format.JSON -> JsonObjectConsumer(mode = outSerialization, jsonMethod = jsonMethod, prettyPrint = prettyPrint, ps = System.out).accept(this, model)
            else -> throw IllegalArgumentException("Unsupported output format")
        }
    }
}
