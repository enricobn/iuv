package org.iuv.openapi

import junit.framework.Assert.assertEquals
import org.junit.Test
import java.io.StringWriter

class OpenAPIReaderTest {

    @Test
    fun petStore() {
        val api = OpenAPIReader.toIUVAPI(getResource("/petstore-expanded.yaml"))
        println(api)
    }

    @Test
    fun mustacheComponent() {
        val properties = listOf(
                IUVAPIComponentProperty("id", "Int", false),
                IUVAPIComponentProperty("name", "String", true))

        val dto = IUVAPIComponent("Person", properties)

        val sw = StringWriter()
        sw.use {
            OpenAPIReader.runTemplate(getResource("/templates/component.mustache"), dto, sw)
            assertEquals("data class Person(\n" +
                    "  val id : Int,\n" +
                    "  val name : String?\n" +
                    ")", sw.toString())
        }
    }

    private fun getResource(resource: String) = this.javaClass.getResource(resource)

}