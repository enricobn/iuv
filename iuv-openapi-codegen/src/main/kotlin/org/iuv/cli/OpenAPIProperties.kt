package org.iuv.cli

import org.iuv.shared.utils.*
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.IOException
import java.util.*

class OpenAPIProperties(
        val swaggerFilesFolder: File,
        val controllerSourceFolder: File,
        val controllerPackage: String,
        val clientSourceFolder: File,
        val clientPackage: String,
        val modelSourceFolder: File,
        val modelPackage: String,
        val sortProperties: Boolean,
        val sortParameters: Boolean) {

    companion object OpenAPIProperties {

        fun read(): Either<String, org.iuv.cli.OpenAPIProperties> {
            val properties = Properties()

            properties.load(FileInputStream("openapi.properties"))

            return readFileProperty(properties, "swaggerFilesFolder", true).flatMap { swaggerFilesFolder ->
                readFileProperty(properties, "controllerSourceFolder", true).flatMap { controllerSourceFolder ->
                    readStringProperty(properties, "controllerPackage").flatMap { controllerPackage ->
                        readFileProperty(properties, "clientSourceFolder", true).flatMap { clientSourceFolder ->
                            readStringProperty(properties, "clientPackage").flatMap { clientPackage ->
                                readFileProperty(properties, "modelSourceFolder", true).flatMap { modelSourceFolder ->
                                    readStringProperty(properties, "modelPackage").flatMap { modelPackage ->
                                        readBooleanProperty(properties, "sortProperties").flatMap { sortProperties ->
                                            readBooleanProperty(properties, "sortParameters").map { sortParameters ->
                                                OpenAPIProperties(swaggerFilesFolder, controllerSourceFolder, controllerPackage,
                                                        clientSourceFolder, clientPackage, modelSourceFolder, modelPackage, sortProperties, sortParameters)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        private fun readFileProperty(properties: Properties, name: String, directory: Boolean): Either<String, File> {
            val value = properties[name]

            if (value == null) {
                return Left("$name is mandatory")
            } else if (value is String) {
                try {
                    val file = File(value)

                    if (!file.exists()) {
                        return if (directory) {
                            Left("$name: directory does not exist")
                        } else {
                            Left("$name: file does not exist")
                        }
                    }

                    if (file.isDirectory != directory) {
                        return if (directory) {
                            Left("$name: directory expected")
                        } else {
                            Left("$name: file expected")
                        }
                    }

                    return Right(file)
                } catch (e: Exception) {
                    return Left("$name: ${e.message}")
                }
            }

            return Left("$name: unknown error")
        }

        private fun readStringProperty(properties: Properties, name: String): Either<String, String> {
            val value = properties[name]

            return if (value == null) {
                Left("$name is mandatory")
            } else if (value is String) {
                Right(value)
            } else {
                Left("$name is not valid")
            }
        }

        private fun readBooleanProperty(properties: Properties, name: String): Either<String, Boolean> {
            val value = properties[name]

            return if (value == null) {
                Right(false)
            } else if (value is String) {
                Right(value.toBoolean())
            } else {
                Left("$name is not valid")
            }
        }
    }

    fun write(file: File): String? {
        val properties = Properties()
        properties["swaggerFilesFolder"] = swaggerFilesFolder.relativeTo(file).toString()
        properties["controllerSourceFolder"] = controllerSourceFolder.relativeTo(file).toString()
        properties["controllerPackage"] = controllerPackage
        properties["clientSourceFolder"] = clientSourceFolder.relativeTo(file).toString()
        properties["clientPackage"] = clientPackage
        properties["modelSourceFolder"] = modelSourceFolder.relativeTo(file).toString()
        properties["modelPackage"] = modelPackage
        properties["sortProperties"] = sortProperties.toString()
        properties["sortParameters"] = sortParameters.toString()

        return try {
            properties.store(FileWriter(file), "IUV OpenAPI properties")
            null
        } catch (e: IOException) {
            e.message
        }
    }

    override fun toString(): String {
        return "swaggerFilesFolder=$swaggerFilesFolder\n" +
                "controllerSourceFolder=$controllerSourceFolder\n" +
                "controllerPackage=$controllerPackage\n" +
                "clientSourceFolder=$clientSourceFolder\n" +
                "clientPackage=$clientPackage\n" +
                "modelSourceFolder=$modelSourceFolder\n" +
                "modelPackage=$modelPackage\n" +
                "sortProperties=$sortProperties\n" +
                "sortParameters=$sortParameters"
    }


}