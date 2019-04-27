package org.iuv.openapi

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.ComposedSchema
import io.swagger.v3.oas.models.media.FileSchema
import io.swagger.v3.oas.models.media.ObjectSchema
import io.swagger.v3.oas.models.media.Schema

private val UNIT_SERIALIZER = IUVAPISerializer("UnitIUVSerializer", "UnitSerializer",
        imports = setOf("kotlinx.serialization.internal.UnitSerializer"))

private val RESERVED_KEYWORDS = setOf("object")

private sealed class ParserType

private data class PrimitiveParserType(val type: String, val serializer: IUVAPISerializer, val imports: List<IUVImport>) : ParserType()

private object MultipartFileParserType : ParserType()

private data class MapParserType(val valueType: ParserType) : ParserType()

private data class RefParserType(val key : String) : ParserType()

private data class AnonymousParserType(val component: ParserComponent) : ParserType()

private data class ArrayParserType(val name : String, val itemsType: ParserType) : ParserType()

private data class ParserProperty(val name: String, val type: ParserType, val optional: Boolean)

private sealed class ParserComponent {

    abstract fun getTypes() : List<ParserType>

    abstract val key : String

    abstract val name : String
}

private data class ConcreteParserComponent(override val key : String, override val name: String, val properties: List<ParserProperty>) : ParserComponent() {
    override fun getTypes(): List<ParserType> {
        return properties.map { it.type }
    }
}

private data class AliasParserComponent(override val key : String, override val name: String, val alias: ParserType) : ParserComponent() {
    override fun getTypes(): List<ParserType> {
        return listOf(alias)
    }
}

class OpenAPIParser(private val api: OpenAPI, private val context: OpenAPIWriteContext) {

    fun components(): Map<String, IUVAPIComponent> {
        val components = api.components.schemas.map { toParserComponent(it) }

        val anonymousParserTypes = components.flatMap { component ->
            component.getTypes().flatMap { getAnonymousParserTypes(it) }
        }

        val componentsMap = (components + anonymousParserTypes.map { it.component })
                .map { it.key to it }
                .toMap()

        return componentsMap.entries.map { it.key to toIUVAPIComponent(componentsMap, it.value) }.toMap()
    }

    private fun toIUVAPIComponent(components: Map<String,ParserComponent>, component: ParserComponent) =
            when (component) {
                is AliasParserComponent ->
                    IUVAPIComponent(component.name, emptyList(), aliasFor = toIUVAPIType(components, component.alias), key = component.key)
                is ConcreteParserComponent ->
                    IUVAPIComponent(component.name, component.properties.map { toIUVAPIProperty(components, it) }, key = component.key)
            }

    private fun toIUVAPIProperty(components: Map<String,ParserComponent>, property: ParserProperty) =
            IUVAPIComponentProperty(property.name, toIUVAPIType(components, property.type), property.optional)

    private fun toIUVAPIType(components: Map<String,ParserComponent>, type: ParserType): IUVAPIType =
        when (type) {
            is PrimitiveParserType -> IUVAPIType(type.type, type.serializer, type.imports)
            MultipartFileParserType -> IUVAPIType("MultipartFile",
                    IUVAPISerializer("", "", imports = emptySet()),
                    listOf(
                            IUVImport("org.iuv.core.MultiPartData", setOf(IUVImportType.CLIENT_IMPL)),
                            IUVImport("org.iuv.core.MultipartFile", setOf(IUVImportType.CLIENT, IUVImportType.CLIENT_IMPL)),
                            IUVImport("org.springframework.web.multipart.MultipartFile",
                                    setOf(IUVImportType.CONTROLLER))
                    )
            )
            is MapParserType -> {
                val mapType = toIUVAPIType(components, type.valueType)
                IUVAPIType("Map<String, $mapType>",
                        IUVAPISerializer("MapString${mapType.serializer.name}",
                                "HashMapSerializer(StringSerializer,${mapType.serializer.code})",
                                imports = setOf("kotlinx.serialization.internal.HashMapSerializer",
                                        "kotlinx.serialization.internal.StringSerializer") + mapType.serializer.imports),
                        mapType.imports)
            }
            is RefParserType -> {
                val component = components[type.key] ?: throw UnsupportedOpenAPISpecification("Cannot fine component " + type.key)
                toComponentType(component)
            }
            is AnonymousParserType -> {
                val component = type.component
                toComponentType(component)
            }
            is ArrayParserType -> {
                val itemsType = toIUVAPIType(components, type.itemsType)

                IUVAPIType("List<$itemsType>",
                        IUVAPISerializer("List${itemsType.serializer.name}", "ArrayListSerializer(${itemsType.serializer.code})",
                                imports = setOf("kotlinx.serialization.internal.ArrayListSerializer") + itemsType.serializer.imports),
                        itemsType.imports, itemsType.innerComponent)
            }
        }

    private fun toComponentType(component: ParserComponent): IUVAPIType {
        return IUVAPIType(component.name, IUVAPISerializer("${component.name}IUVSerializer", "${component.name}::class.serializer()",
                imports = setOf("kotlinx.serialization.serializer")),
                listOf(IUVImport(context.modelPackage + "." + component.name,
                        setOf(IUVImportType.CONTROLLER, IUVImportType.CLIENT, IUVImportType.CLIENT_IMPL))))
    }

