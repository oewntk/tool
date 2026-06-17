/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.tool

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import org.oewntk.json.out.JsonMethod
import org.oewntk.model.*
import org.oewntk.model.ModelEquals.checkDataEq
import org.oewntk.model.ModelEquals.checkDiffs
import org.oewntk.model.ModelEquals.checkLexesEq
import org.oewntk.model.ModelEquals.checkSensesEq
import org.oewntk.model.ModelEquals.checkSynsetsEq
import org.oewntk.model.ModelEquals.checkZipLexesEq
import org.oewntk.model.ModelEquals.checkZipSensesEq
import org.oewntk.model.ModelEquals.checkZipSynsetsEq
import org.oewntk.model.ModelEquals.dataEquals
import org.oewntk.tool.Args.SerializationMode
import org.oewntk.tool.Args.jsonMethodArg
import org.oewntk.tool.Args.serializationModeArg
import org.oewntk.tool.Tracing.progress
import org.oewntk.tool.Tracing.start
import org.oewntk.tool.Utils.getModel
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

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
        val modelInfo2A = "$modelInfoA\n$modelCountsA"
        Tracing.psInfo.println("Model A $modelA")
        Tracing.psInfo.println(modelInfo2A)

        val modelInfoB = modelB.info()
        val modelCountsB = ModelInfo.counts(modelB)
        val modelInfo2B = "$modelInfoB\n$modelCountsB"
        Tracing.psInfo.println("Model B $modelB")
        Tracing.psInfo.println(modelInfo2B)

        if (modelInfo2A != modelInfo2B) Tracing.psErr.println("[E] Model A $modelA and B $modelB don't have the same info")
        else Tracing.psInfo.println("[I] Model A and B have the same info")

        val areEqual = modelA == modelB
        if (!areEqual) {
            Tracing.psErr.println("[E] Model A $modelA and B $modelB are not equal")
            val dataEq = modelA.dataEquals(modelB)
            if (!dataEq) Tracing.psErr.println("[E] Model A $modelA and B $modelB are not data equal")

            checkDiffs(modelA, modelB)
            //findDiffs(modelA, modelB)
        } else Tracing.psInfo.println("[I] Model A and B are equal")

        // End
        progress("end", startTime, verbose = verbose)
    }
}

fun findDiffs(modelA: Model, modelB: Model) {
    val diffs = structuralDiff(DataModel(modelA), DataModel(modelB))
    if (diffs.isNotEmpty()) {
        //diffs.forEach { println(it.path) }
        //diffs.forEach { println("${it.path}:\n  expected: ${it.expected.toString().substring(0,80)}\n  actual:   ${it.actual.toString().substring(0,80)}") }
        diffs.forEach { diff ->
            val expStr = diff.expected.toString()
            val actStr = diff.actual.toString()
            println("${diff.path}:\n${firstDivergence(expStr, actStr)}")
        }
        Tracing.psErr.println("[E] Model A $modelA and B $modelB")
        error("Objects differ at ${diffs.size} location(s)")
    }
}

data class Diff(val path: String, val expected: Any?, val actual: Any?)

private val LEAF_PACKAGES = setOf("java.", "javax.", "kotlin.", "sun.", "com.sun.")

fun isLeaf(obj: Any): Boolean =
    LEAF_PACKAGES.any { obj::class.java.name.startsWith(it) }

typealias KeyExtractor = (Any) -> Any?

val keyExtractors: Map<KClass<*>, KeyExtractor> = mapOf(
    Synset::class to { (it as Synset).key },
    Lex::class to { (it as Lex).key },
    Sense::class to { (it as Sense).key },
)

fun structuralDiff(expected: Any?, actual: Any?, path: String = ""): List<Diff> {
    if (expected == null || actual == null) return if (expected == actual) emptyList() else listOf(Diff(path, expected, actual))
    if (expected::class != actual::class) return listOf(Diff(path, expected, actual))

    // Collections and Maps BEFORE isLeaf — they start with java. but must be recursed into
    if (expected is Collection<*> && actual is Collection<*>) {
        val elementClass = (expected.firstOrNull() ?: actual.firstOrNull())?.let { it::class }
        val keyFn = elementClass?.let { keyExtractors[it] }
        if (keyFn != null) {
            val expMap = expected.filterNotNull().associateBy(keyFn)
            val actMap = actual.filterNotNull().associateBy(keyFn)
            val allKeys = (expMap.keys + actMap.keys).toSet()
            return allKeys.flatMap { k ->
                when {
                    !actMap.containsKey(k) -> listOf(Diff("$path[key=$k]", expMap[k], "<missing>"))
                    !expMap.containsKey(k) -> listOf(Diff("$path[key=$k]", "<missing>", actMap[k]))
                    else -> structuralDiff(expMap[k], actMap[k], "$path[key=$k]")
                }
            }
        } else {
            val expList = expected.toList()
            val actList = actual.toList()
            val minSize = minOf(expList.size, actList.size)
            val diffs = mutableListOf<Diff>()
            for (i in 0 until minSize)
                diffs += structuralDiff(expList[i], actList[i], "$path[$i]")
            for (i in minSize until expList.size)
                diffs += Diff("$path[$i]", expList[i], "<missing>")
            for (i in minSize until actList.size)
                diffs += Diff("$path[$i]", "<missing>", actList[i])
            return diffs
        }
    }

    if (expected is Map<*, *> && actual is Map<*, *>) {
        val allKeys = (expected.keys + actual.keys).toSet()
        return allKeys.flatMap { k ->
            when {
                !actual.containsKey(k) -> listOf(Diff("$path[$k]", expected[k], "<missing>"))
                !expected.containsKey(k) -> listOf(Diff("$path[$k]", "<missing>", actual[k]))
                else -> structuralDiff(expected[k], actual[k], "$path[$k]")
            }
        }
    }

    // Only NOW treat java./kotlin. types as opaque leaves
    if (isLeaf(expected)) return if (expected == actual) emptyList() else listOf(Diff(path, expected, actual))

    // Domain classes: reflect into backing fields
    val props = expected::class.memberProperties
        .filter { it.javaField != null }
    return props.flatMap { prop ->
        @Suppress("UNCHECKED_CAST")
        val p = prop as kotlin.reflect.KProperty1<Any, *>
        p.isAccessible = true
        val childPath = if (path.isEmpty()) prop.name else "$path.${prop.name}"
        structuralDiff(p.get(expected), p.get(actual), childPath)
    }
}

fun firstDivergence(a: String, b: String, context: Int = 40): String {
    val idx = a.zip(b).indexOfFirst { (ca, cb) -> ca != cb }
    return when {
        idx == -1 && a.length == b.length -> "<identical>"
        idx == -1 -> "same up to index ${minOf(a.length, b.length)}, then one is longer: " +
                "expected[${a.length}] actual[${b.length}]"

        else -> {
            val from = maxOf(0, idx - context)
            val toA = minOf(a.length, idx + context)
            val toB = minOf(b.length, idx + context)
            """
            |first difference at index $idx:
            |  expected: ...${a.substring(from, toA)}...
            |  actual:   ...${b.substring(from, toB)}...
            |  char: expected='${a[idx]}' (${a[idx].code})  actual='${b[idx]}' (${b[idx].code})
            """.trimMargin()
        }
    }
}