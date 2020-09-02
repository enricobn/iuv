package org.iuv.xhtml

import com.github.mustachejava.MustacheResolver
import org.apache.ws.commons.schema.*
import org.apache.ws.commons.schema.resolver.URIResolver
import org.iuv.xhtml.impl.XHTMLFileResourceProvider
import org.xml.sax.InputSource
import java.io.InputStreamReader
import java.io.Reader
import java.net.URL

fun main() {
    val resourceProvider = XHTMLFileResourceProvider()
    val definitions = XHTMLReader.read(resourceProvider)
    val xhtmlTemplaterRunner = XHTMLTemplaterRunner(resourceProvider)

    val path = "iuv-core/src/jsMain/kotlin"

    definitions.elements.forEach {
        xhtmlTemplaterRunner.runTemplate("/templates/element.mustache", it, path)
    }

    definitions.enums.forEach {
        xhtmlTemplaterRunner.runTemplate("/templates/enum.mustache", it, path)
    }

    definitions.attributeGroups.forEach {
        xhtmlTemplaterRunner.runTemplate("/templates/attributeGroup.mustache", it, path)
    }

    definitions.groups.forEach {
        xhtmlTemplaterRunner.runTemplate("/templates/group.mustache", it, path)
    }

}

object XHTMLReader {

    fun read(resourceProvider: XHTMLResourceProvider): XHTMLDefinitions {
        val inputStream = resourceProvider.getUrl("/xhtml5.xsd").openStream()
        val schemaCol = XmlSchemaCollection()
        schemaCol.schemaResolver = URIResolver { _, schemaLocation, _ ->
            InputSource(resourceProvider.getUrl("/$schemaLocation").openStream())
        }

        val schema = schemaCol.read(InputSource(inputStream), ValidationEventHandler())

        val context = XHTMLReaderContext()

        val indenter = Indenter()

        parseSchemaTypes(indenter, schema, context)

        parseAttributeGroups(indenter, schema, context)

        parseGroups(indenter, schema, context)

        parseElements(indenter, schema, context)

        return context.toDefinitions()
    }

    private fun parseElements(indenter: Indenter, schema: XmlSchema, context: XHTMLReaderContext) {
        println()
        println("Elements")

        indenter.indent {
            schema.elements.values.forEach { element ->
                if (element is XmlSchemaElement) {
                    println(element.name)
                    indenter.indent {
                        val schemaType = element.schemaType
                        if (schemaType is XmlSchemaComplexType) {
                            context.add(toXHTMLElement(indenter, context, schemaType, element))
                        } else {
                            println("Unknown shema type $schemaType")
                        }
                    }
                } else if (element is XmlSchemaComplexType) {
                    println(element.name)
                    indenter.indent {
                        println("Unknown element $element")
                    }
                } else {
                    println("Unknown element $element")
                }
            }
        }
    }

    private fun parseGroups(indenter: Indenter, schema: XmlSchema, context: XHTMLReaderContext) {
        println()
        println("Groups")

        indenter.indent {
            schema.groups.values.forEach { group ->
                if (group is XmlSchemaGroup) {
                    println(group.name.localPart)
                    indenter.indent {
                        val particle = parseParticle(indenter, context, group.particle, group)
                        context.add(XHTMLGroup(group.name.localPart, particle.groups.filter { it.name != group.name.localPart }, particle.children))
                    }
                } else {
                    println("Unknown group $group")
                }
            }
        }
    }

    private fun parseAttributeGroups(indenter: Indenter, schema: XmlSchema, context: XHTMLReaderContext) {
        println()
        println("Attribute groups")

        indenter.indent {
            schema.attributeGroups.values.forEach {
                if (it is XmlSchemaAttributeGroup) {
                    println(it.name)

                    indent {
                        val attributes = getAttributes(indenter, context, it.attributes)

                        context.add(XHTMLAttributeGroup(it.name.localPart, attributes))
                    }
                } else {
                    println("Unknown attribute group $it")
                }
            }
        }
    }

