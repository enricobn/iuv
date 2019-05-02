package org.iuv.openapi

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import java.io.StringWriter

class OpenAPIReaderTest {
    private val context = OpenAPIWriteContext("org.iuv.test.controllers",
            "org.iuv.test.client", "org.iuv.test.models")

    @Test
    fun petstoreExpandedComponents() {
        val server = OpenAPIReader.parse(getResource("/petstore-expanded.yaml"), "PetStore",
                context, false)

        if (server == null) {
            fail()
            return
        }

        val sw = StringWriter()
        sw.use {
            OpenAPIReader.runTemplate(getResource("/openapi/templates/components.mustache"), server,
                    context, it)
            assertEquals("package org.iuv.test.models\n" +
                    "\n" +
                    "import kotlinx.serialization.Serializable\n" +
                    "\n" +
                    "@Serializable\n" +
                    "data class Pet(\n" +
                    "  val name : String? = null,\n" +
                    "  val tag : String? = null,\n" +
                    "  val id : Long? = null\n" +
                    ")\n" +
                    "\n" +
                    "@Serializable\n" +
                    "data class NewPet(\n" +
                    "  val name : String,\n" +
                    "  val tag : String? = null\n" +
                    ")\n" +
                    "\n" +
                    "@Serializable\n" +
                    "data class Error(\n" +
                    "  val code : Int,\n" +
                    "  val message : String\n" +
                    ")", sw.toString())
        }
    }

    @Test
    fun petStoreExpandedcontroller() {
        val server = OpenAPIReader.parse(getResource("/petstore-expanded.yaml"), "PetStore",
                context, false)

        if (server == null) {
            fail()
            return
        }

        val sw = StringWriter()
        sw.use {
            OpenAPIReader.runTemplate(getResource("/openapi/templates/controller.mustache"), server.apis[0], context, it)

            val expected = getResource("/PetstoreExpandedController.kt.expected").readText()

            assertEquals(expected, it.toString())

        }
    }

    @Test
    fun petstoreExpancedClientImpl() {
        val server = OpenAPIReader.parse(getResource("/petstore-expanded.yaml"), "PetStore",
                context, false)

        if (server == null) {
            fail()
            return
        }

        val sw = StringWriter()
        sw.use {
            OpenAPIReader.runTemplate(getResource("/openapi/templates/clientImpl.mustache"), server.apis[0], context, it)

            val expected = getResource("/PetstoreExpandedApiImpl.kt.expected").readText()

            assertEquals(expected, it.toString())
        }

    }

    @Test
    fun apiPathSubst() {
        val path = IUVAPIPath("/api/{key}/{id1}", emptyList())

        assertEquals("/api/\$key/\$id1", path.pathSubst)
    }

    @Test
    fun apiPathSubstWithNoParams() {
        val path = IUVAPIPath("/api", emptyList())

        assertEquals("/api", path.pathSubst)
    }

    @Test
    fun petstoreUrl() {
        val server = OpenAPIReader.parse(getResource("/petstore.json"), "PetStore",
                context, false)
        assertEquals("https://petstore.swagger.io/v2", server?.baseUrl)
    }

    @Test
    fun petstoreClientImpl() {
        val server = OpenAPIReader.parse(getResource("/petstore.json"), "Petstore",
                context, false)

        if (server == null) {
            fail()
            return
        }

        val sw = StringWriter()
        sw.use {
            OpenAPIReader.runTemplate(getResource("/openapi/templates/clientImpl.mustache"), server.apis[0], context, it)

            val expected = getResource("/PetstoreApiImpl.kt.expected").readText()

            assertEquals(expected, it.toString())
        }

    }

    @Test
    fun petstoreClient() {
        val server = OpenAPIReader.parse(getResource("/petstore.json"), "PetStore",
                context, false)

        if (server == null) {
            fail()
            return
        }

        val sw = StringWriter()
        sw.use {
            OpenAPIReader.runTemplate(getResource("/openapi/templates/client.mustache"), server.apis[0], context, it)

            val expected = getResource("/PetstoreApi.kt.expected").readText()

            assertEquals(expected, it.toString())

        }

    }

    @Test
    fun petstoreConroller() {
        val server = OpenAPIReader.parse(getResource("/petstore.json"), "PetStore",
                context, false)

        if (server == null) {
            fail()
            return
        }

        val sw = StringWriter()
        sw.use {
            OpenAPIReader.runTemplate(getResource("/openapi/templates/controller.mustache"), server.apis[0], context, it)

            val expected = getResource("/PetstoreController.kt.expected").readText()

            assertEquals(expected, it.toString())
        }
    }

}

private fun getResource(resource: String) = OpenAPIReaderTest::class.java.getResource(resource)

