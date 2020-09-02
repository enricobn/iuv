package org.iuv.xhtml

import com.github.mustachejava.DefaultMustacheFactory
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader
import java.io.Writer
import java.net.URL

class XHTMLTemplaterRunner(private val resourceProvider: XHTMLResourceProvider) {

    fun runTemplate(resource: String, generatedClass: GeneratedClass, path: String) {
        val url = resourceProvider.getUrl(resource)

        val file = File(path, generatedClass.nameSpace().replace('.', '/'))
        file.mkdirs()
        val writer = FileWriter(File(file, generatedClass.className() + ".kt"))
        runTemplate(url, generatedClass, writer)
    }

    private fun runTemplate(url: URL, bundle: Any, writer: Writer) {
        val mf = DefaultMustacheFactory(URLMustacheResolver(url))
        url.openStream().use { inputStream ->
            InputStreamReader(inputStream, "UTF-8").use { reader ->
                val mustache = mf.compile(reader, "template.mustache")
                mustache.execute(writer, bundle)
                writer.flush()
            }
        }
    }

}