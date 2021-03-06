package org.iuv.openapi

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.*

private val UNIT_SERIALIZER = IUVAPISerializer("UnitSerializer",
        imports = setOf("kotlinx.serialization.internal.UnitSerializer"))

private val RESERVED_KEYWORDS = setOf("object")

sealed class ParserType

data class PrimitiveParserType(val type: String, val serializer: IUVAPISerializer, val imports: Set<IUVImport>) : ParserType()

object MultipartFileParserType : ParserType()

data class MapParserType(val valueType: ParserType) : ParserType()

data class RefParserType(val key: String) : ParserType()

data class CustomParserType(val component: ParserComponent) : ParserType()

data class ArrayParserType(val name: String, val itemsType: ParserType) : ParserType()

data class EnumParserType(val name: String) : ParserType()

data class ParserProperty(val key: String, val name: String, val type: ParserType, val required: Boolean, val description: String?, val default: String?)

sealed class ParserComponent {

    abstract fun getUsedTypes(): List<ParserType>

    abstract val key: String

    abstract val name: String

    abstract val type: ParserType

    abstract val description: String?
}

data class ConcreteParserComponent(override val key: String, override val name: String, val properties: List<ParserProperty>, override val description: String?) : ParserComponent() {
    override fun getUsedTypes(): List<ParserType> {
        return properties.map { it.type }
    }

    override val type: ParserType
        get() = CustomParserType(this)

}

data class AliasParserComponent(override val key: String, override val name: String, val alias: ParserType) : ParserComponent() {
    override fun getUsedTypes(): List<ParserType> {
        return listOf(alias)
    }

    override val type: ParserType
        get() = alias

    override val description: String?
        get() = null
}

data class EnumParserComponent(override val key: String, override val name: String, override val description: String?,
                               val values: List<String>) : ParserComponent() {
    override fun getUsedTypes(): List<ParserType> {
        return emptyList()
    }

    override val type: ParserType
        get() = EnumParserType(name)

}

class OpenAPIParser(private val api: OpenAPI) {

    fun components(): List<ParserComponent> {
        val components = api.components.schemas.map { toParserComponent(it) }

        val anonymousParserTypes = components.flatMap { component ->
            component.getUsedTypes().flatMap { getAnonymousParserTypes(it) }
        }

        return (components + anonymousParserTypes.map { it.component })
    }

    private fun getAnonymousParserTypes(type: ParserType): List<CustomParserType> =
            when (type) {
                is CustomParserType -> type.component.getUsedTypes().flatMap { getAnonymousParserTypes(it) } + type
                is ArrayParserType -> getAnonymousParserTypes(type.itemsType)
                else -> emptyList()
            }

    private fun toParserComponent(schemaEntry: Map.Entry<String, Schema<*>>): ParserComponent {
        val schema = schemaEntry.value

        val componentProperties = getProperties(api, schema)
        val schemaKey = schemaEntry.key

        val name = safeName(schemaKey).capitalize()

        val required = schema.required

        try {
            return if (schema is ArraySchema) {
                AliasParserComponent(schemaKey, name, ArrayParserType(name, schema.items.resolveType(schemaKey, name)))
            } else if (schema is MapSchema) {
                if (schema.additionalProperties != null) {
                    AliasParserComponent(schemaKey, name, resolveAdditionalProperties(schema, schemaKey, name))
                } else
                    throw UnsupportedOpenAPISpecification("No additional properties specified.")
            } else {
                val properties = getProperties(componentProperties, schemaKey, name, required)

                if (properties.isEmpty()) {
                    if (schema.`$ref` != null) {
                        AliasParserComponent(schemaKey, name, RefParserType(schema.`$ref`.split("/").last()))
                    } else if (schema.enum.isNotEmpty()) {
                        EnumParserComponent(schemaKey, name, schema.description, schema.enum.map { it.toString() })
                    } else
                        throw UnsupportedOpenAPISpecification("No properties and no ref specified.")
                } else {
                    ConcreteParserComponent(schemaKey, name, properties, schema.description)
                }
            }
        } catch (e: Exception) {
            throw UnsupportedOpenAPISpecification("Cannot parse component $schemaKey", e)
        }
    }

}

