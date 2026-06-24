package org.oewntk.tool

import org.oewntk.tool.Args.Format
import org.oewntk.json.out.JsonMethod
import org.oewntk.model.Lemma
import org.oewntk.model.Lex
import org.oewntk.model.Model
import org.oewntk.model.Sense
import org.oewntk.model.SerializationMode
import org.oewntk.model.Synset
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

    private val senseKeyRegex = "\\w+%\\d:\\d{2}:\\d{2}:\\w*:".toRegex()

    private val synsetIdRegex = "\\d{8}-[nvar]".toRegex()

    private val lexIdRegex = "\\w+,[nvars]-?[\\d]?".toRegex()

    private val lemmaRegex = "\\w+".toRegex()

    fun recog(input: String): KClass<*>? {
        if (senseKeyRegex.matches(input))
            return Sense::class
        if (synsetIdRegex.matches(input))
            return Synset::class
        if (lexIdRegex.matches(input))
            return Lex::class
        if (lemmaRegex.matches(input))
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
        verbose: Boolean,
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
                    SerializationMode.OEWN -> OEWNJsonFactory(inputFile, split = !inOne, jsonMethod = inJson, verbose = verbose).get()!!
                    SerializationMode.DATA -> DataJsonFactory(inputFile, split = !inOne, jsonMethod = inJson, verbose = verbose).get()!!
                    SerializationMode.MODEL -> ModelJsonFactory(inputFile, verbose = verbose).get()!!
                }
            }

            else -> throw IllegalArgumentException("Unsupported A input format")
        }
    }
}