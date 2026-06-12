/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.tool

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import org.oewntk.tool.Tracing.progress
import org.oewntk.tool.Tracing.start
import org.oewntk.sql.out.SchemaGenerator
import org.oewntk.sql.out.SourcesGenerator
import java.io.File

/**
 * Main class that generates built-in sql files
 *
 * @author Bernard Bou
 * @see "https://sqlunet.sourceforge.net/schema.html"
 */
object GrindSql {

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
        val parser = ArgParser("grindsql")
        // Options (start with - or --)
        // @formatter:off
        val out by parser.argument(  ArgType.String,                                         description = "Output dir or file")
        val compat by parser.option( ArgType.Boolean, shortName = "c", fullName = "compat",  description = "Verbose output")     .default(false)
        val verbose by parser.option(ArgType.Boolean, shortName = "v", fullName = "verbose", description = "Verbose output")     .default(false)
        // @formatter:on
        parser.parse(args)
        if (verbose) {
            System.err.println("out: $out")
        }

        // Output
        val outFile = File(out)
        if (outFile.exists() && !outFile.isDirectory) {
            outFile.delete()
        }
        Tracing.psInfo.println("[Output] " + outFile.absolutePath)

        val startTime = start()
        progress("before files are generated", startTime, verbose = verbose)
        SourcesGenerator.sources(outFile)
        SchemaGenerator.schema(outFile.absolutePath, compat = compat)
        progress("after files are generated", startTime, verbose = verbose)

        // Grind model
        Grind.main(args)
    }
}
