package org.iuv.xhtml

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheResolver
import org.apache.ws.commons.schema.*
import org.apache.ws.commons.schema.resolver.URIResolver
import org.xml.sax.InputSource
import java.io.*
import java.net.URL

fun main() {
    val definitions = XHTMLReader.read()

    definitions.elements.forEach {
        XHTMLReader.runTemplate("/templates/element.mustache", "elements", it)
    }

    definitions.enums.forEach {
        XHTMLReader.runTemplate("/templates/enum.mustache", "enums", it)
    }

    definitions.attributeGroups.forEach {
        XHTMLReader.runTemplate("/templates/group.mustache", "attributegroups", it)
    }

}

object XHTMLReader {

    fun read(): XHTMLDefinitions {
        val `is` = resourceURL("/xhtml5.xsd").openStream()
        val schemaCol = XmlSchemaCollection()
        schemaCol.schemaResolver = URIResolver { _, schemaLocation, _ ->
            InputSource(resourceURL("/$schemaLocation").openStream())
        }

        val schema = schemaCol.read(InputSource(`is`), ValidationEventHandler())

        val elements = mutableListOf<XHTMLElement>()

        val types = mutableMapOf<String, GeneratedClass>()
        types["string"] = SimpleGeneratedClass("String")
        types["token"] = SimpleGeneratedClass("String")
        types["tokens"] = SimpleGeneratedClass("String")

        val enums = mutableListOf<XHTMLEnumType>()
        val tokens = mutableMapOf<String, XHTMLToken>()

        val indenter = Indenter()

        println()
        println("Schema types")

        indenter.indent {
            schema.schemaTypes.values.forEach {
                if (it is XmlSchemaSimpleType) {
                    indenter.indent {
                        println(it.name)
                        val content = it.content
                        if (content is XmlSchemaSimpleTypeRestriction) {
                            if (content.baseTypeName.localPart == "NMTOKEN") {
                                val enumValues = mutableListOf<XHTMLEnumValue>()
                                content.facets.iterator.forEach { facet ->
                                    if (facet is XmlSchemaEnumerationFacet) {
                                        enumValues.add(XHTMLEnumValue(facet.value.toString()))
                                    } else {
                                        println("Invalid facet $facet")
                                    }
                                }
                                val xhtmlEnumType = XHTMLEnumType(it.name, enumValues)
                                types[it.name] = xhtmlEnumType
                                enums.add(xhtmlEnumType)
                            } else if (content.baseTypeName.localPart == "token") {
                                val enums = mutableListOf<String>()
                                content.facets.iterator.forEach { facet ->
                                    if (facet is XmlSchemaEnumerationFacet) {
                                        enums.add(facet.value.toString())
                                    } else {
                                        println("Invalid facet $facet")
                                    }
                                }
                                val xhtmlToken = XHTMLToken(enums)

                                tokens[it.name] = xhtmlToken
                            } else {
                                println("Invalid content type ${content.baseTypeName.localPart}")
                            }
                        } else {
                            println("Invalid content  ${content}")
                        }
                    }
                } else if (it is XmlSchemaComplexType) {
                    println("Complex: ${it.name}")
                } else {
                    println("Invalid schemaType $it")
                }
            }
        }

        val attributeGroups = mutableMapOf<String, XHTMLAttributeGroup>()

        println()
        println("Attribute groups")

        indenter.indent {
            schema.attributeGroups.values.forEach {
                if (it is XmlSchemaAttributeGroup) {
                    println(it.name)

                    indent {
                        val attributes = getAttributes(indenter, it.attributes, types)

                        val group = XHTMLAttributeGroup(it.name.localPart, attributes)
                        attributeGroups[it.name.localPart] = group
                    }
                } else {
                    println("Unknown attribute group $it")
                }
            }
        }

        println()
        println("Groups")

        val groups = mutableMapOf<String, XHTMLGroup>()

        indenter.indent {
            schema.groups.values.forEach { group ->
                if (group is XmlSchemaGroup) {
                    println(group.name.localPart)
                    indenter.indent {
                        val groupElements = mutableListOf<String>()
                        group.particle.items.iterator.forEach {
                            if (it is XmlSchemaElement) {
                                groupElements.add(it.name)
                            } else {
                                println("Unknown elements for group $group : $it")
                            }
                        }
                        groups[group.name.localPart] = XHTMLGroup(group.name.localPart, groupElements)
                    }
                } else {
                    indenter.println("Unknown group $group")
                }
            }
        }

        println()
        println("Elements")

        indenter.indent {
            schema.elements.values.forEach { element ->
                if (element is XmlSchemaElement) {
                    println(element.name)
                    indenter.indent {
                        val schemaType = element.schemaType
                        if (schemaType is XmlSchemaComplexType) {
                            elements.add(toXHTMLElement(indenter, schemaType, element, types))
                        } else {
                            println("Unknown shema type $schemaType")
                        }
                    }
                } else {
                    println("Unknown element $element")
                }
            }
        }

        return XHTMLDefinitions(elements, enums, attributeGroups.values.toList())
    }

