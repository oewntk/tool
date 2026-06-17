/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.tool

import org.oewntk.model.*
import java.io.PrintStream
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

typealias KeyExtractor = (Any) -> Any?

/**
 * Main class that compares models
 *
 * @author Bernard Bou
 * @see "https://sqlunet.sourceforge.net/schema.html"
 */
object Diffs {

    fun findDiffs(modelA: Model, modelB: Model, throws: Boolean = false, ps: PrintStream = System.out) {
        val diffs = structuralDiff(DataModel(modelA), DataModel(modelB))
        if (diffs.isNotEmpty()) {
            diffs.forEach { diff ->
                val expStr = diff.expected.toString()
                val actStr = diff.actual.toString()
                println("${diff.path}:\n${firstDivergence(expStr, actStr)}")
            }
            val report = "[E] Model A $modelA and B $modelB differ at ${diffs.size} location(s)"
            ps.println(report)
            if (throws) error(report)
        }
    }

    data class Diff(val path: String, val expected: Any?, val actual: Any?)

    private val LEAF_PACKAGES = setOf("java.", "javax.", "kotlin.", "sun.", "com.sun.")

    fun isLeaf(obj: Any): Boolean =
        LEAF_PACKAGES.any { obj::class.java.name.startsWith(it) }

    val keyExtractors: Map<KClass<*>, KeyExtractor> = mapOf(
        Synset::class to { (it as Synset).key },
        Lex::class to { (it as Lex).key },
        Sense::class to { (it as Sense).key },
    )

    fun structure(obj: Any?, path: String = "", level: Int = 0, ps: PrintStream) {
        if (obj == null) return

        // Collections
        if (obj is Collection<*>) {
            val elementClass = obj.firstOrNull()?.let { it::class }
            val keyFn = elementClass?.let { keyExtractors[it] }
            if (keyFn != null) {
                val objMap = obj.filterNotNull().associateBy(keyFn)
                objMap.keys.forEach { k ->
                    structure(objMap[k], "$path[key=$k]", level, ps)
                }

            } else {
                obj.toList().withIndex().forEach { (i, element) ->
                    structure(element, "$path[$i]", level, ps)
                }
            }
        }

        // Maps
        if (obj is Map<*, *>) {
            obj.keys.forEach { k ->
                val value = obj[k]
                structure(value, "$path[$k]", level, ps)
            }
        }

        // Only NOW treat java./kotlin. types as opaque leaves
        if (isLeaf(obj)) {
            ps.println("${"    ".repeat(level)}$path -> $obj")
            return
        }

        // Domain classes: reflect into backing fields
        obj::class.memberProperties
            .filter { it.javaField != null }
            .toList()
            .forEach { kProp ->
                @Suppress("UNCHECKED_CAST")
                val prop = kProp as KProperty1<Any, *>
                prop.isAccessible = true
                val childPath = if (path.isEmpty()) kProp.name else "$path.${kProp.name}"
                val child = prop.get(obj)
                structure(child, childPath, level+1, ps)
            }
    }


    fun structuralDiff(expected: Any?, actual: Any?, path: String = ""): List<Diff> {
        if (expected == null || actual == null) return if (expected == actual) emptyList() else listOf(Diff(path, expected, actual))
        if (expected::class != actual::class) return listOf(Diff(path, expected, actual))

        // Collections and Maps BEFORE isLeaf — they start with java. but must be recursed into

        // Collections
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

        // Maps
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
        if (isLeaf(expected))
            return if (expected == actual)
                emptyList()
            else listOf(Diff(path, expected, actual))

        // Domain classes: reflect into backing fields
        return expected::class.memberProperties
            .filter { it.javaField != null }
            .toList()
            .flatMap { kProp ->
                @Suppress("UNCHECKED_CAST")
                val prop = kProp as KProperty1<Any, *>
                prop.isAccessible = true
                val childPath = if (path.isEmpty()) kProp.name else "$path.${kProp.name}"
                structuralDiff(prop.get(expected), prop.get(actual), childPath)
            }
    }

    fun firstDivergence(a: String, b: String, context: Int = 40): String {
        val idx = a.zip(b).indexOfFirst { (ca, cb) -> ca != cb }
        return when {
            idx == -1 && a.length == b.length -> "<identical>"
            idx == -1 -> "same up to index ${minOf(a.length, b.length)}, then one is longer: expected[${a.length}] actual[${b.length}]"

            else -> {
                val from = maxOf(0, idx - context)
                val toA = minOf(a.length, idx + context)
                val toB = minOf(b.length, idx + context)
                """
            |  first difference at index $idx:
            |    expected:    ${a.substring(from, toA)}...
            |    actual:      ${b.substring(from, toB)}...
            |    char:        expected='${a[idx]}' (${a[idx].code})  actual='${b[idx]}' (${b[idx].code})
            """.trimMargin()
            }
        }
    }
}