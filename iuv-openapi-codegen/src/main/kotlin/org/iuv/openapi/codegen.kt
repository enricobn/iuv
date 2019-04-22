package org.iuv.openapi

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheResolver
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.ComposedSchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.parameters.QueryParameter
import io.swagger.v3.parser.OpenAPIV3Parser
import org.slf4j.LoggerFactory
import java.io.InputStreamReader
import java.io.Reader
import java.io.Writer
import java.net.URL

private const val JSON = "application/json"
private const val ALL_CONTENTS = "*/*"
private const val FORM_URL_ENCODED = "application/x-www-form-urlencoded"
private val UNIT_SERIALIZER = IUVAPISerializer("UnitIUVSerializer", "UnitSerializer", `import` = "kotlinx.serialization.internal.UnitSerializer")

interface Last {
    var last: Boolean
}

data class OpenAPIWriteContext(val controllerPackage: String, val clientPackage: String, val modelPackage: String)

data class IUVAPIType(val type: String, val serializer: IUVAPISerializer, val imports: List<IUVImport>) {

    override fun toString() = type

}

data class IUVAPISerializer(val name: String, val code: String, override var last: Boolean = false, val `import`: String?) : Last

data class IUVAPIComponentProperty(val name: String, val type: IUVAPIType, val optional: Boolean, override var last: Boolean = false) : Last

data class IUVAPIComponent(val name: String, val properties: List<IUVAPIComponentProperty>, override var last: Boolean = false) : Last {
    init {
        properties.calculateLast()
    }
}

enum class ParameterType(val fullClassName: String) {
    PATH_VARIABLE("org.springframework.web.bind.annotation.PathVariable"),
    REQUEST_PARAM("org.springframework.web.bind.annotation.RequestParam"),
    REQUEST_BODY("org.springframework.web.bind.annotation.RequestBody"),
    FORM_PARAM("org.springframework.web.bind.annotation.RequestParam")
}

data class IUVAPIParameter(val name: String, val type: IUVAPIType, val parameterType: ParameterType, override var last: Boolean = false) : Last {
    val pathVariable = parameterType == ParameterType.PATH_VARIABLE
    val requestParam = parameterType == ParameterType.REQUEST_PARAM || parameterType == ParameterType.FORM_PARAM
    val requestBody = parameterType == ParameterType.REQUEST_BODY
    val formParam = parameterType == ParameterType.FORM_PARAM
    val clientQueryParam = parameterType == ParameterType.REQUEST_PARAM
}

enum class IUVAPIOperationType(val fullClassName: String, val clientMethod: String) {
    Get("org.springframework.web.bind.annotation.GetMapping", "GET"),
    Post("org.springframework.web.bind.annotation.PostMapping", "POST"),
    Put("org.springframework.web.bind.annotation.PutMapping", "PUT"),
    Delete("org.springframework.web.bind.annotation.DeleteMapping", "DELETE");

    fun annotation(path: String) =
        fullClassName.split('.').last() + "(\"$path\")"
}

data class IUVAPIOperation(val path: String, val op: IUVAPIOperationType, val id: String, val parameters: List<IUVAPIParameter>, val resultType: IUVAPIType?,
                           val bodyType: IUVAPIType?, override var last: Boolean = false) : Last {

    init {
        parameters.calculateLast()
    }

    val operationAnnotation = op.annotation(path)

    fun name() : String {
        var capitalizeNext = false
        val result = StringBuilder()
        id.forEach {
            capitalizeNext = if (it == ' ')
                true
            else {
                result.append(if (capitalizeNext) it.toUpperCase() else it)
                false
            }
        }
        return result.toString()
    }

    val hasFormData = parameters.any { it.formParam }

    val formData = parameters.filter { it.formParam }.map { it.copy() }.calculateLast()

    val hasClientQueryParams = parameters.any { it.clientQueryParam }

    val clientQueryParams = parameters.filter{ it.clientQueryParam }.map { it.copy() }.calculateLast()
}

data class IUVAPIPath(val path: String, val operations: List<IUVAPIOperation>) {
    init {
        operations.calculateLast()
    }

    val pathSubst =
        Regex("(\\{.*?})").replace(path) {
            val group = it.groups[1]
            "\$" + group?.value?.drop(1)?.dropLast(1)
        }

}

