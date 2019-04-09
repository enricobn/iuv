package org.iuv.openapi

import com.github.mustachejava.DefaultMustacheFactory
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.ComposedSchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.parser.OpenAPIV3Parser
import org.slf4j.LoggerFactory
import java.io.InputStreamReader
import java.io.Writer
import java.net.URL

private const val JSON = "application/json"

interface Last {
    var last: Boolean
}

data class IUVAPIComponentProperty(val name: String, val type: String, val optional: Boolean, override var last: Boolean = false) : Last

data class IUVAPIComponent(val name: String, val properties: List<IUVAPIComponentProperty>) {
    init {
        properties.calculateLast()
    }
}

data class IUVAPIParameter(val name: String, val type: String, val pathParameter: Boolean, override var last: Boolean = false) : Last

enum class IUVAPIOperationType(val fullClassName: String) {
    Get("org.springframework.web.bind.annotation.GetMapping"),
    Post("org.springframework.web.bind.annotation.PostMapping"),
    Put("org.springframework.web.bind.annotation.PutMapping"),
    Delete("org.springframework.web.bind.annotation.DeleteMapping");

}

data class IUVAPIOperation(val op: IUVAPIOperationType, val id: String, val parameters: List<IUVAPIParameter>, val resultType: String,
                           val bodyType: String?, override var last: Boolean = false) : Last {

    init {
        parameters.calculateLast()
    }

    fun isGet() = op == IUVAPIOperationType.Get

    fun isPost() = op == IUVAPIOperationType.Post

    fun isPut() = op == IUVAPIOperationType.Put

    fun isDelete() = op == IUVAPIOperationType.Delete

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

data class IUVAPI(val name: String, val paths: List<IUVAPIPath>, val components: List<IUVAPIComponent>, val imports: List<String>)

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
        url.openStream().use { inputStream ->
            InputStreamReader(inputStream, "UTF-8").use { reader ->
                val mf = DefaultMustacheFactory()

                val mustache = mf.compile(reader, "template.mustache")
                mustache.execute(writer, bundle)
            }
        }
    }

    fun toIUVAPI(url: URL, name: String) : IUVAPI? {
        val api = read(url) ?: return null

        val paths = api.paths.map { toIUVAPIPath(it) }

        val components = api.components.schemas.map { toIUVAPIComponent(api, it) }

        val imports = paths.flatMap { it.operations.map { op -> op.op.fullClassName } }.toSet().toList().sorted()

        return IUVAPI(name, paths, components, imports)
    }

    private fun toIUVAPIPath(pathEntry: Map.Entry<String, PathItem>): IUVAPIPath {
        val pathItem = pathEntry.value

        val iuvAPIOperations = mutableListOf<IUVAPIOperation>()

        try {
            if (pathItem.get != null) {
                iuvAPIOperations.add(toIUVAPIOperation(IUVAPIOperationType.Get, pathItem.get))
            }

            if (pathItem.post != null) {
                iuvAPIOperations.add(toIUVAPIOperation(IUVAPIOperationType.Post, pathItem.post))
            }

            if (pathItem.put != null) {
                iuvAPIOperations.add(toIUVAPIOperation(IUVAPIOperationType.Put, pathItem.put))
            }

            if (pathItem.delete != null) {
                iuvAPIOperations.add(toIUVAPIOperation(IUVAPIOperationType.Delete, pathItem.delete, "204"))
            }

            if (iuvAPIOperations.isEmpty()) {
                throw UnsupportedOpenAPISpecification("Path ${pathEntry.key} : no supported operations.")
            }

            return IUVAPIPath(pathEntry.key, iuvAPIOperations)
        } catch (e : UnsupportedOpenAPISpecification) {
            throw UnsupportedOpenAPISpecification("Error resolving operations for path ${pathEntry.key} : ${e.message}")
        }
    }

    private fun toIUVAPIOperation(type: IUVAPIOperationType, op: Operation, response: String = "200"): IUVAPIOperation {
        if (op.requestBody != null &&
                op.requestBody.content.isNotEmpty() &&
                !op.requestBody.content.containsKey(JSON))
            throw UnsupportedOpenAPISpecification("Unsupported body content: only $JSON is supported.")

        val bodyType = op.requestBody?.content?.get(JSON)?.schema?.resolveType()
        val resultType : String?

        if (op.responses[response] == null)
            throw UnsupportedOpenAPISpecification("No definition for '$type' operation for response '$response'.")

        val responseContent = op.responses[response]?.content
        if (responseContent == null)
            resultType = "Unit"
        else if (responseContent.isNotEmpty() && !responseContent.containsKey(JSON))
            throw UnsupportedOpenAPISpecification("Unsupported response content: only nothing or $JSON is supported.")
        else
            resultType = responseContent[JSON]?.schema?.resolveType()

        if (resultType == null)
            throw UnsupportedOpenAPISpecification("Unknown result type of '$type' operation for response '$response'.")

        return IUVAPIOperation(type, op.operationId, getIUVAPIParameters(op), resultType, bodyType)
    }

    private fun Schema<*>.resolveType() : String {
        if (type != null) {

            if (this is ArraySchema) {
                return "List<${items.resolveType()}>"
            }

            return toKotlinType(type)
        }

        if (`$ref` == null) {
            return "Unit"
        }

        return `$ref`.split("/").last()
    }

    private fun toKotlinType(type: String): String {
        if (type == "string") {
            return "String"
        } else if (type == "integer") {
            return "Int"
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