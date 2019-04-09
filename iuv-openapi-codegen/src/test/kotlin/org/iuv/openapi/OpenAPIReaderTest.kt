package org.iuv.openapi

import junit.framework.Assert.assertEquals
import junit.framework.Assert.fail
import org.junit.Test
import java.io.StringWriter

class OpenAPIReaderTest {

    @Test
    fun mustacheComponent() {
        val properties = listOf(
                IUVAPIComponentProperty("id", "Int", false),
                IUVAPIComponentProperty("name", "String", true))

        val component = IUVAPIComponent("Person", properties)

        val sw = StringWriter()
        sw.use {
            OpenAPIReader.runTemplate(getResource("/templates/component.mustache"), component, sw)
            assertEquals("data class Person(\n" +
                    "  val id : Int,\n" +
                    "  val name : String?\n" +
                    ")", sw.toString())
        }
    }

    @Test
    fun mustacheApi() {
        val api = OpenAPIReader.toIUVAPI(getResource("/petstore-expanded.yaml"), "PetStoreApi")

        if (api == null) {
            fail()
            return
        }

        val sw = StringWriter()
        sw.use {
            OpenAPIReader.runTemplate(getResource("/templates/api.mustache"), api, sw)
            assertEquals("import org.springframework.web.bind.annotation.DeleteMapping\n" +
                    "import org.springframework.web.bind.annotation.GetMapping\n" +
                    "import org.springframework.web.bind.annotation.PostMapping\n" +
                    "\n" +
                    "class PetStoreApi(\n" +
                    "    @GetMapping(\"/pets\")\n" +
                    "    fun findPets(tags : List<String>, limit : Int) : List<Pet> {\n" +
                    "\n" +
                    "    }\n" +
                    "\n" +
                    "    @PostMapping(\"/pets\")\n" +
                    "    fun addPet() : Pet {\n" +
                    "\n" +
                    "    }\n" +
                    "\n" +
                    "    @GetMapping(\"/pets/{id}\")\n" +
                    "    fun findPetById(id : Int) : Pet {\n" +
                    "\n" +
                    "    }\n" +
                    "\n" +
                    "    @DeleteMapping(\"/pets/{id}\")\n" +
                    "    fun deletePet(id : Int) : Unit {\n" +
                    "\n" +
                    "    }\n" +
                    "\n" +
                    ")", sw.toString())
        }
    }

    private fun getResource(resource: String) = this.javaClass.getResource(resource)

}