package org.iuv.openapi

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheResolver
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.ComposedSchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.parser.OpenAPIV3Parser
import org.slf4j.LoggerFactory
import java.io.InputStreamReader
import java.io.Reader
import java.io.Writer
import java.net.URL

private const val JSON = "application/json"

interface Last {
    var last: Boolean
}

data class IUVAPIType(val type: String, val serializer: IUVAPISerializer) {

    override fun toString() = type

}

data class IUVAPISerializer(val name: String, val code: String, override var last: Boolean = false) : Last

data class IUVAPIComponentProperty(val name: String, val type: IUVAPIType, val optional: Boolean, override var last: Boolean = false) : Last

data class IUVAPIComponent(val name: String, val properties: List<IUVAPIComponentProperty>, override var last: Boolean = false) : Last {
    init {
        properties.calculateLast()
    }
}

data class IUVAPIParameter(val name: String, val type: IUVAPIType, val pathParameter: Boolean, override var last: Boolean = false) : Last

enum class IUVAPIOperationType(val fullClassName: String) {
    Get("org.springframework.web.bind.annotation.GetMapping"),
    Post("org.springframework.web.bind.annotation.PostMapping"),
    Put("org.springframework.web.bind.annotation.PutMapping"),
    Delete("org.springframework.web.bind.annotation.DeleteMapping");

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

}

data class IUVAPIPath(val path: String, val operations: List<IUVAPIOperation>) {
    init {
        operations.calculateLast()
    }
}

data class IUVAPI(val name: String, val paths: List<IUVAPIPath>, val components: List<IUVAPIComponent>,
                  val imports: List<IUVImport>) {

    init {
        components.calculateLast()
    }

    val apiImports = imports.filter { it.api }.map { it.copy() }.calculateLast()

    val controllerImports = imports.filter { it.controller }.map { it.copy() }.calculateLast()

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

    val controller = type == IUVImportType.CONTROLLER || api

}

enum class IUVImportType {
    CONTROLLER,
    API
}

fun <T : Last> List<T>.calculateLast(): List<T> {
    if (isNotEmpty()) {
        last().last = true
    }
    return this
}

fun <T> listOfLast(vararg elements: T): List<T> where T : Last {
    val list = elements.asList()
    if (list.isNotEmpty()) {
        list.last().last = true
    }
    return list
}

class UnsupportedOpenAPISpecification(message: String) : Exception(message)

object OpenAPIReader {
    private val LOGGER = LoggerFactory.getLogger(OpenAPIReader::class.java)

    fun read(url: URL): OpenAPI? = OpenAPIV3Parser().read(url.toURI().toString())

    fun runTemplate(url: URL, bundle: Any, writer: Writer) {
        val mf = DefaultMustacheFactory(URLMustacheResolver(url))
        url.openStream().use { inputStream ->
            InputStreamReader(inputStream, "UTF-8").use { reader ->

                val mustache = mf.compile(reader, "template.mustache")
                mustache.execute(writer, bundle)
            }
        }
    }

    fun parse(url: URL, name: String) : IUVAPI? {
        val api = read(url) ?: return null

        val paths = api.paths.map { toIUVAPIPath(it) }

        val components = api.components.schemas.map { toIUVAPIComponent(api, it) }

        val imports = paths.flatMap { it.operations.map { op -> IUVImport(op.op.fullClassName, IUVImportType.CONTROLLER) } }
                .toSet()
                .toList()
                .sorted()

        return IUVAPI(name, paths, components, imports)
    }

