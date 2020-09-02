package org.iuv.xhtml

class XHTMLReaderContext {
    companion object {
        val STRING = SimpleGeneratedClass("String", "types")
        val INTEGER = SimpleGeneratedClass("Int", "types")
        val FLOAT = SimpleGeneratedClass("Float", "types")
        val BOOLEAN = SimpleGeneratedClass("Boolean", "types")
    }

    private val elements = mutableListOf<IXHTMLElement>()
    private val types = mutableMapOf<String, GeneratedClass>()
    private val enums = mutableListOf<XHTMLEnumType>()
    private val tokens = mutableMapOf<String, XHTMLToken>()
    private val attributeGroups = mutableMapOf<String, XHTMLAttributeGroup>()
    private val groups = mutableMapOf<String, XHTMLGroup>()

    init {
        types["string"] = STRING
        types["float"] = FLOAT
        types["integer"] = INTEGER
        types["boolean"] = BOOLEAN
        types["token"] = STRING
        types["tokens"] = STRING
        types["uri"] = STRING
        types["nonNegativeInteger"] = INTEGER
        types["positiveInteger"] = INTEGER
        types["positiveFloat"] = FLOAT
        types["nonNegativeFloat"] = FLOAT
        types["nonEmptyString"] = STRING // TODO can I do something for being non empty
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

    fun add(element: IXHTMLElement) {
        elements.add(element)
    }

    fun addType(type: SimpleGeneratedClass) {
        types[type.originalName] = type
    }

    fun addType(name: String, type: SimpleGeneratedClass) {
        types[name] = type
    }

    fun getType(name: String) = types[name]

    fun getGroup(name: String) = groups[name]

    fun toDefinitions(): XHTMLDefinitions = XHTMLDefinitions(elements, enums, attributeGroups.values.toList(), groups.values.toList())

}