data class IUVAPI(val name: String, val paths: List<IUVAPIPath>, val components: List<IUVAPIComponent>,
                  val imports: List<IUVImport>, val baseUrl: String) {

    init {
        components.calculateLast()
    }

    val apiImports = imports.filter { it.api }.map { it.copy() }.calculateLast()

    val controllerImports = imports.filter { it.controller }.map { it.copy() }.calculateLast()

    val clientImports = imports.filter { it.client}.map { it.copy() }.calculateLast()

    // TODO do we assume that component serializers are a subset or must we add them?
    fun serializers() =
            paths.flatMap {
                it.operations.mapNotNull { op -> op.resultType }.map { resultType -> resultType.serializer } +
                it.operations.mapNotNull { op -> op.bodyType }.map { bodyType -> bodyType.serializer } +
                it.operations.flatMap { op -> op.parameters.map { par -> par.type.serializer } }
            }.toSet().toList().calculateLast()

}

data class IUVImport(val fullClassName: String, val type: IUVImportType, override var last: Boolean = false) : Comparable<IUVImport>, Last {

    override fun compareTo(other: IUVImport): Int = fullClassName.compareTo(other.fullClassName)

    val api = type == IUVImportType.API

    val controller = type == IUVImportType.CONTROLLER || type == IUVImportType.SHARED || api

    val client = type == IUVImportType.CLIENT || type == IUVImportType.SHARED

}

enum class IUVImportType {
    CONTROLLER,
    API,
    CLIENT,
    SHARED
}

fun <T : Last> List<T>.calculateLast(): List<T> {
    if (isNotEmpty()) {
        last().last = true
    }
    return this
}

class UnsupportedOpenAPISpecification: Exception {

    constructor(message: String) : super(message)

    constructor(message: String, exception: Throwable?) : super(message, exception)

}

object OpenAPIReader {
    private val LOGGER = LoggerFactory.getLogger(OpenAPIReader::class.java)

    fun read(url: URL): OpenAPI? = OpenAPIV3Parser().read(url.toURI().toString())

    fun runTemplate(url: URL, api : IUVAPI, context: OpenAPIWriteContext, writer: Writer) {
        val bundle = mapOf("context" to context, "api" to api)
        runTemplate(url, bundle, writer)
    }

    fun runTemplate(url: URL, bundle: Any, writer: Writer) {
        val mf = DefaultMustacheFactory(URLMustacheResolver(url))
        url.openStream().use { inputStream ->
            InputStreamReader(inputStream, "UTF-8").use { reader ->

                val mustache = mf.compile(reader, "template.mustache")
                mustache.execute(writer, bundle)
            }
        }
    }

    fun parse(url: URL, name: String, context: OpenAPIWriteContext) : IUVAPI? {
        val api = read(url) ?: return null

        val paths = api.paths.map { toIUVAPIPath(it, context) }

        val components = api.components.schemas.map { toIUVAPIComponent(api, it, context) }

        val operationsImports = paths.flatMap { it.operations.map { op -> IUVImport(op.op.fullClassName, IUVImportType.CONTROLLER) } }
        val parametersTypesImport = paths.flatMap {
            it.operations.flatMap {
                op -> op.parameters.map { par -> IUVImport(par.parameterType.fullClassName, IUVImportType.CONTROLLER) }
            }
        }
        val parametersImport = paths.flatMap {
            it.operations.flatMap { op -> op.parameters.flatMap { par -> par.type.imports } }
        }

        val resultAndBodyImports = paths.flatMap { it.operations.flatMap { op -> listOfNotNull(op.bodyType, op.resultType).flatMap { type -> type.imports } } }

        val resultAndBodySerializersImport = paths.flatMap { it.operations.flatMap { op -> listOfNotNull(op.bodyType, op.resultType).mapNotNull { type -> type.serializer.import}.map { typeSerializerImport -> IUVImport(typeSerializerImport, IUVImportType.CLIENT) } } }

        val imports = (operationsImports + parametersTypesImport + parametersImport +
                resultAndBodyImports + resultAndBodySerializersImport)
                .toSet()
                .toList()
                .sorted()

        return IUVAPI(name, paths, components, imports, api.servers?.firstOrNull()?.url ?: "")
    }

