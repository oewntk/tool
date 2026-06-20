/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.tool

import kotlinx.cli.ArgType
import org.oewntk.json.out.JsonMethod
import org.oewntk.model.SerializationMode
import org.oewntk.yaml.out.YamlDump
import org.yaml.snakeyaml.DumperOptions

/**
 * Main class that generates the OEWN plus database
 *
 * @author Bernard Bou
 * @see "https://sqlunet.sourceforge.net/schema.html"
 */
object Args {

    enum class Format {
        YAML,
        JSON,
        XML,
        SER,
        SQL,
        WNDB,
    }

    val formatArg = ArgType.Choice(
        choices = Format.entries,
        variantToString = { it.name.lowercase() },
        toVariant = { raw ->
            when (raw.lowercase()) {
                "s", "ser" -> Format.SER
                "q", "sql" -> Format.SQL
                "y", "yaml" -> Format.YAML
                "j", "json" -> Format.JSON
                "w", "wndb" -> Format.WNDB
                "x", "xml" -> Format.XML
                else -> error("Unknown format: $raw")
            }
        }
    )

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

    val jsonMethodArg = ArgType.Choice(
        choices = JsonMethod.entries,
        variantToString = { it.name.lowercase() },
        toVariant = { raw ->
            when (raw.lowercase()) {
                "v", "value_wrapper" -> JsonMethod.VALUE_WRAPPER
                "j", "json_element" -> JsonMethod.JSON_ELEMENT
                "a", "any_serializer" -> JsonMethod.ANY_SERIALIZER
                else -> error("Unknown json method: $raw")
            }
        }
    )

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
}