    private fun toIUVAPIPath(pathEntry: Map.Entry<String, PathItem>): IUVAPIPath {
        val pathItem = pathEntry.value

        val iuvAPIOperations = mutableListOf<IUVAPIOperation>()

        val path = pathEntry.key
        try {
            if (pathItem.get != null) {
                iuvAPIOperations.add(toIUVAPIOperation(path, IUVAPIOperationType.Get, pathItem.get))
            }

            if (pathItem.post != null) {
                iuvAPIOperations.add(toIUVAPIOperation(path, IUVAPIOperationType.Post, pathItem.post))
            }

            if (pathItem.put != null) {
                iuvAPIOperations.add(toIUVAPIOperation(path, IUVAPIOperationType.Put, pathItem.put))
            }

            if (pathItem.delete != null) {
                iuvAPIOperations.add(toIUVAPIOperation(path, IUVAPIOperationType.Delete, pathItem.delete, "204"))
            }

            if (iuvAPIOperations.isEmpty()) {
                throw UnsupportedOpenAPISpecification("Path $path : no supported operations.")
            }

            return IUVAPIPath(path, iuvAPIOperations)
        } catch (e : UnsupportedOpenAPISpecification) {
            throw UnsupportedOpenAPISpecification("Error resolving operations for path $path : ${e.message}")
        }
    }

    private fun toIUVAPIOperation(path: String, type: IUVAPIOperationType, op: Operation, response: String = "200"): IUVAPIOperation {
        if (op.requestBody != null &&
                op.requestBody.content.isNotEmpty() &&
                !op.requestBody.content.containsKey(JSON))
            throw UnsupportedOpenAPISpecification("Unsupported body content: only $JSON is supported.")

        val bodyType = op.requestBody?.content?.get(JSON)?.schema?.resolveType()
        val resultType : IUVAPIType?

        if (op.responses[response] == null)
            throw UnsupportedOpenAPISpecification("No definition for '$type' operation for response '$response'.")

        val responseContent = op.responses[response]?.content
        if (responseContent == null)
            resultType = IUVAPIType("Unit", IUVAPISerializer("UnitIUVSerializer", "UnitSerializer"))
        else if (responseContent.isNotEmpty() && !responseContent.containsKey(JSON))
            throw UnsupportedOpenAPISpecification("Unsupported response content: only nothing or $JSON is supported.")
        else
            resultType = responseContent[JSON]?.schema?.resolveType()

        if (resultType == null)
            throw UnsupportedOpenAPISpecification("Unknown result type of '$type' operation for response '$response'.")

        return IUVAPIOperation(path, type, op.operationId, getIUVAPIParameters(op), resultType, bodyType)
    }

    private fun Schema<*>.resolveType() : IUVAPIType {
        if (type != null) {

            if (this is ArraySchema) {
                val itemsType = items.resolveType()
                return IUVAPIType("List<$itemsType>",
                        IUVAPISerializer("List${itemsType.serializer.name}", "ArrayListSerializer(${itemsType.serializer.code})"))
            }

            return toKotlinType(type)
        }

        if (`$ref` == null) {
            return IUVAPIType("Unit", IUVAPISerializer("UnitIUVSerializer", "UnitSerializer"))
        }

        val type = `$ref`.split("/").last()
        return IUVAPIType(type, IUVAPISerializer("${type}IUVSerializer", "$type::class.serializer()"))
    }

    private fun toKotlinType(type: String): IUVAPIType {
        if (type == "string") {
            return IUVAPIType("String", IUVAPISerializer("StringIUVSerializer", "StringSerializer"))
        } else if (type == "integer") {
            return IUVAPIType("Int", IUVAPISerializer("IntIUVSerializer", "IntSerializer"))
        } else {
            throw UnsupportedOpenAPISpecification("Unknown type '$type'.")
        }
    }

    private fun getIUVAPIParameters(op: Operation) =
         op.parameters?.map {
            if (it.`in` == "query") {
                IUVAPIParameter(it.name, it.schema.resolveType(), false)
            } else {
                IUVAPIParameter(it.name, it.schema.resolveType(), true)
            }
        }.orEmpty()

    private fun toIUVAPIComponent(api: OpenAPI, schema: Map.Entry<String, Schema<*>>) : IUVAPIComponent {
        val properties = getProperties(api, schema.value).map { IUVAPIComponentProperty(it.key, it.value.resolveType(), false) } // TODO optional

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