    private fun toXHTMLElement(indenter: Indenter, schemaType: XmlSchemaComplexType, element: XmlSchemaElement, types: Map<String, GeneratedClass>): XHTMLElement {
        val attributes = getAttributes(indenter, schemaType, types)

        return XHTMLElement(element.name, attributes)
    }

    private fun getAttributes(indenter: Indenter, schemaType: XmlSchemaComplexType, types: Map<String, GeneratedClass>): XHTMLAttributes {
        var result = getAttributes(indenter, schemaType.attributes, types)

        val contentModel = schemaType.contentModel

        if (contentModel is XmlSchemaComplexContent) {
            val content = contentModel.content
            if (content is XmlSchemaComplexContentExtension) {
                result = result.add(getAttributes(indenter, content.attributes, types))
            } else {
                indenter.println("Invalid content $content")
            }
        } else if (contentModel != null) {
            indenter.println("Invalid contentModel $contentModel")
        }

        return result
    }

    private fun getAttributes(indenter: Indenter, attributes: XmlSchemaObjectCollection, types: Map<String, GeneratedClass>): XHTMLAttributes {
        val simpleAttributes = mutableListOf<XHTMLAttribute>()
        val attributeGroups = mutableListOf<AttributeGroupReference>()
        attributes.iterator.iterator().forEach {
            if (it is XmlSchemaAttribute) {
                if (it.schemaTypeName == null) {
                    simpleAttributes.add(XHTMLAttribute(it.name, types["string"] ?: error("Cannot find String type")))
                } else {
                    val type = types[it.schemaTypeName.localPart]
                    if (type == null) {
                        indenter.println("Invalid attribute type for ${it.name}: ${it.schemaTypeName.localPart}")
                    } else {
                        simpleAttributes.add(XHTMLAttribute(it.name, type))
                    }
                }
            } else if (it is XmlSchemaAttributeGroupRef) {
                attributeGroups.add(AttributeGroupReference(it.refName.localPart))
            } else {
                indenter.println("Invalid attribute $it")
            }
        }
        return XHTMLAttributes(simpleAttributes, attributeGroups)
    }

    fun runTemplate(resource: String, `package`: String, generatedClass: GeneratedClass) {
        val url = resourceURL(resource)
        val file = File("iuv-xhtml/src/jsMain/kotlin/org/iuv/html/$`package`")
        //val file = File("iuv-xhtml/src/jsGenerated/kotlin/org/iuv/html")
        file.mkdirs()
        val writer = FileWriter(File(file, generatedClass.className() + ".kt"))
        runTemplate(url, generatedClass, writer)
    }

