package org.oewntk.tool

import org.oewntk.tool.Args.SerializationMode
import org.oewntk.json.out.JsonMethod
import org.oewntk.model.Model
import org.oewntk.yaml.`in`.FactoryPlus
import java.io.File
import org.oewntk.json.`in`.data.Factory as DataJsonFactory
import org.oewntk.json.`in`.model.Factory as ModelJsonFactory
import org.oewntk.json.`in`.oewn.Factory as OEWNJsonFactory
import org.oewntk.ser.`in`.Factory as SerFactory
import org.oewntk.wndb.`in`.Factory as WndbFactory
import org.oewntk.xml.`in`.Factory as XmlFactory
import org.oewntk.yaml.`in`.Factory as YamlFactory

object Utils {

    fun getModel(
        input: String,
        input2: String?,
        inFormat: String,
        inPlus: Boolean,
        inSerialization: SerializationMode,
        inOne: Boolean,
        inJson: JsonMethod,
        verbose: Boolean
    ): Model {
        val inputFile = File(input)
        val inputFile2: File? = input2?.takeIf(String::isNotEmpty)?.let { File(it) }
        return if (inPlus)
            FactoryPlus(inputFile, inputFile2!!).get()!!
        else when (inFormat) {
            "ser" -> SerFactory(inputFile).get()!!
            "yaml" -> YamlFactory(inputFile, inputFile2, inverses = true, verbose = verbose).get()!!
            "xml" -> XmlFactory(inputFile, inputFile2, verbose = verbose).get()!!
            "wndb" -> WndbFactory(inputFile, inputFile2, verbose = verbose).get()!!
            "json" -> {
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