package org.iuv.openapi

import junit.framework.Assert.assertEquals
import junit.framework.Assert.fail
import org.junit.Test
import java.io.StringWriter

class OpenAPIReaderTest {

    @Test
    fun mustacheComponents() {
        val properties = listOf(
                IUVAPIComponentProperty("id", "Int", false),
                IUVAPIComponentProperty("name", "String", true))

        val component = IUVAPIComponent("Person", properties)

        val sw = StringWriter()
        sw.use {
            OpenAPIReader.runTemplate(getResource("/templates/components.mustache"),
                    mapOf("components" to listOf(component)), sw)
            assertEquals("data class Person(\n" +
                    "  val id : Int,\n" +
                    "  val name : String?\n" +
                    ")", sw.toString().trim())
        }
    }

    @Test
    fun controller() {
        val api = OpenAPIReader.toIUVAPI(getResource("/petstore-expanded.yaml"), "PetStore")

        if (api == null) {
            fail()
            return
        }

        val sw = StringWriter()
        sw.use {
            OpenAPIReader.runTemplate(getResource("/templates/controller.mustache"), api, sw)
            assertEquals("import org.springframework.web.bind.annotation.DeleteMapping\n" +
                    "import org.springframework.web.bind.annotation.GetMapping\n" +
                    "import org.springframework.web.bind.annotation.PostMapping\n" +
                    "\n" +
                    "interface PetStoreController : PetStoreApi {\n" +
                    "\n" +
                    "    @GetMapping(\"/pets\")\n" +
                    "    @RouteSerializer\n" +
                    "    override fun findPets(@RequestParam tags : List<String>, @RequestParam limit : Int) : List<Pet>\n" +
                    "\n" +
                    "    @PostMapping(\"/pets\")\n" +
                    "    @RouteSerializer\n" +
                    "    override fun addPet() : Pet\n" +
                    "\n" +
                    "    @GetMapping(\"/pets/{id}\")\n" +
                    "    @RouteSerializer\n" +
                    "    override fun findPetById(@PathVariable id : Int) : Pet\n" +
                    "\n" +
                    "    @DeleteMapping(\"/pets/{id}\")\n" +
                    "    @RouteSerializer\n" +
                    "    override fun deletePet(@PathVariable id : Int) : Unit\n" +
                    "\n" +
                    "}", sw.toString())
        }
    }

    @Test
    fun api() {
        val api = OpenAPIReader.toIUVAPI(getResource("/petstore-expanded.yaml"), "PetStore")

        if (api == null) {
            fail()
            return
        }

        val sw = StringWriter()
        sw.use {
            OpenAPIReader.runTemplate(getResource("/templates/api.mustache"), api, sw)
            assertEquals("interface PetStoreApi {\n" +
                    "\n" +
                    "    fun findPets(tags : List<String>, limit : Int) : List<Pet>\n" +
                    "\n" +
                    "    fun addPet() : Pet\n" +
                    "\n" +
                    "    fun findPetById(id : Int) : Pet\n" +
                    "\n" +
                    "    fun deletePet(id : Int) : Unit\n" +
                    "\n" +
                    "}", sw.toString())
        }

    }

    private fun getResource(resource: String) = this.javaClass.getResource(resource)

}