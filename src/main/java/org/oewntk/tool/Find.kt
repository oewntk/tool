/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.tool

import kotlinx.cli.*
import org.oewntk.json.out.JsonMethod
import org.oewntk.model.*
import org.oewntk.tool.Args.Format
import org.oewntk.tool.Args.YamlDumpMode
import org.oewntk.tool.Args.formatArg
import org.oewntk.tool.Args.jsonMethodArg
import org.oewntk.tool.Args.serializationModeArg
import org.oewntk.tool.Args.yamlDumpModeArg
import org.oewntk.tool.Tracing.progress
import org.oewntk.tool.Tracing.start
import org.oewntk.tool.Utils.getModel
import org.oewntk.tool.Utils.recog
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
        val parser = ArgParser("find")
        // Options (start with - or --)
        // @formatter:off
        val in1 by parser.argument(            ArgType.String,                                                           description = "Input dir or file")
        val objects by parser.argument(        ArgType.String,                                                           description = "Lex ID")                          .vararg()
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
        val senseIds by parser.option(         ArgType.String,        shortName = "s",  fullName = "sense",              description = "Sense ID").multiple()
        val synsetIds by parser.option(        ArgType.String,        shortName = "y",  fullName = "synset",             description = "Synset ID").multiple()
        val lexIds by parser.option(           ArgType.String,        shortName = "x",  fullName = "lex",                description = "Lex ID").multiple()
        val lemmas by parser.option(           ArgType.String,        shortName = "l",  fullName = "lemma",              description = "Lemma").multiple()
        val doNotThrow by parser.option(       ArgType.Boolean,       shortName = "nt", fullName = "no_throw",           description = "Do not throw")                    .default(false)
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
            System.err.println("lexes: $lexIds")
            System.err.println("synsets: $synsetIds")
            System.err.println("senses: $senseIds")
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
            false,
            throws = !doNotThrow,
            verbose,
        )
        progress("after model is supplied", startTime, verbose = verbose)

        // Consume model
        progress("before model is consumed", startTime, verbose = verbose)

        val lemmas2 = lemmas.toMutableList()
        val lexIds2 = lexIds.toMutableList()
        val synsetIds2 = synsetIds.toMutableList()
        val senseIds2 = senseIds.toMutableList()

        objects.forEach { obj ->
            when (recog(obj)) {
                Lemma::class -> lemmas2.add(obj)
                Lex::class -> lexIds2.add(obj)
                Synset::class -> synsetIds2.add(obj)
                Sense::class -> senseIds2.add(obj)
            }
        }

        lemmas2
            .also { if (verbose) Tracing.psInfo.println("# [LEMMAS] $it\n") }
            .map { model.lexFinder(it) }
            .forEach {
                it?.forEach { lex ->
                    if (verbose) Tracing.psInfo.println("# [LEX] ${lex.lemma} ${lex.type.value} ${lex.discriminant}\n")
                    lex.dump(model, outFormat, outSerialization, outJson, outYaml)
                }
            }

        lexIds2
            .also { if (verbose) Tracing.psInfo.println("# [LEXES] $it\n") }
            .map { it.split(",") }
            .map { (lemma, key2) -> key2 to model.lexFinder(lemma) }
            .map { (key2, lexes) -> lexes?.filter { lex -> lex.key2 == key2 } }
            .forEach {
                it?.forEach { lex ->
                    if (verbose) Tracing.psInfo.println("# [LEX] ${lex.lemma} ${lex.type.value} ${lex.discriminant}\n")
                    lex.dump(model, outFormat, outSerialization, outJson, outYaml)
                }
            }

        synsetIds2
            .also { if (verbose) Tracing.psInfo.println("# [SYNSETS] $it\n") }
            .map { model.synsetFinder(it) }
            .forEach {
                if (verbose) Tracing.psInfo.println("# [SYNSET] ${it?.synsetId}\n")
                it?.dump(model, outFormat, outSerialization, outJson, outYaml)
            }

        senseIds2
            .also { if (verbose) Tracing.psInfo.println("# [SENSES] $it\n") }
            .map { model.senseFinder(it) }
            .forEach {
                if (verbose) Tracing.psInfo.println("# [SENSE] ${it?.senseKey}\n")
                it?.dump(model, outFormat, outSerialization, outJson, outYaml, outPretty)
            }

        progress("after model is consumed", startTime, verbose = verbose)

        // End
        progress("end", startTime, verbose = verbose)
    }

    fun Any.dump(model: CoreModel, outFormat: Format, outSerialization: SerializationMode, jsonMethod: JsonMethod, outYaml: YamlDumpMode, prettyPrint: Boolean = true, noCast: Boolean = false) {
        when (outFormat) {
            Format.YAML -> YamlObjectConsumer(mode = outSerialization, dumperOptions = outYaml.options, noCast = noCast, ps = System.out).accept(this, model)
            Format.JSON -> JsonObjectConsumer(mode = outSerialization, jsonMethod = jsonMethod, prettyPrint = prettyPrint, ps = System.out).accept(this, model)
            else -> throw IllegalArgumentException("Unsupported output format")
        }
    }
}
