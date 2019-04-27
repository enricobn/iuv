package org.iuv.openapi

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheResolver
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.FileSchema
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
private const val MULTIPART_FORM_DATA = "multipart/form-data"
private val UNIT_SERIALIZER = IUVAPISerializer("UnitIUVSerializer", "UnitSerializer",
        imports = setOf("kotlinx.serialization.internal.UnitSerializer"))

interface Last {
    var last: Boolean
}

data class OpenAPIWriteContext(val controllerPackage: String, val clientPackage: String, val modelPackage: String)

data class IUVAPIType(val type: String, val serializer: IUVAPISerializer, val imports: List<IUVImport>, val innerComponent :
        IUVAPIComponent? = null) {

    override fun toString() = type

}

data class IUVAPISerializer(val name: String, val code: String, override var last: Boolean = false, val imports: Set<String> = emptySet()) : Last

data class IUVAPIComponentProperty(val name: String, val type: IUVAPIType, val optional: Boolean, override var last: Boolean = false) : Last

data class IUVAPIComponent(val name: String, val properties: List<IUVAPIComponentProperty>, override var last: Boolean = false, val aliasFor: IUVAPIType? = null, val key : String) : Last {
    init {
        properties.calculateLast()
    }
}

enum class ParameterType(val controllerAnnotationClass: String) {
    PATH_VARIABLE("org.springframework.web.bind.annotation.PathVariable"),
    REQUEST_PARAM("org.springframework.web.bind.annotation.RequestParam"),
    REQUEST_BODY("org.springframework.web.bind.annotation.RequestBody"),
    FORM_PARAM("org.springframework.web.bind.annotation.RequestParam"),
    MULTI_PART_PARAM("org.springframework.web.bind.annotation.RequestParam"),
    MULTI_PART_FILE_PARAM("org.springframework.web.bind.annotation.RequestPart"),
    HEADER("org.springframework.web.bind.annotation.RequestHeader")
}

data class IUVAPIParameter(val name: String, val type: IUVAPIType, val parameterType: ParameterType, override var last: Boolean = false) : Last {
    // properties used by mustache templates
    @Suppress("unused")
    val pathVariable = parameterType == ParameterType.PATH_VARIABLE

    @Suppress("unused")
    val requestParam = parameterType == ParameterType.REQUEST_PARAM || parameterType == ParameterType.FORM_PARAM || parameterType == ParameterType.MULTI_PART_PARAM

    @Suppress("unused")
    val requestBody = parameterType == ParameterType.REQUEST_BODY

    @Suppress("unused")
    val formParam = parameterType == ParameterType.FORM_PARAM

    @Suppress("unused")
    val clientQueryParam = parameterType == ParameterType.REQUEST_PARAM

    @Suppress("unused")
    val multiPartParam = parameterType == ParameterType.MULTI_PART_PARAM || parameterType == ParameterType.MULTI_PART_FILE_PARAM

    @Suppress("unused")
    val requestHeader = parameterType == ParameterType.HEADER

    @Suppress("unused")
    val requestPart = parameterType == ParameterType.MULTI_PART_FILE_PARAM
}

/**
 * @param clientMethod is used by mustache templates
 */
enum class IUVAPIOperationType(val controllerAnnotationClass: String, @Suppress("unused") val clientMethod: String) {
    Get("org.springframework.web.bind.annotation.GetMapping", "HttpMethod.Get"),
    Post("org.springframework.web.bind.annotation.PostMapping", "HttpMethod.Post"),
    Put("org.springframework.web.bind.annotation.PutMapping", "HttpMethod.Put"),
    Delete("org.springframework.web.bind.annotation.DeleteMapping", "HttpMethod.Delete");

    fun annotation(path: String) =
        controllerAnnotationClass.split('.').last() + "(\"$path\")"
}

