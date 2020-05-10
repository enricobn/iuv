package org.iuv.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import org.iuv.openapi.IUVAPI
import org.iuv.openapi.IUVAPIServer
import org.iuv.openapi.OpenAPIReader
import org.iuv.openapi.OpenAPIWriteContext
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileWriter

private val LOGGER = LoggerFactory.getLogger(OpenAPICommand::class.java)

class OpenAPICommand : CliktCommand(name = "openAPI") {
    private val swaggerFilesFolder by argument(help="Swagger files folder").file(exists = true, fileOkay = false)
    private val controllerSourceFolder by argument(help="Controller source folder").file(exists = true, fileOkay = false)
    private val controllerPackage by argument(help="Controller package")
    private val clientSourceFolder by argument(help="Client source folder").file(exists = true, fileOkay = false)
    private val clientPackage by argument(help="Client package")
    private val modelSourceFolder by argument(help="Model source folder").file(exists = true, fileOkay = false)
    private val modelPackage by argument(help="Model package")
    private val sortProperties by option(help = "Sort properties").flag()
    private val sortParameters by option(help = "Sort parameters").flag()

    override fun run() {
        getSwaggerFiles(swaggerFilesFolder)
            .forEach {
                try {
                    println("Processing file " + it.name)
                    val serverName = toServerName(it)

                    val openAPIWriteContext = OpenAPIWriteContext(controllerPackage, clientPackage,
                            "$modelPackage.${serverName.toLowerCase()}", sortProperties, sortParameters)

                    val server = OpenAPIReader.parse(it.toURI().toURL(), serverName, openAPIWriteContext)

                    if (server == null) {
                        System.err.println("Error reading $it")
                        return@forEach
                    } else {

                        server.apis.forEach { api ->

                            val apiName = api.name

                            println("Creating api $serverName -> $apiName")

                            val packageSuffix = serverName.toLowerCase()

                            val context = openAPIWriteContext.copy(
                                    controllerPackage = "$controllerPackage.$packageSuffix",
                                    clientPackage = "$clientPackage.$packageSuffix")

                            runTemplate(controllerSourceFolder, context.controllerPackage, apiName + "Controller",
                                    "/openapi/templates/controller.mustache", api, context)
                            runTemplate(clientSourceFolder, context.clientPackage, apiName + "Api",
                                    "/openapi/templates/client.mustache", api, context)
                            runTemplate(clientSourceFolder, context.clientPackage, apiName + "ApiImpl",
                                    "/openapi/templates/clientImpl.mustache", api, context)
                        }

                        runTemplate(modelSourceFolder, modelPackage, serverName + "Model",
                                "/openapi/templates/components.mustache", server, openAPIWriteContext)
                    }
                } catch (e: Exception) {
                    System.err.println("Error reading $it")
                    e.printStackTrace(System.err)
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

    private fun runTemplate(sourceFolder: File, `package`: String, apiName: String, resource: String, server: IUVAPIServer, openAPIWriteContext: OpenAPIWriteContext) {
        val file = getOrCreateFile(sourceFolder, `package`, apiName)
        FileWriter(file).use { writer ->
            OpenAPIReader.runTemplate(getResource(resource), mapOf("server" to server, "context" to openAPIWriteContext), writer)
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
        val yamlFiles = folder.listFiles { file: File -> file.extension == "yaml" || file.extension == "json" } ?: emptyArray()
        val directories = folder.listFiles { file: File -> file.isDirectory } ?: emptyArray()
        return yamlFiles.toList() + directories.flatMap { getSwaggerFiles(it) }
    }

    private fun toServerName(file: File) : String {
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