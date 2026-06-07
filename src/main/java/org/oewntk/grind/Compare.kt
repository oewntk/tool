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
import org.oewntk.json.out.JsonMethod
import org.oewntk.model.DataCoreModel
import org.oewntk.model.ModelInfo
import org.oewntk.yaml.`in`.FactoryPlus
import java.io.File
import org.oewntk.json.`in`.data.Factory as DataJsonFactory
import org.oewntk.json.`in`.model.Factory as ModelJsonFactory
import org.oewntk.json.`in`.oewn.Factory as OEWNJsonFactory
import org.oewntk.ser.`in`.Factory as SerFactory
import org.oewntk.wndb.`in`.Factory as WndbFactory
import org.oewntk.xml.`in`.Factory as XmlFactory
import org.oewntk.yaml.`in`.Factory as YamlFactory

/**
 * Main class that generates the OEWN plus database
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
        val parser = ArgParser("grind")
        // Options (start with - or --)
        // @formatter:off
        val inA1 by parser.argument(            ArgType.String,                                                             description = "A Input dir or file")
        val inA2 by parser.option(              ArgType.String,        shortName = "Ai2", fullName = "a_in2",               description = "A Extra input dir or file")     .default("")
        val inAFormat by parser.option(         ArgType.String,        shortName = "Aif", fullName = "a_in_format",         description = "A In format")                   .default("yaml")
        val inASerialization by parser.option(  serializationModeArg,  shortName = "Ais", fullName = "a_in_serialization",  description = "A Serialization mode")          .default(SerializationMode.OEWN)
        val inAJson by parser.option(           jsonMethodArg,         shortName = "Aij", fullName = "a_in_json",           description = "A JSON input method")           .default(JsonMethod.ANY_SERIALIZER)
        val inAOne by parser.option(            ArgType.Boolean,       shortName = "Ai1", fullName = "a_in_one",            description = "Input one file")                .default(false)
        val inAPlus by parser.option(           ArgType.Boolean,       shortName = "Ap",  fullName = "a_plus",              description = "A Plus input")                  .default(false)

        val inB1 by parser.argument(            ArgType.String,                                                             description = "B Input dir or file")
        val inB2 by parser.option(              ArgType.String,        shortName = "Bi2", fullName = "b_in2",               description = "B Extra input dir or file")     .default("")
        val inBFormat by parser.option(         ArgType.String,        shortName = "Bif", fullName = "b_in_format",         description = "B In format")                   .default("yaml")
        val inBSerialization by parser.option(  serializationModeArg,  shortName = "Bis", fullName = "b_in_serialization",  description = "B Serialization mode")          .default(SerializationMode.OEWN)
        val inBJson by parser.option(           jsonMethodArg,         shortName = "Bij", fullName = "b_in_json",           description = "B JSON input method")           .default(JsonMethod.ANY_SERIALIZER)
        val inBOne by parser.option(            ArgType.Boolean,       shortName = "Bi1", fullName = "b_in_one",            description = "Input one file")                .default(false)
        val inBPlus by parser.option(           ArgType.Boolean,       shortName = "Bp",  fullName = "b_plus",              description = "B Plus input")                  .default(false)

        val verbose by parser.option(           ArgType.Boolean,       shortName = "v",  fullName = "verbose",              description = "Verbose output")                .default(false)

        val traceTime by parser.option(        ArgType.Boolean,        shortName = "tt", fullName = "trace:time",           description = "trace time")                    .default(false)
        val traceHeap by parser.option(        ArgType.Boolean,        shortName = "th", fullName = "trace:heap",           description = "trace heap")                    .default(false)
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

        // Input
        val inputA = File(inA1)
        Tracing.psInfo.println("[Input A] " + inputA.absolutePath)
        val inputB = File(inB1)
        Tracing.psInfo.println("[Input B] " + inputB.absolutePath)

        // Input2
        val input2A = File(inA2)
        Tracing.psInfo.println("[Input2 A] " + input2A.absolutePath)
        val input2B = File(inB2)
        Tracing.psInfo.println("[Input2 B] " + input2B.absolutePath)

        // Supply model
        progress("before model are supplied,", startTime, verbose = verbose)
        progress("before model A is supplied,", startTime, verbose = verbose)
        val modelA = if (inAPlus)
            FactoryPlus(inputA, input2A).get()!!
        else when (inAFormat) {
            "ser" -> SerFactory(inputA).get()!!
            "yaml" -> YamlFactory(inputA, input2A, verbose = verbose).get()!!
            "xml" -> XmlFactory(inputA, input2A, verbose = verbose).get()!!
            "wndb" -> WndbFactory(inputA, input2A, verbose = verbose).get()!!
            "json" -> {
                when (inASerialization) {
                    SerializationMode.OEWN -> OEWNJsonFactory(inputA, split = !inAOne, jsonMethod = inAJson, verbose = verbose).get()!!
                    SerializationMode.DATA -> DataJsonFactory(inputA, split = !inAOne, jsonMethod = inAJson, verbose = verbose).get()!!
                    SerializationMode.MODEL -> ModelJsonFactory(inputA, verbose = verbose).get()!!
                }
            }

            else -> throw IllegalArgumentException("Unsupported A input format")
        }
        progress("after model A $modelA is supplied,", startTime, verbose = verbose)
        progress("before model B is supplied,", startTime, verbose = verbose)
        val modelB = if (inBPlus)
            FactoryPlus(inputB, input2B).get()!!
        else when (inBFormat) {
            "ser" -> SerFactory(inputB).get()!!
            "yaml" -> YamlFactory(inputB, input2B, verbose = verbose).get()!!
            "xml" -> XmlFactory(inputB, input2B, verbose = verbose).get()!!
            "wndb" -> WndbFactory(inputB, input2B, verbose = verbose).get()!!
            "json" -> {
                when (inBSerialization) {
                    SerializationMode.OEWN -> OEWNJsonFactory(inputB, split = !inBOne, jsonMethod = inBJson, verbose = verbose).get()!!
                    SerializationMode.DATA -> DataJsonFactory(inputB, split = !inBOne, jsonMethod = inBJson, verbose = verbose).get()!!
                    SerializationMode.MODEL -> ModelJsonFactory(inputB, verbose = verbose).get()!!
                }
            }

            else -> throw IllegalArgumentException("Unsupported B input format")
        }
        progress("after model B $modelB is supplied,", startTime, verbose = verbose)
        progress("after models are supplied,", startTime, verbose = verbose)

        // Consume models
        progress("before models are consumed,", startTime, verbose = verbose)

        // info
        val modelInfoA = modelA.info()
        val modelCountsA = ModelInfo.counts(modelA)
        val modelInfo2A = "$modelInfoA\n$modelCountsA"
        Tracing.psInfo.println(modelInfo2A)

        val modelInfoB = modelB.info()
        val modelCountsB = ModelInfo.counts(modelB)
        val modelInfo2B = "$modelInfoB\n$modelCountsB"
        Tracing.psInfo.println(modelInfo2B)

        if (modelInfo2A != modelInfo2B) Tracing.psErr.println("Model A $modelA and B $modelB don't have the same info")
        else Tracing.psInfo.println("Model A $modelA and B $modelB have the same info")

        val dataA = DataCoreModel(modelA)
        val dataB = DataCoreModel(modelB)
        val equal = dataA == dataB
        if (equal) Tracing.psErr.println("Model A $modelA and B $modelB are not equal")
        else Tracing.psInfo.println("Model A $modelA and B $modelB are equal")

        // End
        progress("end,", startTime, verbose = verbose)
    }
}
