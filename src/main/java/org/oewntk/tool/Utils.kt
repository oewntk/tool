package org.oewntk.tool

import org.oewntk.json.out.JsonMethod
import org.oewntk.model.*
import org.oewntk.model.LemmaImpl.Companion.isLemma
import org.oewntk.model.LexIdImpl.Companion.isLexId
import org.oewntk.model.SenseKeyImpl.Companion.isSenseKey
import org.oewntk.model.SynsetIdImpl.Companion.isSynsetId
import org.oewntk.tool.Args.Format
import org.oewntk.yaml.`in`.FactoryPlus
import java.io.File
import kotlin.reflect.KClass
import org.oewntk.json.`in`.data.Factory as DataJsonFactory
import org.oewntk.json.`in`.model.Factory as ModelJsonFactory
import org.oewntk.json.`in`.oewn.Factory as OEWNJsonFactory
import org.oewntk.ser.`in`.Factory as SerFactory
import org.oewntk.wndb.`in`.Factory as WndbFactory
import org.oewntk.xml.`in`.Factory as XmlFactory
import org.oewntk.yaml.`in`.Factory as YamlFactory

object Utils {

    fun recog(input: String): KClass<*>? {
        if (input.isSenseKey())
            return Sense::class
        if (input.isSynsetId())
            return Synset::class
        if (input.isLexId())
            return Lex::class
        if (input.isLemma())
            return Lemma::class
        return null
    }

    fun getModel(
        input: String,
        input2: String?,
        inFormat: Format,
        inPlus: Boolean,
        inSerialization: SerializationMode,
        inOne: Boolean,
        inJson: JsonMethod,
        inverses: Boolean,
        throws: Boolean = true,
        verbose: Boolean = false,
    ): Model {
        val inputFile = File(input)
        val inputFile2: File? = input2?.takeIf(String::isNotEmpty)?.let { File(it) }
        return if (inPlus)
            FactoryPlus(inputFile, inputFile2!!, inverses = inverses, verbose = verbose).get()!!
        else when (inFormat) {
            Format.SER -> SerFactory(inputFile).get()!!
            Format.YAML -> YamlFactory(inputFile, inputFile2, inverses = inverses, throws = throws, verbose = verbose).get()!!
            Format.XML-> XmlFactory(inputFile, inputFile2, verbose = verbose).get()!!
            Format.WNDB -> WndbFactory(inputFile, inputFile2, verbose = verbose).get()!!
            Format.JSON -> {
                when (inSerialization) {
                    SerializationMode.OEWN -> OEWNJsonFactory(inputFile, inverses = inverses, split = !inOne, jsonMethod = inJson, verbose = verbose).get()!!
                    SerializationMode.DATA -> DataJsonFactory(inputFile, inverses = inverses, split = !inOne, jsonMethod = inJson, verbose = verbose).get()!!
                    SerializationMode.MODEL -> ModelJsonFactory(inputFile, inverses = inverses, verbose = verbose).get()!!
                }
            }

            else -> throw IllegalArgumentException("Unsupported A input format")
        }
    }
}