    private fun parseSchemaTypes(indenter: Indenter, schema: XmlSchema, context: XHTMLReaderContext) {
        println()
        println("Schema types")

        indenter.indent {
            schema.schemaTypes.values.forEach {
                if (it is XmlSchemaSimpleType) {
                    println(it.name)
                    indenter.indent {
                        val content = it.content
                        if (content is XmlSchemaSimpleTypeRestriction) {
                            if (content.baseTypeName.localPart == "NMTOKEN") {
                                val enumValues = mutableListOf<XHTMLEnumValue>()
                                content.facets.iterator.forEach { facet ->
                                    if (facet is XmlSchemaEnumerationFacet) {
                                        enumValues.add(XHTMLEnumValue(facet.value.toString()))
                                    } else {
                                        println("Unknown facet $facet")
                                    }
                                }
                                context.add(XHTMLEnumType(it.name, enumValues))
                            } else if (content.baseTypeName.localPart == "token") {
                                /*
                                val enums = mutableListOf<String>()
                                content.facets.iterator.forEach { facet ->
                                    if (facet is XmlSchemaEnumerationFacet) {
                                        enums.add(facet.value.toString())
                                    } else {
                                        println("Unknown facet $facet")
                                    }
                                }
                                val xhtmlEnumType = XHTMLEnumType(it.name, enums.map { token -> XHTMLEnumValue(token) })
                                context.add(xhtmlEnumType) // XHTMLToken(it.name, enums)

                                 */
                                context.addType(it.name, XHTMLReaderContext.STRING)
                            } else if (content.baseTypeName.localPart == "tokens") {
                                context.addType(it.name, XHTMLReaderContext.STRING)
                            } else {
                                if (context.getType(content.baseTypeName.localPart) == null) {
                                    println("Unknown content type ${content.baseTypeName.localPart}")
                                }
                            }
                        } else {
                            println("Unknown content $content")
                        }
                    }
                } else if (it is XmlSchemaComplexType) {
                    println(it.name)
                    val contentModel = it.contentModel

                    // TODO flowContentElement remove it?
                    var found = false
                    val content = contentModel?.content
                    if (content is XmlSchemaComplexContentExtension) {
                        if (content.baseTypeName.localPart == "flowContentElement") {
                            context.addType(it.name, SimpleGeneratedClass(it.name, "types"))
                            found = true
                        }
                    }

                    if (!found) {
                        indenter.indent {
                            println("Complex: ${it.name}")
                        }
                    }
                } else {
                    indenter.indent {
                        println("Unknown schemaType $it")
                    }
                }
            }
        }
    }

    private fun parseParticle(indenter: Indenter, context: XHTMLReaderContext, particle: XmlSchemaGroupBase, parent: XmlSchemaAnnotated):
            XHTMLParticle {
        val children = mutableListOf<GeneratedClass>()
        val groups = mutableListOf<GeneratedClass>()

        val parentName = if (parent is XmlSchemaElement) {
            parent.name
        } else if (parent is XmlSchemaGroup) {
            parent.name.localPart
        } else {
            null
        }

        particle.items.iterator.forEach {
            if (it is XmlSchemaElement && it.refName == null && (parent is XmlSchemaElement || parent is XmlSchemaGroup)) {
                if (parentName != null) {
                    val schemaType = it.schemaType
                    if (schemaType is XmlSchemaComplexType) {
                        val element = toXHTMLElement(indenter, context, schemaType, it)
                        context.add(XHTMLInnerElement(it.name, parentName, element.groups, element.attributes, element.children))
                    } else {
                        println("Unknown inner schema type $schemaType of $parent")
                    }
                    //groupElements.add(SimpleGeneratedClass(innerName, forcedName = it.name))
                    children.add(InnerElementGeneratedClass(it.name, parentName))
                }
            } else if (it is XmlSchemaElement) {
                children.add(SimpleGeneratedClass(it.name, "elements"))
            } else if (it is XmlSchemaGroupRef) {
                /*
                var group = context.getGroup(it.refName.localPart)
                if (group == null) {
                    group = context.getGroup(it.refName.localPart)
                }
                if (group == null) {
                    indenter.println("Unknown group for parent $parent : ${it.refName.localPart}")
                } else {
                    children.addAll(group.elements.map { groupElement -> SimpleGeneratedClass(groupElement) })
                }
                 */
                groups.add(SimpleGeneratedClass(it.refName.localPart, "groups"))
            } else {
                indenter.println("Unknown elements for parent $parent : $it")
            }
        }

        val excludes = setOf("Svg", "Math")

        return XHTMLParticle(groups, children.filter { !excludes.contains(it.className()) })
    }