fun Schema<*>.resolveType(parentKey: String, parent: String): ParserType {
    if (type != null) {
        when {
            this is ArraySchema -> {
                val itemsType = items.resolveType(parentKey + "items", parent + "Items")
                return ArrayParserType(parent, itemsType)
            }
            this is MapSchema -> {
                val itemsType = (additionalProperties as Schema<*>).resolveType(parentKey + "items", parent + "Items")
                return MapParserType(itemsType)
            }
            this is FileSchema -> return MultipartFileParserType
            this is ObjectSchema ->
                if (additionalProperties != null) {
                    return resolveAdditionalProperties(this, parentKey, parent)
                } else if (properties != null) {
                    val properties = getProperties(properties, parentKey, parent)

                    if (properties.isEmpty()) {
                        return MapParserType(toPrimitiveType("string", null))
                    }

                    val component = ConcreteParserComponent(parentKey, parent, properties, description)
                    return CustomParserType(component)
                } else {
                    // TODO enums?
                    return toPrimitiveType("string", null)
                }
            this is ComposedSchema -> {
                val filtered = allOf.filter { it.`$ref` != null }

                if (filtered.isEmpty())
                    throw UnsupportedOpenAPISpecification("Composed schema, only one allOf is supported.")

                return filtered.first().resolveType(parentKey, parent)
            }
        }

        return toPrimitiveType(type, format)
    }

    if (`$ref` == null) {
        return PrimitiveParserType("Unit", UNIT_SERIALIZER, emptySet())
    }

    val refSimpleName = `$ref`.split("/").last()

    return RefParserType(refSimpleName)
}

private fun getProperties(componentProperties: Map<String, Schema<*>>, parentKey: String, parentName: String,
                          required: List<String>? = null) =
        componentProperties
                .map {
                    try {
                        val name = safeName(parentName + it.key.capitalize()).capitalize()
                        val type = it.value.resolveType(parentKey + it.key, name)
                        val def = getDefault(it.value.default)

                        ParserProperty(it.key, safeName(it.key), type, (required?.contains(it.key)
                                ?: false), it.value.description, def)
                    } catch (e: Exception) {
                        throw UnsupportedOpenAPISpecification("Cannot create component ${it.key}.", e)
                    }
                }

fun getDefault(value: Any?) =
        if (value is Boolean || value is Number)
            value.toString()
        else if (value is String)
            "\"$value\""
        else
            null


private fun safeName(name: String) =
        if (RESERVED_KEYWORDS.contains(name))
            "`$name`"
        else
            name.mapIndexed { i, ch -> if (i == 0 && ch.isDigit()) "n$ch" else if (ch.isLetterOrDigit()) ch.toString() else "_" }
                    .joinToString("")
                    .split("_")
                    .mapIndexed { i, s -> if (i == 0) s else s.capitalize() }
                    .joinToString("")

private fun toPrimitiveType(type: String, format: String?) =
        when (type) {
            "string" -> PrimitiveParserType("String", IUVAPISerializer("String.serializer()",
                    imports = setOf("kotlinx.serialization.builtins.serializer")), emptySet())
            "integer", "number" ->
                if (format == "int64") {
                    PrimitiveParserType("Long", IUVAPISerializer("Long.serializer()",
                            imports = setOf("kotlinx.serialization.builtins.serializer")), emptySet())
                } else {
                    PrimitiveParserType("Int", IUVAPISerializer("Int.serializer()",
                            imports = setOf("kotlinx.serialization.builtins.serializer")), emptySet())
                }
            "boolean" -> PrimitiveParserType("Boolean", IUVAPISerializer("Boolean.serializer()",
                    imports = setOf("kotlinx.serialization.builtins.serializer")), emptySet())
            "file" -> PrimitiveParserType("MultipartFile", IUVAPISerializer("MultipartFileSerializer",
                    imports = setOf("kotlinx.serialization.internal.MultipartFileSerializer")), emptySet())
            else -> throw UnsupportedOpenAPISpecification("Unknown type '$type'.")
        }

private fun resolveAdditionalProperties(schema: Schema<*>, parentKey: String, parent: String): MapParserType {
    val additionalProperties = schema.additionalProperties
    additionalProperties.let {
        if (it is Boolean) {
            throw UnsupportedOpenAPISpecification("Unsupported boolean additional properties.")
        } else if (it is Schema<*>) {
            val mapType = it.resolveType(parentKey, parent)
            return MapParserType(mapType)
        } else {
            throw UnsupportedOpenAPISpecification("Unknown additional properties type: " + additionalProperties::class)
        }
    }
}

private fun getProperties(api: OpenAPI, schema: Schema<*>): Map<String, Schema<*>> =
        if (schema.`$ref` != null) {
            // TODO handle reference to another api file
            val refSchema = api.components.schemas[schema.`$ref`.split("/").last()]
            getProperties(api, refSchema!!)
        } else if (schema is ComposedSchema) {
            if (schema.allOf != null) {
                schema.allOf
                        .flatMap { getProperties(api, it).entries }
                        .map { Pair(it.key, it.value) }
                        .toMap()
            } else {
                throw UnsupportedOpenAPISpecification("Error reading properties of schema ${schema.name}, only allOf is supported.")
            }
        } else {
            schema.properties ?: emptyMap()
        }