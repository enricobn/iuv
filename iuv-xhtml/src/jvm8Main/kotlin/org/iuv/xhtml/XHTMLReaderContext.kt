package org.iuv.xhtml

class XHTMLReaderContext {
    private val elements = mutableListOf<XHTMLElement>()

    private val types = mutableMapOf<String, GeneratedClass>()

    private val enums = mutableListOf<XHTMLEnumType>()
    private val tokens = mutableMapOf<String, XHTMLToken>()
    private val attributeGroups = mutableMapOf<String, XHTMLAttributeGroup>()
    private val groups = mutableMapOf<String, XHTMLGroup>()

    init {
        types["string"] = SimpleGeneratedClass("String")
        types["token"] = SimpleGeneratedClass("String")
        types["tokens"] = SimpleGeneratedClass("String")
    }

    fun add(enumType: XHTMLEnumType) {
        types[enumType.name] = enumType
        enums.add(enumType)
    }

    fun add(token: XHTMLToken) {
        tokens[token.name] = token
    }

    fun add(group: XHTMLGroup) {
        groups[group.name] = group
    }

    fun add(attributeGroup: XHTMLAttributeGroup) {
        attributeGroups[attributeGroup.name] = attributeGroup
    }

    fun add(element: XHTMLElement) {
        elements.add(element)
    }

    fun getType(name: String) = types[name]

    fun getGroup(name: String) = groups[name]

    fun toDefinitions(): XHTMLDefinitions = XHTMLDefinitions(elements, enums, attributeGroups.values.toList())

}