    private fun toXHTMLElement(indenter: Indenter, context: XHTMLReaderContext, schemaType: XmlSchemaComplexType, element: XmlSchemaElement): XHTMLElement {
        val attributes = getAttributes(indenter, context, schemaType)

        val contentModel = schemaType.contentModel
        if (contentModel is XmlSchemaComplexContent) {
            val content = contentModel.content
            if (content is XmlSchemaComplexContentExtension) {
                indenter.println("baseType: " + content.baseTypeName.localPart)
            }
        }

        var children : List<GeneratedClass> = listOf()
        var groups : List<GeneratedClass> = listOf()

        val particle = schemaType.particle

        if (particle is XmlSchemaGroupBase) {
            val parseParticle = parseParticle(indenter, context, particle, element)
            children = parseParticle.children
            groups = parseParticle.groups
        } else if (particle != null) {
            indenter.println("Unknown particle type: $particle")
        }

        return XHTMLElement(element.name, attributes, groups, children)
    }

    private fun getAttributes(indenter: Indenter, context: XHTMLReaderContext, schemaType: XmlSchemaComplexType): XHTMLAttributes {
        var result = getAttributes(indenter, context, schemaType.attributes)

        val contentModel = schemaType.contentModel

        if (contentModel is XmlSchemaComplexContent) {
            val content = contentModel.content
            if (content is XmlSchemaComplexContentExtension) {
                result = result.add(getAttributes(indenter, context, content.attributes))
            } else {
                indenter.println("Unknown content $content")
            }
        } else if (contentModel != null) {
            indenter.println("Unknown contentModel $contentModel")
        }

        return result
    }

    private fun getAttributes(indenter: Indenter, context: XHTMLReaderContext, attributes: XmlSchemaObjectCollection): XHTMLAttributes {
        val simpleAttributes = mutableListOf<XHTMLAttribute>()
        val attributeGroups = mutableListOf<AttributeGroupReference>()
        attributes.iterator.iterator().forEach {
            if (it is XmlSchemaAttribute) {
                if (it.schemaTypeName == null) {
                    simpleAttributes.add(XHTMLAttribute(it.name, context.getType("string") ?: error("Cannot find String type")))
                } else {
                    val type = context.getType(it.schemaTypeName.localPart)
                    if (type == null) {
                        indenter.println("Unknown attribute type for ${it.name}: ${it.schemaTypeName.localPart}")
                    } else {
                        simpleAttributes.add(XHTMLAttribute(it.name, type))
                    }
                }
            } else if (it is XmlSchemaAttributeGroupRef) {
                attributeGroups.add(AttributeGroupReference(it.refName.localPart))
            } else {
                indenter.println("Unknown attribute $it")
            }
        }
        return XHTMLAttributes(simpleAttributes, attributeGroups)
    }

}

data class XHTMLAttribute(val originalName: String, val type: GeneratedClass) {

    val name = normalizeName(originalName)

}

interface GeneratedClass {

    val name: String

    fun className(): String = name.capitalize()

    /**
     * an expression to get a string representation
     */
    fun value() : String = ""

    /**
     * the name of a function to use to parse the value from the string representation
     */
    fun valueOf() : String = ""

    val imports: List<String>

    fun nameSpace() = "org.iuv.core.html"

    fun fullClassName() =
        if (nameSpace().isEmpty())
            className()
        else
            nameSpace() + "." + className()

}

data class SimpleGeneratedClass(val originalName: String, val relativeNameSpace: String) : GeneratedClass {
    override val imports: List<String> = listOf()

    override val name = normalizeName(originalName)

    override fun className(): String {
        return name.capitalize()
    }

    override fun nameSpace(): String {
        return super.nameSpace() + "." + relativeNameSpace
    }
}

data class InnerElementGeneratedClass(val originalName: String, val parentName: String) : GeneratedClass {
    override val imports: List<String> = listOf()

