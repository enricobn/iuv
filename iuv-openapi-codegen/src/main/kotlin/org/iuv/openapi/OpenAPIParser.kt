package org.iuv.openapi

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.ComposedSchema
import io.swagger.v3.oas.models.media.FileSchema
import io.swagger.v3.oas.models.media.ObjectSchema
import io.swagger.v3.oas.models.media.Schema

private val UNIT_SERIALIZER = IUVAPISerializer("UnitIUVSerializer", "UnitSerializer",
        imports = setOf("kotlinx.serialization.internal.UnitSerializer"))

class OpenAPIParser(private val api: OpenAPI, private val context: OpenAPIWriteContext) {

    fun components() = api.components.schemas.flatMap { toIUVAPIComponents(it).toList() }.toMap()

    private fun toIUVAPIComponents(schemaEntry: Map.Entry<String, Schema<*>>) : Map<String,IUVAPIComponent> {
        val components = mutableMapOf<String,IUVAPIComponent>()

        val schema = schemaEntry.value

        val componentProperties = getProperties(schema)
        val schemaKey = schemaEntry.key

        val name = schemaKey.split("-", ".", "_").joinToString("") { it.capitalize() }

        val required = schema.required

        if (schema is ArraySchema) {
            components[schemaKey] = IUVAPIComponent(name, emptyList(), aliasFor = schema.resolveType(emptyMap(), name, schemaKey), key = schemaKey)
        } else {
            val properties = getProperties(componentProperties, components, name, schemaKey, required)

            components[schemaKey] = IUVAPIComponent(name, properties, key = schemaKey)
        }
        return components + components.flatMap { getInnerComponents(it.value) }.map { it.key to it }
    }

    private fun getInnerComponents(component: IUVAPIComponent): List<IUVAPIComponent> {
        val list = component.properties
                .mapNotNull { it.type.innerComponent } +
                if (component.aliasFor?.innerComponent != null)
                    getInnerComponents(component.aliasFor.innerComponent) + component.aliasFor.innerComponent
                else emptyList()
        return list + list.flatMap { getInnerComponents(it) }
    }

    private fun getProperties(componentProperties: Map<String, Schema<*>>, components: Map<String, IUVAPIComponent>, parentName: String, parentKey: String,
                              required: List<String>? = null): List<IUVAPIComponentProperty> {
        return componentProperties
                .map {
                    try {
                        val type = it.value.resolveType(components, parentName + it.key.capitalize(), parentKey + it.key)

                        IUVAPIComponentProperty(it.key, type, !(required?.contains(it.key)
                                ?: false))
                    } catch (e: Exception) {
                        throw UnsupportedOpenAPISpecification("Cannot create component ${it.key}.", e)
                    }
                }
    }

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

