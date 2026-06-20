/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.tool

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import org.oewntk.json.out.JsonMethod
import org.oewntk.model.ModelEquals.checkDiffs
import org.oewntk.model.ModelEquals.dataEquals
import org.oewntk.model.ModelInfo
import org.oewntk.model.SerializationMode
import org.oewntk.tool.Args.Format
import org.oewntk.tool.Args.formatArg
import org.oewntk.tool.Args.jsonMethodArg
import org.oewntk.tool.Args.serializationModeArg
import org.oewntk.tool.Diffs.diff
import org.oewntk.tool.Diffs.findDiffs
import org.oewntk.tool.Tracing.progress
import org.oewntk.tool.Tracing.start
import org.oewntk.tool.Utils.getModel
import java.io.File
import java.io.PrintStream
import kotlin.system.exitProcess

/**
 * Main class that compares models
 *
 * @author Bernard Bou
 * @see "https://sqlunet.sourceforge.net/schema.html"
 */
object Compare {

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
        val parser = ArgParser("compare")
        // Options (start with - or --)
        // @formatter:off
        val inA1 by parser.argument(            ArgType.String,                                                             description = "A Input dir or file")
        val inA2 by parser.option(              ArgType.String,        shortName = "Ai2", fullName = "a_in2",               description = "A Extra input dir or file")     .default("")
        val inAFormat by parser.option(         formatArg,             shortName = "Aif", fullName = "a_in_format",         description = "A In format")                   .default(Format.YAML)
        val inASerialization by parser.option(  serializationModeArg,  shortName = "Ais", fullName = "a_in_serialization",  description = "A Serialization mode")          .default(SerializationMode.OEWN)
        val inAJson by parser.option(           jsonMethodArg,         shortName = "Aij", fullName = "a_in_json",           description = "A JSON input method")           .default(JsonMethod.ANY_SERIALIZER)
        val inAOne by parser.option(            ArgType.Boolean,       shortName = "Ai1", fullName = "a_in_one",            description = "Input one file")                .default(false)
        val inAPlus by parser.option(           ArgType.Boolean,       shortName = "Ap",  fullName = "a_plus",              description = "A Plus input")                  .default(false)

        val inB1 by parser.argument(            ArgType.String,                                                             description = "B Input dir or file")
        val inB2 by parser.option(              ArgType.String,        shortName = "Bi2", fullName = "b_in2",               description = "B Extra input dir or file")     .default("")
        val inBFormat by parser.option(         formatArg,             shortName = "Bif", fullName = "b_in_format",         description = "B In format")                   .default(Format.YAML)
        val inBSerialization by parser.option(  serializationModeArg,  shortName = "Bis", fullName = "b_in_serialization",  description = "B Serialization mode")          .default(SerializationMode.OEWN)
        val inBJson by parser.option(           jsonMethodArg,         shortName = "Bij", fullName = "b_in_json",           description = "B JSON input method")           .default(JsonMethod.ANY_SERIALIZER)
        val inBOne by parser.option(            ArgType.Boolean,       shortName = "Bi1", fullName = "b_in_one",            description = "Input one file")                .default(false)
        val inBPlus by parser.option(           ArgType.Boolean,       shortName = "Bp",  fullName = "b_plus",              description = "B Plus input")                  .default(false)

        val verbose by parser.option(           ArgType.Boolean,       shortName = "v",  fullName = "verbose",              description = "Verbose output")                .default(false)

        val traceTime by parser.option(         ArgType.Boolean,       shortName = "tt", fullName = "trace:time",           description = "trace time")                    .default(false)
        val traceHeap by parser.option(         ArgType.Boolean,       shortName = "th", fullName = "trace:heap",           description = "trace heap")                    .default(false)
        // @formatter:on
        parser.parse(args)
        if (verbose) {
            System.err.println("A in: $inA1")
            System.err.println("A in2: $inA2")
            System.err.println("A plus: $inAPlus")
            System.err.println("A in format: $inAFormat")
            System.err.println("A in serialization: $inASerialization")
            System.err.println("A in JSON: $inAJson")

            System.err.println("B in: $inB1")
            System.err.println("B in2: $inB2")
            System.err.println("B plus: $inBPlus")
            System.err.println("B in format: $inBFormat")
            System.err.println("B in serialization: $inBSerialization")
            System.err.println("B in JSON: $inBJson")
        }

        // Tracing
        Tracing.traceTime = traceTime
        Tracing.traceHeap = traceHeap

        val startTime = start()

        // Supply model
        progress("before model are supplied", startTime, verbose = verbose)
        progress("before model A is supplied", startTime, verbose = verbose)
        Tracing.psInfo.println("[Input A] " + File(inA1).absolutePath)
        if (!inA2.isBlank())
            Tracing.psInfo.println("[Input2 A] " + File(inA2).absolutePath)
        val modelA = getModel(
            inA1,
            inA2,
            inAFormat,
            inAPlus,
            inASerialization,
            inAOne,
            inAJson,
            verbose
        )
        progress("after model A $modelA is supplied", startTime, verbose = verbose)
        progress("before model B is supplied", startTime, verbose = verbose)
        Tracing.psInfo.println("[Input B] " + File(inB1).absolutePath)
        if (!inB2.isBlank())
            Tracing.psInfo.println("[Input2 B] " + File(inB2).absolutePath)
        val modelB = getModel(
            inB1,
            inB2,
            inBFormat,
            inBPlus,
            inBSerialization,
            inBOne,
            inBJson,
            verbose
        )
        progress("after model B $modelB is supplied", startTime, verbose = verbose)
        progress("after models are supplied", startTime, verbose = verbose)

        // Consume models
        progress("before models are consumed", startTime, verbose = verbose)

        // info

        val modelInfoA = modelA.info()
        val modelCountsA = ModelInfo.counts(modelA)
        val modelRelationsA = ModelInfo.relations(modelA)
        val modelInfo2A = "$modelInfoA\n$modelCountsA\n$modelRelationsA"
        Tracing.psInfo.println("Model A $modelA")
        //Tracing.psInfo.println(modelInfo2A)

        val modelInfoB = modelB.info()
        val modelCountsB = ModelInfo.counts(modelB)
        val modelRelationsB = ModelInfo.relations(modelB)
        val modelInfo2B = "$modelInfoB\n$modelCountsB\n$modelRelationsB"
        Tracing.psInfo.println("Model B $modelB")
        //Tracing.psInfo.println(modelInfo2B)

        if (modelInfo2A != modelInfo2B) {
            Tracing.psErr.println("[E] Model A $modelA and B $modelB don't have the same info")
            Tracing.psInfo.println(modelInfo2A)
            Tracing.psInfo.println(modelInfo2B)
            Tracing.psInfo.println("diff:")
            val diff = diff(modelInfo2A, modelInfo2B)
            Tracing.psInfo.println(diff)
        } else Tracing.psInfo.println("[I] Model A and B have the same info")

        val areEqual = modelA == modelB
        val ret = if (!areEqual) {
            Tracing.psErr.println("[E] Model A $modelA and B $modelB are not equal")
            val dataEq = modelA.dataEquals(modelB)
            if (!dataEq) Tracing.psErr.println("[E] Model A $modelA and B $modelB are not data equal")

            checkDiffs(modelA, modelB)
            PrintStream(File("diff_@${modelA.id}_@${modelB.id}.log")).use { ps ->
                findDiffs(modelA, modelB, ps = ps)
            }
            1
        } else {
            Tracing.psInfo.println("[I] Model A and B are equal")
            0
        }

        // End
        progress("end $ret", startTime, verbose = verbose)
        exitProcess(ret)
    }
}
