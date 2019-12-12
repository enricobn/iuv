package org.iuv.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
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
import java.io.InputStream

private val LOGGER = LoggerFactory.getLogger(OpenAPICommand::class.java)

class CLICommand : CliktCommand() {
    override fun run() {

    }

}

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
        val yamlFiles = folder.listFiles { it: File -> it.extension == "yaml" || it.extension == "json" } ?: emptyArray()
        val directories = folder.listFiles { it: File -> it.isDirectory } ?: emptyArray()
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

data class NewProjectContext(val group: String, val projectName: String, val kotlinVersion: String, val iuvVersion: String,
                             val serializationVersion: String, val springBootVersion: String, val jarFileName: String) {

    companion object {
        fun safePackage(packageName: String) : String =
            packageName.filter { it == '.' || it.isLetterOrDigit() }
    }

    val clientPackage = safePackage("$group.ui")

    val webPackage = safePackage("$group.web")

}

class NewProjectCommand : CliktCommand(name = "newProject") {
    private val group: String by argument("group")
    private val kotlinVersion : String by option().default("1.3.30")
    private val iuvVersion : String by option().default("0.1-SNAPSHOT")
    private val serializationVersion : String by option().default("0.9.1")
    private val springBootVersion : String by option().default("2.1.4.RELEASE")
    private val projectName = File(System.getProperty("user.dir")).name
    private val jarFileName = File(this::class.java.protectionDomain.codeSource.location
            .toURI()).path
    private lateinit var projectContext : NewProjectContext

    override fun run() {
        projectContext = NewProjectContext(group, projectName, kotlinVersion, iuvVersion, serializationVersion,
                springBootVersion, jarFileName)
        runTemplate("", ".gitattributes")
        runTemplate("", ".gitignore")
        runTemplate("", "build.gradle")

        copyResource("gradlew", executable = true)
        copyResource("gradlew.bat")

        runTemplate("", "README.md")
        runTemplate("", "settings.gradle")

        runTemplate("shared", "build.gradle", destFolder = "$projectName-shared")
        runTemplate("shared", "settings.gradle", destFolder = "$projectName-shared")

        runTemplate("ui", ".gitignore", destFolder = "$projectName-ui")
        runTemplate("ui", "build.gradle", destFolder = "$projectName-ui")
        runTemplate("ui", "karma.conf.js", destFolder = "$projectName-ui")
        runTemplate("ui", "package.json", destFolder = "$projectName-ui")
        runTemplate("ui", "web.gradle", destFolder = "$projectName-ui")
        runTemplate("ui/src/main", "Main.kt", destFolder = "$projectName-ui/src/main/kotlin/" +
                packageToDir(projectContext.clientPackage))
        runTemplate("ui/src/main", "ExampleView.kt", destFolder = "$projectName-ui/src/main/kotlin/" +
                packageToDir(projectContext.clientPackage))

        runTemplate("ui/web", "index.html", destFolder = "$projectName-ui/web")
        runTemplate("ui/web", "style.css", destFolder = "$projectName-ui/web")

        runTemplate("web", "build.gradle", destFolder = "$projectName-web")
        runTemplate("web", ".gitignore", destFolder = "$projectName-web")
        runTemplate("web/src/main", "SpringBootApp.kt", destFolder = "$projectName-web/src/main/kotlin/" +
                packageToDir(projectContext.webPackage))

        runTemplate("gradle/wrapper", "gradle-wrapper.properties")
        copyResource("gradle/wrapper/gradle-wrapper.jar.copy", "gradle/wrapper/gradle-wrapper.jar")

        runTemplate("", "openapi.sh")

        File("openapi.sh").setExecutable(true)

        File("openapi").mkdir()
        File("$projectName-web/src/main/kotlin").mkdirs()
        File("$projectName-web/src/test/kotlin").mkdirs()
        File("$projectName-ui/src/main/kotlin").mkdirs()
        File("$projectName-ui/src/test/kotlin").mkdirs()
        File("$projectName-shared/src/commonMain/kotlin").mkdirs()
        File("$projectName-shared/src/commonTest/kotlin").mkdirs()
    }

    private fun packageToDir(`package`: String) = `package`.replace('.', '/')

    private fun copyResource(resource: String, fileName: String = resource, executable: Boolean = false) {
        val file = File(fileName)
        getResource("/project/templates/$resource").openStream().copyTo(file)

        if (executable) {
            file.setExecutable(true)
        }
    }

    private fun runTemplate(folder: String, fileName: String, resource: String = fileName, destFolder: String = folder) {
        val destFolderFile =
            if (destFolder.isEmpty()) {
                File(".")
            } else {
                File(destFolder)
            }

        val resourceFolder =
                if (folder.isEmpty()) {
                    "/project/templates"
                } else {
                    "/project/templates/$folder"
                }

        getOrCreateFileWriter(destFolderFile, fileName)
            .use { writer ->
                OpenAPIReader.runTemplate(getResource("$resourceFolder/$resource.mustache"), projectContext, writer)
            }
    }

    private fun getOrCreateFileWriter(folder: File, fileName: String): FileWriter {
        assert(folder.mkdirs())
        return FileWriter(File(folder, fileName))
    }

    private fun getResource(resource: String) = this.javaClass.getResource(resource)

    private fun InputStream.copyTo(file: File) {
        use { input ->
            file.outputStream().use { fileOut ->
                input.copyTo(fileOut)
            }
        }
    }

}

fun main(args: Array<String>) = CLICommand().subcommands(NewProjectCommand(), OpenAPICommand()).main(args)