    private fun toIUVAPIPath(pathEntry: Map.Entry<String, PathItem>, context: OpenAPIWriteContext): IUVAPIPath {
        val pathItem = pathEntry.value

        val iuvAPIOperations = mutableListOf<IUVAPIOperation>()

        val path = pathEntry.key
        try {
            addOperation(iuvAPIOperations, path, IUVAPIOperationType.Get, pathItem.get, context)
            addOperation(iuvAPIOperations, path, IUVAPIOperationType.Post, pathItem.post, context)
            addOperation(iuvAPIOperations, path, IUVAPIOperationType.Put, pathItem.put, context)
            addOperation(iuvAPIOperations, path, IUVAPIOperationType.Delete, pathItem.delete, context, setOf("200", "204"))

            if (iuvAPIOperations.isEmpty()) {
                LOGGER.warn("Path $path : no supported operations.")
            }

            return IUVAPIPath(path, iuvAPIOperations)
        } catch (e : UnsupportedOpenAPISpecification) {
            throw UnsupportedOpenAPISpecification("Error resolving operations for path $path : ${e.message}", e)
        }
    }

    private fun addOperation(iuvAPIOperations: MutableList<IUVAPIOperation>, path: String,
                             operationType: IUVAPIOperationType, op: Operation?,
                             context: OpenAPIWriteContext, responses: Set<String> = setOf("200")) {
        if (op != null) {
            try {
                val iuvAPIOperation = toIUVAPIOperation(path, operationType, op, responses = responses, context = context)
                iuvAPIOperations.add(iuvAPIOperation)
            } catch (e: UnsupportedOpenAPISpecification) {
                LOGGER.warn("Cannot add operation fot path '$path'", e)
            }
        }
    }

    private fun toIUVAPIOperation(path: String, type: IUVAPIOperationType, op: Operation, responses: Set<String> = setOf("200"), context: OpenAPIWriteContext): IUVAPIOperation {
        var schemaForProperties : Schema<*>? = null

        val bodyType = if (op.requestBody != null &&
                op.requestBody.content.isNotEmpty()) {
                    if (op.requestBody.content.containsKey(JSON) || op.requestBody.content.containsKey(ALL_CONTENTS)) {
                        val resolveType = op.requestBody?.content?.get(JSON)?.schema?.resolveType(context)
                        resolveType ?: op.requestBody?.content?.get(ALL_CONTENTS)?.schema?.resolveType(context)
                    } else if (op.requestBody.content.containsKey(FORM_URL_ENCODED)) {
                        schemaForProperties = op.requestBody?.content?.get(FORM_URL_ENCODED)?.schema
                        null
                    } else {
                        throw UnsupportedOpenAPISpecification(
                                "Unsupported body content for '$type' operation: ${op.requestBody.content.keys} not supported.")
                    }
                } else null

        val response = op.responses.filterKeys { responses.contains(it) || it == "default" }.values.firstOrNull()

        val resultType =
            if (response == null) {
                if (type == IUVAPIOperationType.Get) {
                    throw UnsupportedOpenAPISpecification("No definition for '$type' operation for responses ${responses.joinToString()}.")
                } else if (type == IUVAPIOperationType.Delete || schemaForProperties != null) {
                    IUVAPIType("Unit", UNIT_SERIALIZER, listOf())
                } else {
                    bodyType
                }
            } else {
                val responseContent = response.content

                if (responseContent == null || responseContent.isEmpty())
                    IUVAPIType("Unit", UNIT_SERIALIZER, listOf())
                else
                    responseContent[JSON]?.schema?.resolveType(context)
            }

        if (resultType == null)
            throw UnsupportedOpenAPISpecification("Unsupported response content of '$type' operation : only nothing or $JSON are supported.")

        val parameters = getIUVAPIParameters(op, bodyType, context) +
                if (schemaForProperties == null) emptyList() else getIUVAPIParametersFromSchemaProperties(schemaForProperties, context)

        return IUVAPIOperation(path, type, op.operationId, parameters, resultType, bodyType)
    }

