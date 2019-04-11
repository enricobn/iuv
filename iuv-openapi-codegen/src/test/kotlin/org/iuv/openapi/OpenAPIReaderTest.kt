package org.iuv.openapi

import junit.framework.Assert.assertEquals
import junit.framework.Assert.fail
import org.junit.Test
import java.io.StringWriter

class OpenAPIReaderTest {

    @Test
    fun mustacheComponents() {
        val properties = listOf(
                IUVAPIComponentProperty("id", IUVAPIType("Int", IUVAPISerializer("IntIUVSerializer", "IntSerializer")), false),
                IUVAPIComponentProperty("name", IUVAPIType("String", IUVAPISerializer("StringIUVSerializer", "StringSerializer")), true))

        val component = IUVAPIComponent("Person", properties)

        val sw = StringWriter()
        sw.use {
            OpenAPIReader.runTemplate(getResource("/templates/components.mustache"),
                    mapOf("components" to listOf(component).calculateLast()), sw)
            assertEquals("data class Person(\n" +
                    "  val id : Int,\n" +
                    "  val name : String?\n" +
                    ")", sw.toString())
        }
    }

    @Test
    fun controller() {
        val api = OpenAPIReader.parse(getResource("/petstore-expanded.yaml"), "PetStore")

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
                    "    @RouteSerializer(ListPetIUVSerializer::class)\n" +
                    "    override fun findPets(@RequestParam tags : List<String>, @RequestParam limit : Int) : List<Pet>\n" +
                    "\n" +
                    "    @PostMapping(\"/pets\")\n" +
                    "    @RouteSerializer(PetIUVSerializer::class)\n" +
                    "    override fun addPet() : Pet\n" +
                    "\n" +
                    "    @GetMapping(\"/pets/{id}\")\n" +
                    "    @RouteSerializer(PetIUVSerializer::class)\n" +
                    "    override fun findPetById(@PathVariable id : Int) : Pet\n" +
                    "\n" +
                    "    @DeleteMapping(\"/pets/{id}\")\n" +
                    "    @RouteSerializer(UnitIUVSerializer::class)\n" +
                    "    override fun deletePet(@PathVariable id : Int) : Unit\n" +
                    "\n" +
                    "}", sw.toString())
        }
    }

    @Test
    fun api() {
        val api = OpenAPIReader.parse(getResource("/petstore-expanded.yaml"), "PetStore")

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

    @Test
    fun serializers() {
        val api = OpenAPIReader.parse(getResource("/petstore-expanded.yaml"), "PetStore")

        if (api == null) {
            fail()
            return
        }

        val sw = StringWriter()
        sw.use {
            OpenAPIReader.runTemplate(getResource("/templates/serializers.mustache"), api, sw)
            assertEquals("import kotlinx.serialization.ImplicitReflectionSerializer\n" +
                    "\n" +
                    "object ListPetIUVSerializer : IUVSerializer {\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override val serializer: KSerializer<*>\n" +
                    "        get() = ArrayListSerializer(Pet::class.serializer())\n" +
                    "}\n" +
                    "\n" +
                    "object PetIUVSerializer : IUVSerializer {\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override val serializer: KSerializer<*>\n" +
                    "        get() = Pet::class.serializer()\n" +
                    "}\n" +
                    "\n" +
                    "object NewPetIUVSerializer : IUVSerializer {\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override val serializer: KSerializer<*>\n" +
                    "        get() = NewPet::class.serializer()\n" +
                    "}\n" +
                    "\n" +
                    "object ListStringIUVSerializer : IUVSerializer {\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override val serializer: KSerializer<*>\n" +
                    "        get() = ArrayListSerializer(StringSerializer)\n" +
                    "}\n" +
                    "\n" +
                    "object IntIUVSerializer : IUVSerializer {\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override val serializer: KSerializer<*>\n" +
                    "        get() = IntSerializer\n" +
                    "}\n" +
                    "\n" +
                    "object UnitIUVSerializer : IUVSerializer {\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override val serializer: KSerializer<*>\n" +
                    "        get() = UnitSerializer\n" +
                    "}", sw.toString())
        }
    }

    private fun getResource(resource: String) = this.javaClass.getResource(resource)

}