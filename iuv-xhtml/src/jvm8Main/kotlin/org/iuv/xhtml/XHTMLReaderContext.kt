package org.iuv.xhtml

class XHTMLReaderContext {
    companion object {
        val STRING = NativeClass("String")
        val INTEGER = NativeClass("Int")
        val FLOAT = NativeClass("Float")
        val BOOLEAN = NativeClass("Boolean")
    }

    private val elements = mutableMapOf<String,IXHTMLElement>()
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
        elements[element.name] = element
    }

    fun addType(type: SimpleGeneratedClass) {
        types[type.originalName] = type
    }

    fun addType(name: String, type: GeneratedClass) {
        types[name] = type
    }

    fun getType(name: String) = types[name]

    fun getGroup(name: String) = groups[name]

    fun getElement(name: String) = elements[name]

    fun getAttributeGroup(name: String) = attributeGroups[name]

    fun toDefinitions(): XHTMLDefinitions = XHTMLDefinitions(elements.values, enums, getAttributeGroups(), getGroups())

    private fun getAttributeGroups(): Collection<XHTMLAttributeGroup> {
        var result: Collection<XHTMLAttributeGroup> = attributeGroups.values
        while (true) {
            val collection = mergeAttributeGroups(result)
            if (collection != null) {
                result = collection
            } else {
                return result
            }
        }
    }

    /**
     * tries to merge attribute groups finding common attributes.
     * @return the new attributes groups if a merge has benn done, otherwise null
     */
    private fun mergeAttributeGroups(groups: Collection<XHTMLAttributeGroup>): Collection<XHTMLAttributeGroup>? {
        val result = groups.associateBy { it.name }.toMutableMap()

        for (it in groups) {
            for (other in groups) {
                if (other.name != it.name) {
                    val attributes = it.attributes.attributes
                    val commonAttributes = attributes.filter { other.attributes.attributes.contains(it) }.toSet()
                    if (commonAttributes.isNotEmpty()) {
                        if (commonAttributes.size == attributes.size) {
                            val xhtmlAttributeGroup = addAttributeGroup(it, other)
                            if (xhtmlAttributeGroup != null) {
                                result[xhtmlAttributeGroup.name] = xhtmlAttributeGroup
                                return result.values
                            }
                        } else if (commonAttributes.size == other.attributes.attributes.size) {
                            val xhtmlAttributeGroup = addAttributeGroup(other, it)
                            if (xhtmlAttributeGroup != null) {
                                result[xhtmlAttributeGroup.name] = xhtmlAttributeGroup
                                return result.values
                            }
                        } else {
                            val newGroup = XHTMLAttributeGroup(it.name + other.name.capitalize(), XHTMLAttributes(commonAttributes, emptySet(), emptySet()))

                            val itNew = addAttributeGroup(newGroup, it)
                            val otherNew = addAttributeGroup(newGroup, other)

                            if (itNew != null || otherNew != null) {
                                result[newGroup.name] = newGroup
                            }

                            if (itNew != null) {
                                result[itNew.name] = itNew
                            }

                            if (otherNew != null) {
                                result[otherNew.name] = otherNew
                            }
                            return result.values
                        }

                    }
                }
            }
        }
        return null
    }

    private fun getGroups(): Collection<XHTMLGroup> {
        var result: Collection<XHTMLGroup> = groups.values
        while (true) {
            val collection = mergeGroups(result)
            if (collection != null) {
                result = collection
            } else {
                return result
            }
        }
    }

    /**
     * tries to merge groups finding common children.
     * @return the new groups if a merge has benn done, otherwise null
     */
    private fun mergeGroups(groups: Collection<XHTMLGroup>): Collection<XHTMLGroup>? {
        val result = groups.associateBy { it.name }.toMutableMap()

        for (it in groups) {
            for (other in groups) {
                if (other.name != it.name) {
                    val children = it.children
                    val commonChildren = children.filter { other.children.contains(it) }.toSet()
                    if (commonChildren.isNotEmpty()) {
                        if (commonChildren.size == children.size) {
                            val group = addGroup(it, other)
                            if (group != null) {
                                result[group.name] = group
                                return result.values
                            }
                        } else if (commonChildren.size == other.children.size) {
                            val group = addGroup(other, it)
                            if (group != null) {
                                result[group.name] = group
                                return result.values
                            }
                        } else {
                            val newGroup = XHTMLGroup(it.name + other.name.capitalize(), emptySet(), commonChildren)

                            val itNew = addGroup(newGroup, it)
                            val otherNew = addGroup(newGroup, other)

                            if (itNew != null || otherNew != null) {
                                result[newGroup.name] = newGroup
                            }

                            if (itNew != null) {
                                result[itNew.name] = itNew
                            }

                            if (otherNew != null) {
                                result[otherNew.name] = otherNew
                            }
                            return result.values
                        }

                    }
                }
            }
        }
        return null
    }

    /**
     * adds a group as a parent, removing the attributes and functions
     *
     * @return null if the group is already a parent
     */
    private fun addAttributeGroup(groupToAdd: XHTMLAttributeGroup, to: XHTMLAttributeGroup): XHTMLAttributeGroup? =
            if (to.attributes.groups.any { it.name == groupToAdd.name }) {
                null
            } else {
                to.copy(attributes = to.attributes.copy(
                        functions = to.attributes.functions.minus(groupToAdd.attributes.functions),
                        attributes = to.attributes.attributes.minus(groupToAdd.attributes.attributes),
                        groups = to.attributes.groups + AttributeGroupReference(groupToAdd.name)))
            }

    /**
     * adds a group as a parent removing the children
     *
     * @return null if the group is already a parent
     */
    private fun addGroup(groupToAdd: XHTMLGroup, to: XHTMLGroup): XHTMLGroup? =
            if (to.groups.any { it.name == groupToAdd.name }) {
                null
            } else {
                to.copy(children = to.children.minus(groupToAdd.children), groups = to.groups + SimpleGeneratedClass(groupToAdd.name, "groups"))
            }
}