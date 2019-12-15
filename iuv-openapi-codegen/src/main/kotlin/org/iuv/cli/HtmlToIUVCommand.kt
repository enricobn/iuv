package org.iuv.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import org.jsoup.Jsoup
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import java.io.InputStream

class HtmlToIUVCommand : CliktCommand(name = "HTMLtoIUV") {
    private val htmlFile by argument(name = "htmlFile", help="HTML snippet file").file(exists = true, fileOkay = true)

    override fun run() {
        htmlFile.inputStream().use {
            HtmlToIUVCommandParser().parse(it)
        }
    }
}

class HtmlToIUVCommandParser {

    fun parse(source: InputStream) : String =
        parse(source.reader().readText())

    fun parse(source: String) : String {
        val doc = Jsoup.parseBodyFragment(source)

        val sb = StringBuilder()

        doc.body().children().forEach {
            parse(it, 0, sb)
        }

        return sb.toString()
    }

    private fun parse(node: Node, indent: Int, sb: StringBuilder) {
        indent(sb, indent)

        if (node is TextNode) {
            sb.append("+\"").append(node.text()).append("\"\n")
        } else {
            sb.append(node.nodeName()).append(" {\n")

            var i = 0
            node.attributes().forEach {
                i++
                indent(sb, indent + 1)
                var key = it.key

                if (key == "class") {
                    key = "classes"
                } else if (key == "for") {
                    key = "forElement"
                }

                sb.append(key).append(" = \"").append(it.value).append("\"\n")
            }

            val textNodes = node.childNodes().filterIsInstance<TextNode>()
            textNodes.forEach {
                i++
                parse(it, indent + 1, sb)
            }

            val nodes = node.childNodes().filter { it !is TextNode }

            if (i > 0 && nodes.isNotEmpty()) {
                indent(sb, indent + 1)
                sb.append("\n")
            }

            i = 0
            nodes.forEach {
                if (i > 0) {
                    sb.append("\n")
                }
                i++
                parse(it, indent + 1, sb)
            }

            indent(sb, indent)

            sb.append("}\n")
        }
    }

    private fun indent(sb: StringBuilder, indent: Int) {
        sb.append("    ".repeat(indent))
    }

}