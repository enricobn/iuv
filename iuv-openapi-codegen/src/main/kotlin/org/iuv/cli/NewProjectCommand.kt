package org.iuv.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import org.iuv.openapi.OpenAPIReader
import java.io.File
import java.io.FileWriter
import java.io.InputStream

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
        runTemplate("", "openapi.properties")

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

data class NewProjectContext(val group: String, val projectName: String, val kotlinVersion: String, val iuvVersion: String,
                             val serializationVersion: String, val springBootVersion: String, val jarFileName: String) {

    companion object {
        fun safePackage(packageName: String) : String =
            packageName.filter { it == '.' || it.isLetterOrDigit() }
    }

    val clientPackage = safePackage("$group.ui")

    val webPackage = safePackage("$group.web")

}