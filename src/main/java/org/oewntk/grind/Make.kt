/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.grind

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import org.oewntk.grind.Args.SerializationMode
import org.oewntk.grind.Args.jsonMethodArg
import org.oewntk.grind.Args.serializationModeArg
import org.oewntk.grind.Tracing.progress
import org.oewntk.grind.Tracing.start
import org.oewntk.grind.Utils.getModel
import org.oewntk.json.out.JsonMethod
import org.oewntk.model.ModelInfo
import java.io.File

/**
 * Main class that invokes suppliers to make models
 *
 * @author Bernard Bou
 * @see "https://sqlunet.sourceforge.net/schema.html"
 */
object Make {

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
        val parser = ArgParser("make")
        // Options (start with - or --)
        // @formatter:off
        val in1 by parser.argument(            ArgType.String,                                                           description = "Input dir or file")
        val in2 by parser.option(              ArgType.String,        shortName = "i2", fullName = "in2",                description = "Extra input dir or file")         .default("")
        val inFormat by parser.option(         ArgType.String,        shortName = "if", fullName = "in_format",          description = "In format")                       .default("yaml")
        val inSerialization by parser.option(  serializationModeArg,  shortName = "is", fullName = "in_serialization",   description = "Serialization mode")              .default(SerializationMode.OEWN)
        val inJson by parser.option(           jsonMethodArg,         shortName = "ij", fullName = "in_json",            description = "JSON input method")               .default(JsonMethod.ANY_SERIALIZER)
        val inOne by parser.option(            ArgType.Boolean,       shortName = "i1", fullName = "in_one",             description = "Input one file")                  .default(false)
        val inPlus by parser.option(           ArgType.Boolean,       shortName = "p",  fullName = "plus",               description = "Plus input")                      .default(false)
        val verbose by parser.option(          ArgType.Boolean,       shortName = "v",  fullName = "verbose",            description = "Verbose output")                  .default(false)

        val traceTime by parser.option(        ArgType.Boolean,       shortName = "tt", fullName = "trace:time",         description = "trace time")                      .default(false)
        val traceHeap by parser.option(        ArgType.Boolean,       shortName = "th", fullName = "trace:heap",         description = "trace heap")                      .default(false)
        // @formatter:on
        parser.parse(args)
        if (verbose) {
            System.err.println("in: $in1")
            System.err.println("in2: $in2")
            System.err.println("plus: $inPlus")
            System.err.println("in format: $inFormat")
            System.err.println("in serialization: $inSerialization")
            System.err.println("in JSON: $inJson")
        }

        // Tracing
        Tracing.traceTime = traceTime
        Tracing.traceHeap = traceHeap

        val startTime = start()

        // Inputs
        Tracing.psInfo.println("[Input] " + File(in1).absolutePath)
        if (!in2.isBlank())
            Tracing.psInfo.println("[Input2] " + File(in2).absolutePath)

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

        // End
        progress("end, ", startTime, verbose = verbose)

        // info
        val modelInfo = model.info()
        val modelCounts = ModelInfo.counts(model)
        val modelInfo2 = "$modelInfo\n$modelCounts"
        Tracing.psInfo.println(modelInfo2)
    }
}
