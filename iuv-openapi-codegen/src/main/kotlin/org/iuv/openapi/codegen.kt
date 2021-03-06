package org.iuv.openapi

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheResolver
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
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
private val UNIT_SERIALIZER = IUVAPISerializer("Unit.serializer()",
        imports = setOf("kotlinx.serialization.builtins.serializer"))
private val unitType = PrimitiveParserType("Unit", UNIT_SERIALIZER, emptySet())

interface Last {
    var last: Boolean
}

data class OpenAPIWriteContext(val controllerPackage: String, val clientPackage: String, val modelPackage: String,
                               val sortProperties: Boolean = false, val sortParameters: Boolean = false)

data class IUVAPIType(val type: String, val serializer: IUVAPISerializer, val imports: Set<IUVImport>, val innerComponent :
        IUVAPIComponent? = null) {

    override fun toString() = type

}

data class IUVAPISerializer(val code: String, override var last: Boolean = false, val imports: Set<String> = emptySet()) : Last

data class IUVAPIComponentProperty(val key: String, val name: String, val type: IUVAPIType, val required: Boolean,
                                   val description: String?, val default: String?, override var last: Boolean = false) : Last {

    @Suppress("unused")
    val typeAndDefault = toTypeAndDefault(type.type, required, default)

    // properties used by mustache templates
    @Suppress("private", "unused")
    val descriptions = description?.split("\n")?.map { it.trim() }

    @Suppress("unused")
    val hasDescription = description != null

}

fun toTypeAndDefault(type: String, required: Boolean, default: String? ) =
        type + if (!required) {
            if (default == null)
                "? = null"
            else
                "? = $default"
        } else {
            if (default == null)
                ""
            else
                " = $default"
        }

data class WithLast<T>(val value: T, override var last: Boolean = false) : Last