data class IUVAPIOperation(val path: String, val op: IUVAPIOperationType, val id: String, val parameters: List<IUVAPIParameter>,
                           val resultType: IUVAPIType, val bodyType: IUVAPIType?, override var last: Boolean = false) : Last {

    init {
        parameters.calculateLast()
    }

    fun name() : String {
        return id.split(" ").mapIndexed { i,s -> if (i != 0) s.capitalize() else s }.joinToString("")
    }

    // properties used by mustache templates

    @Suppress("unused")
    val operationAnnotation = op.annotation(path)

    @Suppress("unused")
    val hasFormData = parameters.any { it.formParam }

    @Suppress("unused")
    val formData = parameters.filter { it.formParam }.map { it.copy() }.calculateLast()

    @Suppress("unused")
    val hasClientQueryParams = parameters.any { it.clientQueryParam }

    @Suppress("unused")
    val clientQueryParams = parameters.filter{ it.clientQueryParam }.map { it.copy() }.calculateLast()

    @Suppress("unused")
    val hasMultiPartData = parameters.any { it.multiPartParam }

    @Suppress("unused")
    val multiPartData = parameters.filter { it.multiPartParam }.map { it.copy() }.calculateLast()

    @Suppress("unused")
    val hasHeaders = parameters.any { it.requestHeader }

    @Suppress("unused")
    val headers = parameters.filter { it.requestHeader }.map { it.copy() }.calculateLast()

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

    // properties used by mustache templates

    @Suppress("unused")
    val controllerImports = imports.filter { it.controller }.map { it.copy() }.calculateLast()

    @Suppress("unused")
    val clientImplImports = imports.filter { it.clientImpl}.map { it.copy() }.calculateLast()

    @Suppress("unused")
    val clientImports = imports.filter { it.client}.map { it.copy() }.calculateLast()

}

data class IUVImport(val fullClassName: String, val types: Set<IUVImportType>, override var last: Boolean = false) : Comparable<IUVImport>, Last {

    override fun compareTo(other: IUVImport): Int = fullClassName.compareTo(other.fullClassName)

    val controller = types.contains(IUVImportType.CONTROLLER)

    val client = types.contains(IUVImportType.CLIENT)

    val clientImpl = types.contains(IUVImportType.CLIENT_IMPL)

}

enum class IUVImportType {
    CONTROLLER,
    CLIENT,
    CLIENT_IMPL
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

    private fun read(url: URL): OpenAPI? = OpenAPIV3Parser().read(url.toURI().toString())

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
        val standardImports = setOf(
                IUVImport("org.iuv.shared.Task", setOf(IUVImportType.CLIENT, IUVImportType.CLIENT_IMPL)),
                IUVImport("kotlinx.serialization.ImplicitReflectionSerializer", setOf(IUVImportType.CLIENT_IMPL)),
                IUVImport("org.iuv.core.Http", setOf(IUVImportType.CLIENT_IMPL)),
                IUVImport("org.iuv.core.HttpMethod", setOf(IUVImportType.CLIENT_IMPL))
        )

        val api = read(url) ?: return null

        val openAPIParser = OpenAPIParser(api, context)

        val components = openAPIParser.components()//api.components.schemas.flatMap { toIUVAPIComponents(api, it, context).toList() }.toMap()

        val paths = api.paths.map { toIUVAPIPath(it, context, components) }

        val operationsImports = paths.flatMap {
            it.operations.map { op -> IUVImport(op.op.controllerAnnotationClass, setOf(IUVImportType.CONTROLLER)) }
        }

        val parametersTypesImport = paths.flatMap {
            it.operations.flatMap {
                op -> op.parameters.map { par -> IUVImport(par.parameterType.controllerAnnotationClass, setOf(IUVImportType.CONTROLLER)) }
            }
        }
        val parametersImport = paths.flatMap {
            it.operations.flatMap { op -> op.parameters.flatMap { par -> par.type.imports } }
        }

        val resultAndBodyImports = paths.flatMap { it.operations.flatMap { op -> listOfNotNull(op.bodyType, op.resultType).flatMap { type -> type.imports } } }

        val resultAndBodySerializersImport = paths.flatMap {
            it.operations.flatMap { op ->
                listOfNotNull(op.bodyType, op.resultType)
                        .flatMap { type -> type.serializer.imports }
                        .map { typeSerializerImport -> IUVImport(typeSerializerImport, setOf(IUVImportType.CLIENT_IMPL)) }
            }
        }

        val imports = (standardImports + operationsImports + parametersTypesImport + parametersImport +
                resultAndBodyImports + resultAndBodySerializersImport)
                .toSet()
                .toList()
                .sortedBy {
                    val prefix =
                            if (it.fullClassName.startsWith(context.modelPackage)) {
                                "2"
                            } else if (it.fullClassName.startsWith(context.controllerPackage) || it.fullClassName.startsWith(context.clientPackage)) {
                                "3"
                            } else if (it.fullClassName.startsWith("org.iuv")) {
                                "1"
                            } else {
                                "0"
                            }
                    prefix + it.fullClassName
                }