    private fun Schema<*>.resolveType(context: OpenAPIWriteContext) : IUVAPIType {
        if (type != null) {

            if (this is ArraySchema) {
                val itemsType = items.resolveType(context)
                return IUVAPIType("List<$itemsType>",
                        IUVAPISerializer("List${itemsType.serializer.name}", "ArrayListSerializer(${itemsType.serializer.code})",
                                `import` = "kotlinx.serialization.internal.ArrayListSerializer"),
                        itemsType.imports)
            }

            return toKotlinType(type, format)
        }

        if (`$ref` == null) {
            return IUVAPIType("Unit", UNIT_SERIALIZER, listOf())
        }

        val type = `$ref`.split("/").last()
        return IUVAPIType(type, IUVAPISerializer("${type}IUVSerializer", "$type::class.serializer()", `import` = "kotlinx.serialization.serializer"),
                listOf(IUVImport(context.modelPackage + "." + type, IUVImportType.SHARED)))
    }

    private fun toKotlinType(type: String, format: String?) =
        when (type) {
            "string" -> IUVAPIType("String", IUVAPISerializer("StringIUVSerializer", "StringSerializer", `import` = "kotlinx.serialization.internal.StringSerializer"), listOf())
            "integer" ->
                if (format == "int64") {
                    IUVAPIType("Long", IUVAPISerializer("LongIUVSerializer", "LongSerializer", `import` = "kotlinx.serialization.internal.LongSerializer"), listOf())
                } else {
                    IUVAPIType("Int", IUVAPISerializer("IntIUVSerializer", "IntSerializer", `import` = "kotlinx.serialization.internal.IntSerializer"), listOf())
                }
            "boolean" -> IUVAPIType("Boolean", IUVAPISerializer("BooleanIUVSerializer", "BooleanSerializer", `import` = "kotlinx.serialization.internal.BooleanSerializer"), listOf())
            else -> throw UnsupportedOpenAPISpecification("Unknown type '$type'.")
        }

    private fun getIUVAPIParameters(op: Operation, bodyType: IUVAPIType?, context: OpenAPIWriteContext) : List<IUVAPIParameter> {
        val parameters = op.parameters?.map {
            if (it.`in` == "query" || it is QueryParameter) {
                try {
                    if ((it.style != null && it.style != Parameter.StyleEnum.FORM) || (it.explode != null && !it.explode)) {
                        throw UnsupportedOpenAPISpecification("Parameter ${it.name}: unsupported parameter style, only form and explode true is supported.")
                    }
                    IUVAPIParameter(it.name, it.schema.resolveType(context), ParameterType.REQUEST_PARAM)
                } catch (e: NullPointerException) {
                    throw e
                }
            } else {
                IUVAPIParameter(it.name, it.schema.resolveType(context), ParameterType.PATH_VARIABLE)
            }
        }.orEmpty()

        if (bodyType != null) {
            return parameters + IUVAPIParameter("payload", bodyType, ParameterType.REQUEST_BODY)
        }

        return parameters
    }

    private fun getIUVAPIParametersFromSchemaProperties(schema: Schema<*>, context: OpenAPIWriteContext) : List<IUVAPIParameter> {
        return schema.properties.map { IUVAPIParameter(it.key, it.value.resolveType(context), ParameterType.FORM_PARAM) }
    }

    private fun toIUVAPIComponent(api: OpenAPI, schema: Map.Entry<String, Schema<*>>, context: OpenAPIWriteContext) : IUVAPIComponent {
        val properties = getProperties(api, schema.value)
                .map { IUVAPIComponentProperty(it.key, it.value.resolveType(context), !(schema.value.required?.contains(it.key) ?: false)) }

        return IUVAPIComponent(schema.key, properties)
    }

    private fun getProperties(api: OpenAPI, schema: Schema<*>) : Map<String, Schema<*>> =
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
            schema.properties ?: mapOf()
        }

}

class URLMustacheResolver(private val url: URL) : MustacheResolver {

    override fun getReader(resourceName: String?): Reader {
        val lastSlash = url.toString().lastIndexOf("/")
        val resourceURL = URL(url.toString().substring(0, lastSlash) + "/" + resourceName)
        return InputStreamReader(resourceURL.openStream())
    }

}
