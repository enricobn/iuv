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

    definitions.elements.filter { !it.isAbstract }.forEach {
        xhtmlTemplaterRunner.runTemplate("/templates/element.mustache", it, path)
    }

    definitions.elements.filter { it.isAbstract }.forEach {
        xhtmlTemplaterRunner.runTemplate("/templates/abstractElement.mustache", it, path)
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
                            context.add(toXHTMLElement(indenter, context, schemaType, element, element.name, false))
                        } else {
                            println("Unknown schema type $schemaType")
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
                        val objectCollection = parseObjectCollection(indenter, context, group.particle.items, group)
                        context.add(XHTMLGroup(group.name.localPart, objectCollection.groups.filter { it.name != group.name.localPart }.toSet(), objectCollection.children.toSet()))
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
                                val enums = mutableListOf<String>()
                                content.facets.iterator.forEach { facet ->
                                    if (facet is XmlSchemaEnumerationFacet) {
                                        enums.add(facet.value.toString())
                                    } else if (facet is XmlSchemaEnumerationFacet) {
                                    } else if (facet is XmlSchemaPatternFacet) {
                                    } else {
                                        println("Unknown facet $facet")
                                    }
                                }
                                if (enums.isEmpty()) {
                                    context.addType(it.name, XHTMLReaderContext.STRING)
                                } else {
                                    val xhtmlEnumType = XHTMLEnumType(it.name, enums.map { token -> XHTMLEnumValue(token) })
                                    context.add(xhtmlEnumType) // XHTMLToken(it.name, enums)
                                }
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

                    val element = toXHTMLElement(indenter, context, it, null, it.name, true)
                    context.add(element)
                } else {
                    indenter.indent {
                        println("Unknown schemaType $it")
                    }
                }
            }
        }
    }

    private fun parseObjectCollection(indenter: Indenter, context: XHTMLReaderContext, objectCollection: XmlSchemaObjectCollection,
                                      parent: XmlSchemaAnnotated?):
            XHTMLObjectCollection {
        val children = mutableListOf<GeneratedClass>()
        val groups = mutableListOf<GeneratedClass>()

        val parentName = if (parent is XmlSchemaElement) {
            parent.name
        } else if (parent is XmlSchemaGroup) {
            parent.name.localPart
        } else {
            null
        }

        objectCollection.iterator.forEach {
            if (it is XmlSchemaElement && it.refName == null) {
                if (parentName != null) {
                    val schemaType = it.schemaType
                    if (schemaType is XmlSchemaComplexType) {
                        val element = toXHTMLElement(indenter, context, schemaType, it, it.name, false)
                        context.add(XHTMLInnerElement(context, it.name, parentName, element.groups, element.selfAttributes,
                                element.selfChildren, element.baseElement))
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
            } else if (it is XmlSchemaSequence) {
                val innerObjectCollection = parseObjectCollection(indenter, context, it.items, parent)
                groups.addAll(innerObjectCollection.groups)
                children.addAll(innerObjectCollection.children)
            } else if (it is XmlSchemaChoice) {
                val innerObjectCollection = parseObjectCollection(indenter, context, it.items, parent)
                groups.addAll(innerObjectCollection.groups)
                children.addAll(innerObjectCollection.children)
            } else {
                indenter.println("Unknown elements for parent $parent : $it")
            }
        }

        val excludes = setOf("Svg", "Math")

        return XHTMLObjectCollection(groups, children.filter { !excludes.contains(it.className()) })
    }

    private fun toXHTMLElement(indenter: Indenter, context: XHTMLReaderContext, schemaType: XmlSchemaComplexType,
                               element: XmlSchemaElement?,
                               name: String, isAbstract: Boolean): XHTMLElement {
        val attributes = getAttributes(indenter, context, schemaType)

        val contentModel = schemaType.contentModel

        var baseElement : GeneratedClass? = null

        if (contentModel is XmlSchemaComplexContent) {
            val content = contentModel.content
            if (content is XmlSchemaComplexContentExtension) {
                baseElement = context.getElement(content.baseTypeName.localPart)
            }
        }

        val children = mutableSetOf<GeneratedClass>()
        val groups = mutableSetOf<GeneratedClass>()

        val particle = schemaType.particle

        if (particle is XmlSchemaGroupBase) {
            val objectCollection = parseObjectCollection(indenter, context, particle.items, element)
            children.addAll(objectCollection.children)
            groups.addAll(objectCollection.groups)
        } else if (particle != null) {
            indenter.println("Unknown particle type: $particle")
        }

        /*
        val schema = xmlSchema(schemaType)

        schema.groups.values.forEach {
            if (it is XmlSchemaGroup) {
                groups.add(SimpleGeneratedClass(it.name.localPart, "groups"))
            }
        }
         */

        return XHTMLElement(context, name, attributes, groups, children, baseElement, isAbstract)
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

        /*
        val schema = xmlSchema(schemaType)

        val attributeGroups = mutableListOf<AttributeGroupReference>()

        schema.attributeGroups.values.forEach {
            if (it is XmlSchemaAttributeGroup) {
                attributeGroups.add(AttributeGroupReference(it.name.localPart))
            }
        }
        schema.attributes.values.forEach {
            println(it)
        }

         */

        return XHTMLAttributes(result.attributes, result.groups, result.functions)
    }

    private fun getAttributes(indenter: Indenter, context: XHTMLReaderContext, attributes: XmlSchemaObjectCollection): XHTMLAttributes {
        val simpleAttributes = mutableSetOf<XHTMLAttribute>()
        val attributeGroups = mutableSetOf<AttributeGroupReference>()
        val functions = mutableSetOf<XHTMLEventHandler>()
        attributes.iterator.iterator().forEach {
            if (it is XmlSchemaAttribute) {
                if (it.schemaTypeName == null) {
                    simpleAttributes.add(XHTMLAttribute(it.name, context.getType("string") ?: error("Cannot find String type")))
                } else {
                    val type = context.getType(it.schemaTypeName.localPart)
                    if (type == null) {
                        if (it.schemaTypeName.localPart == "functionBody") {
                            if (it.name.startsWith("on")) {
                                functions.add(XHTMLEventHandler(it.name))
                            } else {
                                indenter.println("Unknown function: ${it.name}")
                            }
                        } else {
                            indenter.println("Unknown attribute type for ${it.name}: ${it.schemaTypeName.localPart}")
                        }
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
        return XHTMLAttributes(simpleAttributes, attributeGroups, functions)
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

    fun imports() : Collection<String>

    fun nameSpace() = "org.iuv.core.html"

    fun fullClassName() =
        if (nameSpace().isEmpty())
            className()
        else
            nameSpace() + "." + className()

}

data class SimpleGeneratedClass(val originalName: String, val relativeNameSpace: String) : GeneratedClass {

    override fun imports() = emptySet<String>()

    override val name = normalizeName(originalName)

    override fun className(): String {
        return name.capitalize()
    }

    override fun nameSpace(): String {
        return super.nameSpace() + "." + relativeNameSpace
    }
}

data class NativeClass(val originalName: String) : GeneratedClass {

    override fun imports() = emptySet<String>()

    override val name = normalizeName(originalName)

    override fun className(): String {
        return name.capitalize()
    }

    override fun nameSpace(): String {
        return ""
    }
}

data class InnerElementGeneratedClass(val originalName: String, val parentName: String) : GeneratedClass {
    override fun imports() = emptySet<String>()

    override val name = normalizeName(originalName)

    override fun className(): String {
        return parentName.capitalize() + originalName.capitalize()
    }

    override fun nameSpace(): String {
        return super.nameSpace() + ".elements"
    }

}

data class AttributeGroupReference(override val name: String) : GeneratedClass {
    override fun imports() = emptySet<String>()

    override fun nameSpace(): String {
        return super.nameSpace() + ".attributegroups"
    }
}

interface IXHTMLElement : GeneratedClass {
    val selfAttributes: XHTMLAttributes
    val groups: Set<GeneratedClass>
    val selfChildren: Set<GeneratedClass>
    val context: XHTMLReaderContext
    val baseElement: GeneratedClass?
    val isAbstract: Boolean
    val elementName: String

    fun attributes() = selfAttributes.attributes.minus(selfAttributes.inheritedAttributes(context))

    fun functions() = selfAttributes.functions.minus(selfAttributes.inheritedFunctions(context))

    fun children() =
        selfChildren.minus(groups.flatMap {
            val group = context.getGroup(it.name)
            if (group == null) {
                println("Unknown group ${it.name} for $name")
                emptyList()
            } else {
                group.children
            }
        })

    override fun imports() = (groups + selfChildren +
            selfAttributes.attributes.map { it.type } + selfAttributes.groups)
            .filter { it.nameSpace().isNotEmpty() && it.nameSpace() != nameSpace() }
            .map { it.fullClassName() }.toSet().sorted()
}

data class XHTMLObjectCollection(val groups: List<GeneratedClass>, val children: List<GeneratedClass>)

data class XHTMLElement(override val context: XHTMLReaderContext,
                        val originalName: String,
                        override val selfAttributes: XHTMLAttributes,
                        override val groups: Set<GeneratedClass>,
                        override val selfChildren: Set<GeneratedClass>,
                        override val baseElement: GeneratedClass?,
                        override val isAbstract: Boolean) : IXHTMLElement {

    override fun nameSpace(): String {
        return super.nameSpace() + ".elements"
    }

    override fun className(): String {
        return normalizeName(originalName).capitalize()
    }

    override val name: String = originalName
    override val elementName: String = originalName

}

data class XHTMLInnerElement(override val context: XHTMLReaderContext,
                             override val elementName: String,
                             val parentName: String,
                             override val groups: Set<GeneratedClass>,
                             override val selfAttributes: XHTMLAttributes,
                             override val selfChildren: Set<GeneratedClass>,
                             override val baseElement: GeneratedClass?) : IXHTMLElement {

    override val isAbstract = false

    override val name = parentName + elementName

    override fun nameSpace(): String {
        return super.nameSpace() + ".elements"
    }

    override fun className(): String = parentName.capitalize() + elementName.capitalize()
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

    override fun imports() = emptySet<String>()

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
        "empty"
    } else if (name == "true") {
        "true_"
    } else if (name == "false") {
        "false_"
    } else if (name == "open") {
        "open_"
    } else {
        name.replace("-", "")
                .replace("/", "_")
                .replace(Regex("^\\d+.*")) { matchResult ->
                    "_" + matchResult.value
                }
                .replace(":", "_")
    }

data class XHTMLDefinitions(val elements: Collection<IXHTMLElement>, val enums: Collection<XHTMLEnumType>,
                            val attributeGroups: Collection<XHTMLAttributeGroup>,
                            val groups: Collection<XHTMLGroup>)

data class XHTMLAttributeGroup(override val name: String, val attributes: XHTMLAttributes) : GeneratedClass {
    override fun imports() = (
            attributes.attributes.map { it.type } + attributes.groups)
            .filter { it.nameSpace().isNotEmpty() && it.nameSpace() != nameSpace() }
            .map { it.fullClassName() }.toSet().sorted()

    override fun nameSpace(): String {
        return super.nameSpace() + ".attributegroups"
    }

    override fun toString(): String {
        return name
    }
}

data class XHTMLAttributes(val attributes: Set<XHTMLAttribute>, val groups: Set<AttributeGroupReference>,
                           val functions: Set<XHTMLEventHandler>) {

    fun add(xhtmlAttributes: XHTMLAttributes): XHTMLAttributes =
            XHTMLAttributes(attributes + xhtmlAttributes.attributes, groups + xhtmlAttributes.groups, functions + xhtmlAttributes.functions)

    val imports = (groups.map { it.fullClassName() } + attributes.flatMap {
        val type = it.type
        if (type is XHTMLEnumType) {
            setOf(type.fullClassName())
        } else {
            emptySet()
        }
    }).toSortedSet().toList()

    fun allAttributes(context: XHTMLReaderContext) : Set<XHTMLAttribute> {
        val result = mutableSetOf<XHTMLAttribute>()
        result.addAll(attributes)
        result.addAll(inheritedAttributes(context))
        return result
    }

    fun inheritedAttributes(context: XHTMLReaderContext) : Set<XHTMLAttribute> {
        val result = mutableSetOf<XHTMLAttribute>()
        groups.forEach { val group = context.getAttributeGroup(it.name)
            if (group != null) {
                result.addAll(group.attributes.allAttributes(context))
            } else {
                println("Cannot find attribute group: ${it.name}")
            }
        }
        return result
    }

    fun allFunctions(context: XHTMLReaderContext) : Set<XHTMLEventHandler> {
        val result = mutableSetOf<XHTMLEventHandler>()
        result.addAll(functions)
        groups.forEach { val group = context.getAttributeGroup(it.name)
            if (group != null) {
                result.addAll(group.attributes.allFunctions(context))
            } else {
                println("Cannot find attribute group: ${it.name}")
            }
        }
        return result
    }

    fun inheritedFunctions(context: XHTMLReaderContext) : Set<XHTMLEventHandler> {
        val result = mutableSetOf<XHTMLEventHandler>()
        groups.forEach { val group = context.getAttributeGroup(it.name)
            if (group != null) {
                result.addAll(group.attributes.allFunctions(context))
            } else {
                println("Cannot find attribute group: ${it.name}")
            }
        }
        return result
    }

}

data class XHTMLToken(val name: String, val tokens: List<String>)

data class XHTMLGroup(override val name: String, val groups: Set<GeneratedClass>, val children: Set<GeneratedClass>) : GeneratedClass {

    override fun imports() = children.map { it.fullClassName() }.toSet()

    override fun nameSpace(): String {
        return super.nameSpace() + ".groups"
    }
}

data class XHTMLEventHandler(val name: String) {

    val eventName = name.substring(2)

    val eventType = if (eventName == "keydown") {
            "org.w3c.dom.events.KeyboardEvent"
        } else {
            "org.w3c.dom.events.Event"
        }


}