    fun Schema<*>.resolveType(components: Map<String,IUVAPIComponent>, parent: String?, parentKey: String) : IUVAPIType {
        if (type != null) {
            when {
                this is ArraySchema -> {
                    val itemsType = items.resolveType(components, parent + "Items", parentKey + "items")
                    return IUVAPIType("List<$itemsType>",
                            IUVAPISerializer("List${itemsType.serializer.name}", "ArrayListSerializer(${itemsType.serializer.code})",
                                    imports = setOf("kotlinx.serialization.internal.ArrayListSerializer") + itemsType.serializer.imports),
                            itemsType.imports, itemsType.innerComponent)
                }
                this is FileSchema -> return IUVAPIType("MultipartFile",
                        IUVAPISerializer("", "", imports = emptySet()),
                        listOf(
                                IUVImport("org.iuv.core.MultiPartData", setOf(IUVImportType.CLIENT_IMPL)),
                                IUVImport("org.iuv.core.MultipartFile", setOf(IUVImportType.CLIENT, IUVImportType.CLIENT_IMPL)),
                                IUVImport("org.springframework.web.multipart.MultipartFile",
                                        setOf(IUVImportType.CONTROLLER))
                        )
                )
                this is ObjectSchema ->
                    if (additionalProperties != null) {
                        additionalProperties.let {
                            if (it is Boolean) {
                                throw UnsupportedOpenAPISpecification("Unknown type.")
                            } else if (it is Schema<*>) {
                                val mapType = it.resolveType(components, parent, parentKey)
                                return IUVAPIType("Map<String, $mapType>",
                                        IUVAPISerializer("MapString${mapType.serializer.name}",
                                                "HashMapSerializer(StringSerializer,${mapType.serializer.code})",
                                                imports = setOf("kotlinx.serialization.internal.HashMapSerializer",
                                                        "kotlinx.serialization.internal.StringSerializer") + mapType.serializer.imports),
                                        mapType.imports)
                            } else {
                                throw UnsupportedOpenAPISpecification("Unknown additionaproperties type: " + additionalProperties::class)
                            }
                        }
                    } else if (parent != null) {
                        if (properties != null) {
                            val properties = getProperties(properties, components, parent, parentKey)
                            val iuvapiComponent = IUVAPIComponent(parent, properties, key = parentKey)
                            return toComponentType(parent, iuvapiComponent)
                        } else {
                            // TODO enums?
                            return toKotlinType("string", null)
                        }
                    } else {
                        throw UnsupportedOpenAPISpecification("Cannot resolve type.")
                    }
                this is ComposedSchema -> {
                    return allOf.filter { it.`$ref` != null }
                            .firstOrNull()?.resolveType(components, parent, parentKey) ?: throw UnsupportedOpenAPISpecification("Cannot resolve type.")
                }
            }

            return toKotlinType(type, format)
        }

        if (`$ref` == null) {
            return IUVAPIType("Unit", UNIT_SERIALIZER, listOf())
        }

        val refSimpleName = `$ref`.split("/").last()
        var iuvapiComponent = components[refSimpleName]

        if (iuvapiComponent == null) {
            val schema = api.components.schemas[refSimpleName]
            iuvapiComponent = toIUVAPIComponents(Pair(refSimpleName, schema!!).toEntry())[refSimpleName]

            if (iuvapiComponent == null)
                throw UnsupportedOpenAPISpecification("Cannot find component $`$ref`")
        }

        val type = iuvapiComponent.name
        return toComponentType(type, iuvapiComponent)
    }

    private fun toComponentType(type: String, innerComponent: IUVAPIComponent): IUVAPIType {
        return IUVAPIType(type, IUVAPISerializer("${type}IUVSerializer", "$type::class.serializer()",
                imports = setOf("kotlinx.serialization.serializer")),
                listOf(IUVImport(context.modelPackage + "." + type,
                        setOf(IUVImportType.CONTROLLER, IUVImportType.CLIENT, IUVImportType.CLIENT_IMPL))),
                innerComponent)
    }

    private fun <A,B> Pair<A,B>.toEntry() = object : Map.Entry<A, B> {
        override val key: A
            get() = first
        override val value: B
            get() = second
    }

    private fun toKotlinType(type: String, format: String?) =
            when (type) {
                "string" -> IUVAPIType("String", IUVAPISerializer("StringIUVSerializer", "StringSerializer",
                        imports = setOf("kotlinx.serialization.internal.StringSerializer")), listOf())
                "integer", "number" ->
                    if (format == "int64") {
                        IUVAPIType("Long", IUVAPISerializer("LongIUVSerializer", "LongSerializer",
                                imports = setOf("kotlinx.serialization.internal.LongSerializer")), listOf())
                    } else {
                        IUVAPIType("Int", IUVAPISerializer("IntIUVSerializer", "IntSerializer",
                                imports = setOf("kotlinx.serialization.internal.IntSerializer")), listOf())
                    }
                "boolean" -> IUVAPIType("Boolean", IUVAPISerializer("BooleanIUVSerializer", "BooleanSerializer",
                        imports = setOf("kotlinx.serialization.internal.BooleanSerializer")), listOf())
                "file" -> IUVAPIType("MultipartFile", IUVAPISerializer("BooleanIUVSerializer", "MultipartFileSerializer",
                        imports = setOf("kotlinx.serialization.internal.MultipartFileSerializer")), listOf())
                else -> throw UnsupportedOpenAPISpecification("Unknown type '$type'.")
            }


}