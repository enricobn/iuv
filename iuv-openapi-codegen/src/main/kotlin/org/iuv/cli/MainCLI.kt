package org.iuv.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import org.iuv.openapi.IUVAPI
import org.iuv.openapi.OpenAPIReader
import org.iuv.openapi.OpenAPIWriteContext
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileWriter

private val LOGGER = LoggerFactory.getLogger(OpenAPICommand::class.java)

class OpenAPICommand : CliktCommand() {
    private val swaggerFilesFolder by argument(help="Swagger files folder").file(exists = true, fileOkay = false)
    private val controllerSourceFolder by argument(help="Controller source folder").file(exists = true, fileOkay = false)
    private val controllerPackage by argument(help="Controller package")
    private val clientSourceFolder by argument(help="Client source folder").file(exists = true, fileOkay = false)
    private val clientPackage by argument(help="Client package")
    private val modelSourceFolder by argument(help="Model source folder").file(exists = true, fileOkay = false)
    private val modelPackage by argument(help="Model package")

    override fun run() {
    // iuv-openapi-codegen/src/test/resources/petstore-expanded.yaml

        val openAPIWriteContext = OpenAPIWriteContext(controllerPackage, clientPackage, modelPackage)

        getSwaggerFiles(swaggerFilesFolder)
            .forEach {
                try {
                    val apiName = toApiName(it)
                    val api = OpenAPIReader.parse(it.toURI().toURL(), apiName)
                    if (api == null) {
                        LOGGER.error("Error reading $it")
                        return@forEach
                    } else {
                        runTemplate(controllerSourceFolder, controllerPackage, apiName + "Controller",
                                "/openapi/templates/controller.mustache", api, openAPIWriteContext)
                        runTemplate(clientSourceFolder, clientPackage, apiName + "Client",
                                "/openapi/templates/client.mustache", api, openAPIWriteContext)
                        runTemplate(modelSourceFolder, modelPackage, apiName + "Model",
                                "/openapi/templates/components.mustache", api, openAPIWriteContext)
                    }
                } catch (e: Exception) {
                    LOGGER.error("Error reading $it", e)
                    return@forEach
                }
            }
    }

    private fun runTemplate(sourceFolder: File, `package`: String, apiName: String, resource: String, api: IUVAPI, openAPIWriteContext: OpenAPIWriteContext) {
        val file = getOrCreateFile(sourceFolder, `package`, apiName)
        FileWriter(file).use { writer ->
            OpenAPIReader.runTemplate(getResource(resource), api, openAPIWriteContext, writer)
        }
    }

    private fun getOrCreateFile(sourceFolder: File, `package`: String, apiName: String): File {
        val destFolder = File(sourceFolder.absolutePath + "/" + `package`.replace('.', '/'))
        assert(destFolder.mkdirs())
        return File(destFolder, "$apiName.kt")
    }

    private fun getValidFolder(folder: String): File {
        val controllerSourceFolderFile = File(folder)
        if (!controllerSourceFolderFile.exists() || !controllerSourceFolderFile.isDirectory) {
            LOGGER.error("$folder does not exist.")
        }
        return controllerSourceFolderFile
    }

    private fun getResource(resource: String) = this.javaClass.getResource(resource)

    private fun getSwaggerFiles(folder: File) : List<File> {
        return folder.listFiles { it : File -> it.endsWith(".yaml") }.toList() +
                folder.listFiles { it: File -> it.isDirectory }.flatMap { getSwaggerFiles(it) }
    }

    private fun toApiName(file: File) : String {
        return capitalize(file.nameWithoutExtension)
    }

    fun capitalize(name: String) : String {
        var capitalizeNext = false
        val result = StringBuilder()
        name.capitalize().forEach {
            capitalizeNext = if (!it.isLetter())
                true
            else {
                result.append(if (capitalizeNext) it.toUpperCase() else it)
                false
            }
        }
        return result.toString()
    }
}

fun main(args: Array<String>) = OpenAPICommand().main(args)