    override val name = normalizeName(originalName)

    override fun className(): String {
        return parentName.capitalize() + originalName.capitalize()
    }

}

data class AttributeGroupReference(override val name: String) : GeneratedClass {
    override val imports: List<String> = listOf()

    override fun nameSpace(): String {
        return super.nameSpace() + ".attributegroups"
    }
}

interface IXHTMLElement : GeneratedClass {
    val attributes: XHTMLAttributes
    val groups: List<GeneratedClass>
    val children: List<GeneratedClass>
}

data class XHTMLParticle(val groups: List<GeneratedClass>, val children: List<GeneratedClass>)

data class XHTMLElement(val originalName: String, override val attributes: XHTMLAttributes, override val groups: List<GeneratedClass>,
                        override val children: List<GeneratedClass>) : IXHTMLElement {

    override fun nameSpace(): String {
        return super.nameSpace() + ".elements"
    }

    override val imports: List<String> = attributes.imports.toList()

    override fun className(): String {
        return normalizeName(originalName).capitalize()
    }

    override val name: String = originalName
}

data class XHTMLInnerElement(override val name: String, val parentName: String, override val groups: List<GeneratedClass>, override val attributes: XHTMLAttributes,
                             override val children: List<GeneratedClass>) : IXHTMLElement {

    override fun nameSpace(): String {
        return super.nameSpace() + ".elements"
    }

    override val imports: List<String> = attributes.imports.toList()

    override fun className(): String = parentName.capitalize() + name.capitalize()
}


class URLMustacheResolver(private val url: URL) : MustacheResolver {

    override fun getReader(resourceName: String?): Reader {
        val lastSlash = url.toString().lastIndexOf("/")
        val resourceURL = URL(url.toString().substring(0, lastSlash) + "/" + resourceName)
        return InputStreamReader(resourceURL.openStream())
    }

}

data class XHTMLEnumType(override val name: String, val values: List<XHTMLEnumValue>) : GeneratedClass {

    override fun value() = ".value"

    override fun valueOf() = className() + ".fromValue"

    override val imports: List<String> = listOf()

    override fun nameSpace(): String {
        return super.nameSpace() + ".enums"
    }

}

data class XHTMLEnumValue(val value: String) {

    val name = normalizeName(value)

}

fun normalizeName(name: String) : String =
    if (name == "var") {
        "var_"
    } else if (name == "object") {
        "object_"
    } else if (name == "class") {
        "classes"
    } else if (name == "map") {
        "map_"
    } else if (name == "for") {
        "for_"
    } else if (name == "") {
        "none"
    } else {
        name.replace("-", "")
                .replace(Regex("^\\d+.*")) { matchResult ->
                    "_" + matchResult.value
                }
                .replace(":", "_")
    }

data class XHTMLDefinitions(val elements: List<IXHTMLElement>, val enums: List<XHTMLEnumType>,
                            val attributeGroups: List<XHTMLAttributeGroup>,
                            val groups: List<XHTMLGroup>)

data class XHTMLAttributeGroup(override val name: String, val attributes: XHTMLAttributes) : GeneratedClass {
    override val imports: List<String> = attributes.imports

    override fun nameSpace(): String {
        return super.nameSpace() + ".attributegroups"
    }
}

data class XHTMLAttributes(val attributes: List<XHTMLAttribute>, val groups: List<AttributeGroupReference>) {

    fun add(xhtmlAttributes: XHTMLAttributes): XHTMLAttributes =
            XHTMLAttributes(attributes + xhtmlAttributes.attributes, groups + xhtmlAttributes.groups)

    val imports = (groups.map { it.fullClassName() } + attributes.flatMap {
        val type = it.type
        if (type is XHTMLEnumType) {
            setOf(type.fullClassName())
        } else {
            emptySet()
        }
    }).toSortedSet().toList()

}

data class XHTMLToken(val name: String, val tokens: List<String>)

data class XHTMLGroup(override val name: String, val groups: List<GeneratedClass>, val children: List<GeneratedClass>) : GeneratedClass {

    override val imports: List<String> = children.map { it.fullClassName() }

    override fun nameSpace(): String {
        return super.nameSpace() + ".groups"
    }
}