data class IUVAPIComponent(val name: String, val _properties: List<IUVAPIComponentProperty>, override var last: Boolean = false,
                           val aliasFor: IUVAPIType? = null, val key: String, val description: String?,
                           val _enumValues: List<String>?, val sortProperties: Boolean) : Last {

    // properties used by mustache templates
    @Suppress("unused")
    val properties: List<IUVAPIComponentProperty> by lazy {
      if (sortProperties)
          _properties.sortedBy { if (it.default == null && it.required) 1 else 2 }.calculateLast()
        else
          _properties.calculateLast()
    }

    @Suppress("unused")
    val enumValues : List<WithLast<String>>? by lazy {
        _enumValues?.map { WithLast(it) }?.calculateLast()
    }

    @Suppress("unused")
    val isEnum = _enumValues != null

    @Suppress("private", "unused")
    val descriptions = description?.split("\n")?.map { it.trim() }

    @Suppress("unused")
    val hasDescription = description != null

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

data class IUVAPIParameter(val name: String, val description: String?, val type: IUVAPIType, val parameterType: ParameterType,
                           val required: Boolean, val default: String?, override var last: Boolean = false) : Last {
    // properties used by mustache templates

    @Suppress("unused")
    val typeAndDefault = toTypeAndDefault(type.type, required, default)

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

    @Suppress("unused")
    val hasDescription = description != null

    @Suppress("private")
    val descriptions = description?.split("\n")?.map { it.trim() }

    @Suppress("unused")
    val firstDescriptionLine = descriptions?.first()

    @Suppress("unused")
    val otherDescriptionLines =
        if (descriptions == null || descriptions.size == 1)
            null
        else
            descriptions.drop(1)
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

data class IUVAPIOperation(val serverPath: String, val path: String, val description: String?, val op: IUVAPIOperationType, val id: String,
                           val _parameters: List<IUVAPIParameter>, val resultType: IUVAPIType, val bodyType: IUVAPIType?,
                           val nullableResult: Boolean, val sortParameters: Boolean,
                           override var last: Boolean = false) : Last {

    fun name() : String {
        return id.split(" ").mapIndexed { i,s -> if (i != 0) s.capitalize() else s }.joinToString("")
    }

    // properties used by mustache templates

    val parameters: List<IUVAPIParameter> by lazy {
        if (sortParameters) {
            _parameters.sortedBy {
                val forDefault = if (it.default == null && it.required) 10 else 20
                val forType = when (it.parameterType) {
                    ParameterType.PATH_VARIABLE -> 1
                    ParameterType.REQUEST_PARAM -> 2
                    ParameterType.MULTI_PART_PARAM -> 3
                    ParameterType.MULTI_PART_FILE_PARAM -> 3
                    ParameterType.HEADER -> 4
                    else -> 5
                }
                forDefault + forType
            }.calculateLast()
        } else {
            _parameters.calculateLast()
        }
    }

    @Suppress("unused")
    val operationAnnotation : String
        get() {
            val path = this.path.removePrefix("/")
            val fullPath = "$serverPath/$path".removePrefix("/")
            return op.annotation(fullPath)
        }

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

    @Suppress("unused")
    val hasDescription = description != null || parameters.any { it.description != null }

    @Suppress("unused")
    val descriptions = description?.split("\n")?.map { it.trim() }

}

data class IUVAPIPath(val path: String, val operations: List<IUVAPIOperation>) {
    init {
        operations.calculateLast()
    }

    /**
     * used in the clientImpl template to transform a path like list/{id} in a path like list/$id
     * so it can be directly interpreted by kotlin via substitution since the id is a parameter of the client method
     * fun list(id: String)
     */
    val pathSubst =
        Regex("(\\{.*?})").replace(path) {
            val group = it.groups[1]
            "\$" + group?.value?.drop(1)?.dropLast(1)
        }

}

data class IUVAPIServer(val name: String, val apis : List<IUVAPI>, val components: List<IUVAPIComponent>, val baseUrl: String) {

    init {
        components.calculateLast()
    }

}

data class IUVAPI(val name: String, val paths: List<IUVAPIPath>, val imports: List<IUVImport>, val baseUrl: String) {

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

class OpenAPIReader(private val name : String, private val api: OpenAPI, private val context: OpenAPIWriteContext) {

    private val components = OpenAPIParser(api).components()
            .map { it.key to it }
            .toMap()

    companion object {
        private val LOGGER = LoggerFactory.getLogger(OpenAPIReader::class.java)

        private fun read(url: URL): OpenAPI? = OpenAPIV3Parser().read(url.toURI().toString())

        fun runTemplate(url: URL, api : IUVAPI, context: OpenAPIWriteContext, writer: Writer) {
            val bundle = mapOf("context" to context, "api" to api)
            runTemplate(url, bundle, writer)
        }

        fun runTemplate(url: URL, server : IUVAPIServer, context: OpenAPIWriteContext, writer: Writer) {
            val bundle = mapOf("context" to context, "server" to server)
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

        fun parse(url: URL, name: String, context: OpenAPIWriteContext, splitApisByPath : Boolean = true) : IUVAPIServer? {
            val api = read(url) ?: return null

            return OpenAPIReader(name, api, context).read(splitApisByPath)
        }

    }

    private fun read(splitApisByPath : Boolean) : IUVAPIServer {
        val standardImports = setOf(
                IUVImport("kotlinx.serialization.InternalSerializationApi", setOf(IUVImportType.CLIENT_IMPL)),
                IUVImport("org.iuv.shared.Task", setOf(IUVImportType.CLIENT, IUVImportType.CLIENT_IMPL)),
                IUVImport("org.iuv.core.Http", setOf(IUVImportType.CLIENT_IMPL)),
                IUVImport("org.iuv.core.HttpError", setOf(IUVImportType.CLIENT_IMPL, IUVImportType.CLIENT)),
                IUVImport("org.iuv.core.HttpRequestRunner", setOf(IUVImportType.CLIENT_IMPL)),
                IUVImport("org.iuv.core.HttpResult", setOf(IUVImportType.CLIENT_IMPL, IUVImportType.CLIENT)),
                IUVImport("org.iuv.core.HttpMethod", setOf(IUVImportType.CLIENT_IMPL)),
                IUVImport("org.w3c.dom.get", setOf(IUVImportType.CLIENT_IMPL)),
                IUVImport("kotlinx.browser.document", setOf(IUVImportType.CLIENT_IMPL)),
                IUVImport("org.iuv.core.Authentication", setOf(IUVImportType.CLIENT, IUVImportType.CLIENT_IMPL)),
                IUVImport("org.springframework.http.ResponseEntity", setOf(IUVImportType.CONTROLLER))
        )

        val baseUrl = (api.servers?.firstOrNull()?.url ?: "").removeSuffix("/")

        val indexOfDoubleSlash = baseUrl.indexOf("//")

        val serverPath = if (indexOfDoubleSlash > 0) {
            val indexOf = baseUrl.indexOf('/', indexOfDoubleSlash + 2)
            if (indexOf < 0) {
                ""
            } else {
                baseUrl.substring(indexOf + 1)
            }
        } else {
            baseUrl
        }

        val allPaths = api.paths.map { toIUVAPIPath(serverPath, it) }

        val pathsByName =
                if (splitApisByPath)
                    allPaths.groupBy({ it.path.split("/")[1] }) { it }
                else
                    mapOf(name to allPaths)

        val apis = pathsByName.map {
            val apiName = it.key.capitalize()

            val paths = it.value

            val operationsImports = paths.flatMap { path ->
                path.operations.map { op -> IUVImport(op.op.controllerAnnotationClass, setOf(IUVImportType.CONTROLLER)) }
            }

            val parametersTypesImport = paths.flatMap { path ->
                path.operations.flatMap { op ->
                    op.parameters.map { par -> IUVImport(par.parameterType.controllerAnnotationClass, setOf(IUVImportType.CONTROLLER)) }
                }
            }

            val parametersImport = paths.flatMap { path ->
                path.operations.flatMap { op -> op.parameters.flatMap { par -> par.type.imports } }
            }

            val resultAndBodyImports = paths.flatMap { path ->
                path.operations.flatMap { op -> listOfNotNull(op.bodyType, op.resultType).flatMap { type -> type.imports } }
            }

            val resultAndBodySerializersImport = paths.flatMap { path ->
                path.operations.flatMap { op ->
                    listOfNotNull(op.bodyType, op.resultType)
                            .flatMap { type ->
                                type.serializer.imports.map { imp -> IUVImport(imp, setOf(IUVImportType.CLIENT_IMPL)) } + type.imports
                            }
                }
            }

            val imports = (standardImports + operationsImports + parametersTypesImport + parametersImport +
                    resultAndBodyImports + resultAndBodySerializersImport)
                .toSet()
                .toList()
                .sortedBy { imp ->
                    val prefix =
                        if (imp.fullClassName.startsWith(context.modelPackage)) {
                            "2"
                        } else if (imp.fullClassName.startsWith(context.controllerPackage) || imp.fullClassName.startsWith(context.clientPackage)) {
                            "3"
                        } else if (imp.fullClassName.startsWith("org.iuv")) {
                            "1"
                        } else {
                            "0"
                        }
                    prefix + imp.fullClassName
                }

            IUVAPI(apiName, paths, imports, baseUrl)
        }

        // to remove duplicated names
        val notDuplicatedComponents = components
            .map { Pair(it.value.name, it) }
            .toMap()
            .map { it.value.value }

        return IUVAPIServer(name, apis, notDuplicatedComponents
            .filter { it !is AliasParserComponent }
            .map { it.toIUVAPIComponent() },
            baseUrl)
    }

    private fun ParserComponent.toIUVAPIComponent() : IUVAPIComponent =
        when (this) {
            is AliasParserComponent -> {
                val referenceComponent =
                        if (this.alias is RefParserType) {
                            components[alias.key]?.toIUVAPIComponent()
                        } else null

                if (referenceComponent == null) {
                    val aliasFor = alias.toIUVAPIType()
                    IUVAPIComponent(name, emptyList(), aliasFor = aliasFor, key = key, description = description,
                        _enumValues = null, sortProperties = context.sortProperties)
                } else referenceComponent

            }

            is ConcreteParserComponent ->
                IUVAPIComponent(name, properties.map { it.toIUVAPIProperty() }, key = key, description = description,
                    _enumValues = null, sortProperties = context.sortProperties)

            is EnumParserComponent ->
                IUVAPIComponent(name, emptyList(), key = key, _enumValues = this.values, description = description,
                    sortProperties = context.sortProperties)
        }

    private fun ParserProperty.toIUVAPIProperty() =
            IUVAPIComponentProperty(key, name, type.toIUVAPIType(), required, description, default)

    private fun toIUVAPIPath(serverPath: String, pathEntry: Map.Entry<String, PathItem>): IUVAPIPath {
        val pathItem = pathEntry.value

        val iuvAPIOperations = mutableListOf<IUVAPIOperation>()

        val path = pathEntry.key

        addOperation(serverPath, iuvAPIOperations, path, IUVAPIOperationType.Get, pathItem.get)
        addOperation(serverPath, iuvAPIOperations, path, IUVAPIOperationType.Post, pathItem.post)
        addOperation(serverPath, iuvAPIOperations, path, IUVAPIOperationType.Put, pathItem.put)
        addOperation(serverPath, iuvAPIOperations, path, IUVAPIOperationType.Delete, pathItem.delete)

        return IUVAPIPath(pathEntry.key, iuvAPIOperations)
    }

    private fun addOperation(serverPath: String, iuvAPIOperations: MutableList<IUVAPIOperation>, path: String,
                             operationType: IUVAPIOperationType, op: Operation?) {
        if (op != null) {
            try {
                val iuvAPIOperation = toIUVAPIOperation(serverPath, path, operationType, op)
                iuvAPIOperations.add(iuvAPIOperation)
            } catch (e: UnsupportedOpenAPISpecification) {
                LOGGER.warn("Cannot add operation fot path '$path'", e)
            }
        }
    }

    private fun toIUVAPIOperation(serverPath: String, path: String, type: IUVAPIOperationType, op: Operation): IUVAPIOperation {
        var schemaForProperties : Schema<*>? = null

        var multiPartForm = false

        val bodyType = if (op.requestBody != null &&
                op.requestBody.content.isNotEmpty()) {
                    if (op.requestBody.content.containsKey(JSON) || op.requestBody.content.containsKey(ALL_CONTENTS)) {
                        val resolveType = op.requestBody?.content?.get(JSON)?.schema?.resolveType("", "") // TODO parents
                        resolveType ?: op.requestBody?.content?.get(ALL_CONTENTS)?.schema?.resolveType("", "") // TODO parents
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

        val responseKeysFilter: (String) -> Boolean = { it.startsWith("2") || it == "default" }

        val responses = op.responses.filterKeys(responseKeysFilter)
                .toSortedMap().values

        if (responses.size > 1) {
            val validKeys = op.responses.keys.filter(responseKeysFilter).sorted()
            LOGGER.warn("$path '$type' operation: found more than one valid response (${validKeys.joinToString()}), first is taken.")
        }

        val response = responses.firstOrNull()

        val resultType =
            if (response == null) {
                bodyType ?: unitType
            } else {
                val responseContent = response.content

                if (responseContent == null || responseContent.isEmpty())
                    unitType
                else
                    responseContent[JSON]?.schema?.resolveType("", "") // parents
                        ?: throw UnsupportedOpenAPISpecification("Unsupported response content of '$type' operation : " +
                            "only unit (void) response or JSON are supported.")
            }

        val nullableResponse = resultType != unitType && op.responses.keys.contains("204")

        val parameters = getIUVAPIParameters(op, bodyType?.toIUVAPIType()) +
                if (schemaForProperties == null) emptyList() else getIUVAPIParametersFromSchemaProperties(schemaForProperties,
                        multiPartForm)

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

        return IUVAPIOperation(serverPath, path, op.description, type, operationId, parameters, resultType.toIUVAPIType(),
                bodyType?.toIUVAPIType(), nullableResponse, sortParameters = context.sortParameters)
    }

    private fun getIUVAPIParameters(op: Operation, bodyType: IUVAPIType?) : List<IUVAPIParameter> {
        val parameters = op.parameters?.map {
            val type = it.schema.resolveType("", "").toIUVAPIType() // TODO parents
            if (it.`in` == "query" || it is QueryParameter) {
                try {
                    if ((it.style != null && it.style != Parameter.StyleEnum.FORM) || (it.explode != null && !it.explode)) {
                        throw UnsupportedOpenAPISpecification("Parameter ${it.name}: unsupported parameter style, only form and explode true is supported.")
                    }
                    IUVAPIParameter(it.name, it.description, type, ParameterType.REQUEST_PARAM, it.required ?: false, getDefault(it.schema.default))
                } catch (e: NullPointerException) {
                    throw e
                }
            } else if (it.`in` == "header") {
                IUVAPIParameter(it.name, it.description, type, ParameterType.HEADER, it.required ?: false, getDefault(it.schema.default))
            } else {
                // path parameters are always required
                IUVAPIParameter(it.name, it.description, type, ParameterType.PATH_VARIABLE, true, null)
            }
        }.orEmpty()

        if (bodyType != null) {
            return parameters + IUVAPIParameter("body", null, bodyType, ParameterType.REQUEST_BODY, true, null)
        }

        return parameters
    }

    private fun getIUVAPIParametersFromSchemaProperties(schema: Schema<*>, multiPartForm: Boolean) : List<IUVAPIParameter> {
        return schema.properties.map {
            val type = it.value.resolveType("", "").toIUVAPIType() // TODO parent
            IUVAPIParameter(it.key, it.value.description, type,
                if (multiPartForm)
                    if (type.type == "MultipartFile")
                        ParameterType.MULTI_PART_FILE_PARAM
                    else ParameterType.MULTI_PART_PARAM
                else ParameterType.FORM_PARAM,
                (schema.required?.contains(it.key) ?: false), getDefault(it.value.default)) }
    }

    private fun ParserType.toIUVAPIType(): IUVAPIType =
        when (this) {
            is PrimitiveParserType -> IUVAPIType(type, serializer, imports)
            MultipartFileParserType -> IUVAPIType("MultipartFile",
                    IUVAPISerializer("", imports = emptySet()),
                    setOf(
                            IUVImport("org.iuv.core.MultiPartData", setOf(IUVImportType.CLIENT_IMPL)),
                            IUVImport("org.iuv.core.MultipartFile", setOf(IUVImportType.CLIENT, IUVImportType.CLIENT_IMPL)),
                            IUVImport("org.springframework.web.multipart.MultipartFile",
                                    setOf(IUVImportType.CONTROLLER))
                    )
            )
            is MapParserType -> {
                val mapType = valueType.toIUVAPIType()
                IUVAPIType("Map<String, $mapType>",
                        IUVAPISerializer(
                                "MapSerializer(String.serializer(),${mapType.serializer.code})",
                                imports = setOf("kotlinx.serialization.builtins.MapSerializer",
                                        "kotlinx.serialization.builtins.serializer") + mapType.serializer.imports),
                        mapType.imports)
            }
            is RefParserType -> {
                val component = components[key] ?: throw UnsupportedOpenAPISpecification("Cannot fine component $key")
                if (component is AliasParserComponent)
                    component.alias.toIUVAPIType()
                else
                    toComponentType(component)
            }
            is CustomParserType -> {
                val component = component
                toComponentType(component)
            }
            is ArrayParserType -> {
                val itemsType = itemsType.toIUVAPIType()

                IUVAPIType("List<$itemsType>",
                        IUVAPISerializer("ListSerializer(${itemsType.serializer.code})",
                                imports = setOf("kotlinx.serialization.builtins.ListSerializer") + itemsType.serializer.imports),
                        itemsType.imports, itemsType.innerComponent)
            }
            is EnumParserType -> {
                IUVAPIType(name,
                        IUVAPISerializer("$name::class.serializer()",
                                imports = setOf("kotlinx.serialization.internal.EnumSerializer")),
                        emptySet(), null)
            }
        }

    private fun toComponentType(component: ParserComponent): IUVAPIType {
        return IUVAPIType(component.name, IUVAPISerializer( "${component.name}::class.serializer()",
                imports = setOf("kotlinx.serialization.serializer")),
                setOf(IUVImport(context.modelPackage + "." + component.name,
                        setOf(IUVImportType.CONTROLLER, IUVImportType.CLIENT, IUVImportType.CLIENT_IMPL))))
    }
}

class URLMustacheResolver(private val url: URL) : MustacheResolver {

    override fun getReader(resourceName: String?): Reader {
        val lastSlash = url.toString().lastIndexOf("/")
        val resourceURL = URL(url.toString().substring(0, lastSlash) + "/" + resourceName)
        return InputStreamReader(resourceURL.openStream())
    }

}