    private fun resourceURL(resource: String): URL {
        //val url = XHTMLReader.javaClass.getResource(resource)
        return File("iuv-xhtml/src/jvm8Main/resources$resource").toURI().toURL()
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

data class XHTMLAttribute(val originalName: String, val type: GeneratedClass) {

    val name =
            if (originalName == "class") {
                "classes"
            } else {
                originalName.replace("-", "").replace(":", "")
            }

}

interface GeneratedClass {

    val name: String

    fun className(): String = name.capitalize()

    val value: String

    val valueOf: String

    val imports: List<String>

    fun nameSpace() = "org.iuv.html"

    fun fullClassName() =
        if (nameSpace().isEmpty())
            className()
        else
            nameSpace() + "." + className()

}

data class SimpleGeneratedClass(override val name: String) : GeneratedClass {
    override val imports: List<String> = listOf()

    override val value = ""

    override val valueOf = "("

}

data class AttributeGroupReference(override val name: String) : GeneratedClass {
    override val imports: List<String> = listOf()

    override val value = ""

    override val valueOf = "("

    override fun nameSpace(): String {
        return super.nameSpace() + ".attributegroups"
    }
}

data class XHTMLElement(override val name: String, val attributes: XHTMLAttributes) : GeneratedClass {

    // TODO I don't like it
    override val value = ""

    override val valueOf = "("

    override fun nameSpace(): String {
        return super.nameSpace() + ".elements"
    }

    override val imports: List<String> = attributes.imports.toList()
}

class URLMustacheResolver(private val url: URL) : MustacheResolver {

    override fun getReader(resourceName: String?): Reader {
        val lastSlash = url.toString().lastIndexOf("/")
        val resourceURL = URL(url.toString().substring(0, lastSlash) + "/" + resourceName)
        return InputStreamReader(resourceURL.openStream())
    }

}

data class XHTMLEnumType(override val name: String, val values: List<XHTMLEnumValue>) : GeneratedClass {

    override val value = ".value"

    override val valueOf = className() + ".fromValue("

    override val imports: List<String> = listOf()

    override fun nameSpace(): String {
        return super.nameSpace() + ".enums"
    }
}

data class XHTMLEnumValue(val value: String) {

    val name = value.replace("-", "")
            .replace(Regex("^\\d+.*")) { matchResult ->
                "_" + matchResult.value
            }

}

data class XHTMLDefinitions(val elements: List<XHTMLElement>, val enums: List<XHTMLEnumType>,
                            val attributeGroups: List<XHTMLAttributeGroup>)

data class XHTMLAttributeGroup(override val name: String, val attributes: XHTMLAttributes) : GeneratedClass {
    override val value: String = ""
    override val valueOf: String = ""

    override val imports: List<String> = listOf()

    override fun nameSpace(): String {
        return super.nameSpace() + ".attributegroups"
    }
}

data class XHTMLAttributes(val attributes: List<XHTMLAttribute>, val groups: List<AttributeGroupReference>) {

    fun add(xhtmlAttributes: XHTMLAttributes): XHTMLAttributes =
            XHTMLAttributes(attributes + xhtmlAttributes.attributes, groups + xhtmlAttributes.groups)

    val imports = groups.map { it.fullClassName() }.toSortedSet()

}

data class XHTMLToken(val tokens: List<String>)

data class XHTMLGroup(override val name: String, val elements: List<String>) : GeneratedClass {
    override val imports: List<String> = listOf()
    override val value: String = ""
    override val valueOf: String = ""
    override fun nameSpace(): String {
        return super.nameSpace() + ".groups"
    }
}

class Indenter(val size: Int = 2) {
    private var level = 0;

    private fun open() {
        level++
    }

    private fun close() {
        level--
    }

    fun println(s: String) {
        kotlin.io.println(" ".repeat(level * size) + s)
    }

    fun indent(init: Indenter.() -> Unit) {
        open()
        init.invoke(this)
        close()
    }

}