    private fun getAnonymousParserTypes(type : ParserType) : List<AnonymousParserType> =
        when (type) {
            is AnonymousParserType -> type.component.getTypes().flatMap { getAnonymousParserTypes(it) } + type
            is ArrayParserType -> getAnonymousParserTypes(type.itemsType)
            else -> emptyList()
        }

    private fun toParserComponent(schemaEntry: Map.Entry<String, Schema<*>>) : ParserComponent {
        val schema = schemaEntry.value

        val componentProperties = getProperties(schema)
        val schemaKey = schemaEntry.key

        val name = safeName(schemaKey).capitalize()

        val required = schema.required

        return if (schema is ArraySchema) {
                AliasParserComponent(schemaKey, name, ArrayParserType(name, schema.items.resolveType(schemaKey, name)))
            } else {
                val properties = getProperties(componentProperties, schemaKey, name, required)

                ConcreteParserComponent(schemaKey, name, properties)
            }
    }

    private fun getProperties(componentProperties: Map<String, Schema<*>>, parentKey: String, parentName: String,
                              required: List<String>? = null) =
        componentProperties
            .map {
                try {
                    val name = safeName(parentName + it.key.capitalize()).capitalize()
                    val type = it.value.resolveType(parentKey + it.key, name)

                    ParserProperty(safeName(it.key), type, !(required?.contains(it.key) ?: false))
                } catch (e: Exception) {
                    throw UnsupportedOpenAPISpecification("Cannot create component ${it.key}.", e)
                }
            }

    private fun safeName(name : String) =
            if (RESERVED_KEYWORDS.contains(name))
                "`$name`"
            else
                name.mapIndexed { i, ch -> if ( i == 0 && ch.isDigit()) "n&ch" else if (ch.isLetterOrDigit()) ch.toString() else "_" }
                    .joinToString("")
                    .split("_")
                    .mapIndexed {i, s -> if (i == 0) s else s.capitalize() }
                    .joinToString("")

    private fun getProperties(schema: Schema<*>) : Map<String, Schema<*>> =
            if (schema.`$ref` != null) {
                // TODO handle reference to another api file
                val refSchema = api.components.schemas[schema.`$ref`.split("/").last()]
                getProperties(refSchema!!)
            } else if (schema is ComposedSchema) {
                if (schema.allOf != null) {
                    schema.allOf
                            .flatMap { getProperties(it).entries }
                            .map { Pair(it.key, it.value) }
                            .toMap()
                } else {
                    throw UnsupportedOpenAPISpecification("Error reading properties of schema ${schema.name}, only allOf is supported.")
                }
            } else {
                schema.properties ?: mapOf()
            }

    private fun Schema<*>.resolveType(parentKey: String, parent: String) : ParserType {
        if (type != null) {
            when {
                this is ArraySchema -> {
                    val itemsType = items.resolveType(parentKey + "items", parent + "Items")
                    return ArrayParserType(parent, itemsType)
                }
                this is FileSchema -> return MultipartFileParserType
                this is ObjectSchema ->
                    if (additionalProperties != null) {
                        additionalProperties.let {
                            if (it is Boolean) {
                                throw UnsupportedOpenAPISpecification("Unknown type.")
                            } else if (it is Schema<*>) {
                                val mapType = it.resolveType(parentKey, parent)
                                return MapParserType(mapType)
                            } else {
                                throw UnsupportedOpenAPISpecification("Unknown additionaproperties type: " + additionalProperties::class)
                            }
                        }
                    } else if (properties != null) {
                            val properties = getProperties(properties, parentKey, parent)
                            val component = ConcreteParserComponent(parentKey, parent, properties)
                            return AnonymousParserType(component)
                    } else {
                        // TODO enums?
                        return toPrimitypeType("string", null)
                    }
                this is ComposedSchema -> {
                    return allOf.filter { it.`$ref` != null }
                            .firstOrNull()?.resolveType(parentKey, parent) ?: throw UnsupportedOpenAPISpecification("Cannot resolve type.")
                }
            }

            return toPrimitypeType(type, format)
        }

        if (`$ref` == null) {
            return PrimitiveParserType("Unit", UNIT_SERIALIZER, listOf())
        }

        val refSimpleName = `$ref`.split("/").last()

        return RefParserType(refSimpleName)
    }

    private fun toPrimitypeType(type: String, format: String?) =
            when (type) {
                "string" -> PrimitiveParserType("String", IUVAPISerializer("StringIUVSerializer", "StringSerializer",
                        imports = setOf("kotlinx.serialization.internal.StringSerializer")), listOf())
                "integer", "number" ->
                    if (format == "int64") {
                        PrimitiveParserType("Long", IUVAPISerializer("LongIUVSerializer", "LongSerializer",
                                imports = setOf("kotlinx.serialization.internal.LongSerializer")), listOf())
                    } else {
                        PrimitiveParserType("Int", IUVAPISerializer("IntIUVSerializer", "IntSerializer",
                                imports = setOf("kotlinx.serialization.internal.IntSerializer")), listOf())
                    }
                "boolean" -> PrimitiveParserType("Boolean", IUVAPISerializer("BooleanIUVSerializer", "BooleanSerializer",
                        imports = setOf("kotlinx.serialization.internal.BooleanSerializer")), listOf())
                "file" -> PrimitiveParserType("MultipartFile", IUVAPISerializer("BooleanIUVSerializer", "MultipartFileSerializer",
                        imports = setOf("kotlinx.serialization.internal.MultipartFileSerializer")), listOf())
                else -> throw UnsupportedOpenAPISpecification("Unknown type '$type'.")
            }

}