        return IUVAPI(name, paths, components.values.toList(), imports, api.servers?.firstOrNull()?.url ?: "")
    }

    private fun toIUVAPIPath(pathEntry: Map.Entry<String, PathItem>, context: OpenAPIWriteContext, components: Map<String, IUVAPIComponent>): IUVAPIPath {
        val pathItem = pathEntry.value

        val iuvAPIOperations = mutableListOf<IUVAPIOperation>()

        val path = pathEntry.key
        try {
            addOperation(iuvAPIOperations, path, IUVAPIOperationType.Get, pathItem.get, context, components)
            addOperation(iuvAPIOperations, path, IUVAPIOperationType.Post, pathItem.post, context, components)
            addOperation(iuvAPIOperations, path, IUVAPIOperationType.Put, pathItem.put, context, components)
            addOperation(iuvAPIOperations, path, IUVAPIOperationType.Delete, pathItem.delete, context, components)

            return IUVAPIPath(path, iuvAPIOperations)
        } catch (e : UnsupportedOpenAPISpecification) {
            throw UnsupportedOpenAPISpecification("Error resolving operations for path $path : ${e.message}", e)
        }
    }

    private fun addOperation(iuvAPIOperations: MutableList<IUVAPIOperation>, path: String,
                             operationType: IUVAPIOperationType, op: Operation?,
                             context: OpenAPIWriteContext, components: Map<String, IUVAPIComponent>) {
        if (op != null) {
            try {
                val iuvAPIOperation = toIUVAPIOperation(path, operationType, op, context, components)
                iuvAPIOperations.add(iuvAPIOperation)
            } catch (e: UnsupportedOpenAPISpecification) {
                LOGGER.warn("Cannot add operation fot path '$path'", e)
            }
        }
    }

    private fun toIUVAPIOperation(path: String, type: IUVAPIOperationType, op: Operation,
                                  context: OpenAPIWriteContext, components: Map<String, IUVAPIComponent>,
                                  responses: Set<String> = setOf("200", "204", "302")): IUVAPIOperation {
        var schemaForProperties : Schema<*>? = null

        var multiPartForm = false

        val bodyType = if (op.requestBody != null &&
                op.requestBody.content.isNotEmpty()) {
                    if (op.requestBody.content.containsKey(JSON) || op.requestBody.content.containsKey(ALL_CONTENTS)) {
                        val resolveType = op.requestBody?.content?.get(JSON)?.schema?.resolveType(context, components)
                        resolveType ?: op.requestBody?.content?.get(ALL_CONTENTS)?.schema?.resolveType(context, components)
                    } else if (op.requestBody.content.containsKey(FORM_URL_ENCODED)) {
                        schemaForProperties = op.requestBody?.content?.get(FORM_URL_ENCODED)?.schema
                        null
                    } else if (op.requestBody.content.containsKey(MULTIPART_FORM_DATA)) {
                        schemaForProperties = op.requestBody?.content?.get(MULTIPART_FORM_DATA)?.schema
                        multiPartForm = true
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
                    } else bodyType
                            ?: throw UnsupportedOpenAPISpecification("Unsupported response content of '$type' operation : " +
                                    "no response and no body specified.")
                } else {
                    val responseContent = response.content

                    if (responseContent == null || responseContent.isEmpty())
                        IUVAPIType("Unit", UNIT_SERIALIZER, listOf())
                    else
                        responseContent[JSON]?.schema?.resolveType(context, components)
                                ?: throw UnsupportedOpenAPISpecification("Unsupported response content of '$type' operation : " +
                                        "only unit (void) response or JSON are supported.")
                }

        val parameters = getIUVAPIParameters(op, bodyType, context, components) +
                if (schemaForProperties == null) emptyList() else getIUVAPIParametersFromSchemaProperties(schemaForProperties, context,
                        multiPartForm, components)

        val operationId =
            if (op.operationId == null) {
                val name = path.split("/", ".")
                type.name.toLowerCase() + name.map {
                    if (it.startsWith("{")) "By" + it.removeSurrounding("{", "}").capitalize() else it
                }.joinToString("") { it.capitalize() }
            } else {
                op.operationId
            }

        val duplicatedParameters =
                parameters.map { it.name }
                        .groupBy { it }
                        .entries
                        .filter { it.value.size > 1 }

        if (duplicatedParameters.isNotEmpty()) {
            throw UnsupportedOpenAPISpecification("Duplicated parameter names : " + duplicatedParameters.map { it.key })
        }

        return IUVAPIOperation(path, type, operationId, parameters, resultType, bodyType)
    }

    fun Schema<*>.resolveType(context: OpenAPIWriteContext, components: Map<String,IUVAPIComponent>) : IUVAPIType {
        if (type != null) {

            when {
                this is ArraySchema -> {
                    val itemsType = items.resolveType(context, components)
                    return IUVAPIType("List<$itemsType>",
                            IUVAPISerializer("List${itemsType.serializer.name}", "ArrayListSerializer(${itemsType.serializer.code})",
                                    imports = setOf("kotlinx.serialization.internal.ArrayListSerializer") + itemsType.serializer.imports),
                            itemsType.imports)
                }
                this is FileSchema -> return IUVAPIType("MultipartFile",
                        IUVAPISerializer("", "",imports = emptySet()),
                        listOf(
                                IUVImport("org.iuv.core.MultiPartData", setOf(IUVImportType.CLIENT_IMPL)),
                                IUVImport("org.iuv.core.MultipartFile", setOf(IUVImportType.CLIENT, IUVImportType.CLIENT_IMPL)),
                                IUVImport("org.springframework.web.multipart.MultipartFile",
                                        setOf(IUVImportType.CONTROLLER))
                        )
                )
                type == "object" -> additionalProperties.let {
                    if (it is Boolean)  {
                        throw UnsupportedOpenAPISpecification("Unknown type.")
                    } else if (it is Schema<*>) {
                        val mapType = it.resolveType(context, components)
                        return IUVAPIType("Map<String, $mapType>",
                                IUVAPISerializer("MapString${mapType.serializer.name}",
                                        "HashMapSerializer(StringSerializer,${mapType.serializer.code})",
                                        imports = setOf("kotlinx.serialization.internal.HashMapSerializer",
                                                "kotlinx.serialization.internal.StringSerializer") + mapType.serializer.imports),
                                mapType.imports)
                    } else {
                        // TODO
                        return IUVAPIType("", IUVAPISerializer("", ""), emptyList())
                    }
                }
            }

            return toKotlinType(type, format)
        }

        if (`$ref` == null) {
            return IUVAPIType("Unit", UNIT_SERIALIZER, listOf())
        }

        val iuvapiComponent = components[`$ref`.split("/").last()]

        if (iuvapiComponent == null) {
            throw UnsupportedOpenAPISpecification("Cannot find component $`$ref`")
        } else {
            val type = iuvapiComponent.name

            if (iuvapiComponent.aliasFor != null) {
                return IUVAPIType(type, iuvapiComponent.aliasFor.serializer,
                        listOf(IUVImport(context.modelPackage + "." + type,
                                setOf(IUVImportType.CONTROLLER, IUVImportType.CLIENT, IUVImportType.CLIENT_IMPL))))
            } else {
                return IUVAPIType(type, IUVAPISerializer("${type}IUVSerializer", "$type::class.serializer()",
                        imports = setOf("kotlinx.serialization.serializer")),
                        listOf(IUVImport(context.modelPackage + "." + type,
                                setOf(IUVImportType.CONTROLLER, IUVImportType.CLIENT, IUVImportType.CLIENT_IMPL))))
            }
        }

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

    private fun getIUVAPIParameters(op: Operation, bodyType: IUVAPIType?, context: OpenAPIWriteContext,
                                    components: Map<String, IUVAPIComponent>) : List<IUVAPIParameter> {
        val parameters = op.parameters?.map {
            if (it.`in` == "query" || it is QueryParameter) {
                try {
                    if ((it.style != null && it.style != Parameter.StyleEnum.FORM) || (it.explode != null && !it.explode)) {
                        throw UnsupportedOpenAPISpecification("Parameter ${it.name}: unsupported parameter style, only form and explode true is supported.")
                    }
                    IUVAPIParameter(it.name, it.schema.resolveType(context, components), ParameterType.REQUEST_PARAM)
                } catch (e: NullPointerException) {
                    throw e
                }
            } else if (it.`in` == "header") {
                IUVAPIParameter(it.name, it.schema.resolveType(context, components), ParameterType.HEADER)
            } else {
                IUVAPIParameter(it.name, it.schema.resolveType(context, components), ParameterType.PATH_VARIABLE)
            }
        }.orEmpty()

        if (bodyType != null) {
            return parameters + IUVAPIParameter("body", bodyType, ParameterType.REQUEST_BODY)
        }

        return parameters
    }

    private fun getIUVAPIParametersFromSchemaProperties(schema: Schema<*>, context: OpenAPIWriteContext, multiPartForm: Boolean,
                                                        components: Map<String, IUVAPIComponent>) : List<IUVAPIParameter> {
        return schema.properties.map {
            val type = it.value.resolveType(context, components)
            IUVAPIParameter(it.key, type,
                if (multiPartForm)
                    if (type.type == "MultipartFile")
                        ParameterType.MULTI_PART_FILE_PARAM
                    else ParameterType.MULTI_PART_PARAM
                else ParameterType.FORM_PARAM
        ) }
    }

}

class URLMustacheResolver(private val url: URL) : MustacheResolver {

    override fun getReader(resourceName: String?): Reader {
        val lastSlash = url.toString().lastIndexOf("/")
        val resourceURL = URL(url.toString().substring(0, lastSlash) + "/" + resourceName)
        return InputStreamReader(resourceURL.openStream())
    }

}
