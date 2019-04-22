package org.iuv.openapi

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import java.io.StringWriter

class OpenAPIReaderTest {
    private val context = OpenAPIWriteContext("org.iuv.test.controllers",
            "org.iuv.test.client", "org.iuv.test.models")

    @Test
    fun components() {
        val api = OpenAPIReader.parse(getResource("/petstore-expanded.yaml"), "PetStore", context)

        if (api == null) {
            fail()
            return
        }

        val sw = StringWriter()
        sw.use {
            OpenAPIReader.runTemplate(getResource("/openapi/templates/components.mustache"), api, context, it)
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
    fun controller() {
        val api = OpenAPIReader.parse(getResource("/petstore-expanded.yaml"), "PetStore", context)

        if (api == null) {
            fail()
            return
        }

        val sw = StringWriter()
        sw.use {
            OpenAPIReader.runTemplate(getResource("/openapi/templates/controller.mustache"), api, context, it)
            assertEquals("package org.iuv.test.controllers\n" +
                    "\n" +
                    "import org.iuv.test.models.NewPet\n" +
                    "import org.iuv.test.models.Pet\n" +
                    "import org.springframework.web.bind.annotation.DeleteMapping\n" +
                    "import org.springframework.web.bind.annotation.GetMapping\n" +
                    "import org.springframework.web.bind.annotation.PathVariable\n" +
                    "import org.springframework.web.bind.annotation.PostMapping\n" +
                    "import org.springframework.web.bind.annotation.RequestBody\n" +
                    "import org.springframework.web.bind.annotation.RequestParam\n" +
                    "\n" +
                    "interface PetStoreController {\n" +
                    "\n" +
                    "    @GetMapping(\"/pets\")\n" +
                    "    fun findPets(@RequestParam tags : List<String>, @RequestParam limit : Int) : List<Pet>\n" +
                    "\n" +
                    "    @PostMapping(\"/pets\")\n" +
                    "    fun addPet(@RequestBody payload : NewPet) : Pet\n" +
                    "\n" +
                    "    @GetMapping(\"/pets/{id}\")\n" +
                    "    fun findPetById(@PathVariable id : Long) : Pet\n" +
                    "\n" +
                    "    @DeleteMapping(\"/pets/{id}\")\n" +
                    "    fun deletePet(@PathVariable id : Long) : Unit\n" +
                    "\n" +
                    "}", sw.toString())
        }
    }

    @Test
    fun api() {
        val api = OpenAPIReader.parse(getResource("/petstore-expanded.yaml"), "PetStore", context)

        if (api == null) {
            fail()
            return
        }

        val sw = StringWriter()
        sw.use {
            OpenAPIReader.runTemplate(getResource("/openapi/templates/api.mustache"), api, context, it)
            assertEquals("interface PetStoreApi {\n" +
                    "\n" +
                    "    fun findPets(tags : List<String>, limit : Int) : List<Pet>\n" +
                    "\n" +
                    "    fun addPet(payload : NewPet) : Pet\n" +
                    "\n" +
                    "    fun findPetById(id : Long) : Pet\n" +
                    "\n" +
                    "    fun deletePet(id : Long) : Unit\n" +
                    "\n" +
                    "}", sw.toString())
        }

    }

    @Test
    fun serializers() {
        val api = OpenAPIReader.parse(getResource("/petstore-expanded.yaml"), "PetStore", context)

        if (api == null) {
            fail()
            return
        }

        val sw = StringWriter()
        sw.use {
            OpenAPIReader.runTemplate(getResource("/openapi/templates/serializers.mustache"), api, context, it)
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
                    "}\n" +
                    "\n" +
                    "object LongIUVSerializer : IUVSerializer {\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override val serializer: KSerializer<*>\n" +
                    "        get() = LongSerializer\n" +
                    "}", sw.toString())
        }
    }

    @Test
    fun client() {
        val api = OpenAPIReader.parse(getResource("/petstore-expanded.yaml"), "PetStore", context)

        if (api == null) {
            fail()
            return
        }

        val sw = StringWriter()
        sw.use {
            OpenAPIReader.runTemplate(getResource("/openapi/templates/client.mustache"), api, context, it)
            assertEquals("package org.iuv.test.client\n" +
                    "\n" +
                    "import kotlinx.serialization.ImplicitReflectionSerializer\n" +
                    "import org.iuv.shared.Task\n" +
                    "import org.iuv.core.Http\n" +
                    "\n" +
                    "import kotlinx.serialization.internal.ArrayListSerializer\n" +
                    "import kotlinx.serialization.internal.UnitSerializer\n" +
                    "import kotlinx.serialization.serializer\n" +
                    "import org.iuv.test.models.NewPet\n" +
                    "import org.iuv.test.models.Pet\n" +
                    "\n" +
                    "object PetStoreClient {\n" +
                    "    private val baseUrl = \"http://petstore.swagger.io/api\"\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    fun findPets(tags : List<String>, limit : Int) : Task<String,List<Pet>> =\n" +
                    "        Http.GET(\"\$baseUrl/pets\", ArrayListSerializer(Pet::class.serializer()))\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    fun addPet(payload : NewPet) : Task<String,Pet> =\n" +
                    "        Http.POST(\"\$baseUrl/pets\", Pet::class.serializer(), payload, NewPet::class.serializer())\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    fun findPetById(id : Long) : Task<String,Pet> =\n" +
                    "        Http.GET(\"\$baseUrl/pets/\$id\", Pet::class.serializer())\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    fun deletePet(id : Long) : Task<String,Unit> =\n" +
                    "        Http.DELETE(\"\$baseUrl/pets/\$id\", UnitSerializer)\n" +
                    "\n" +
                    "}", sw.toString())
        }

    }

    @Test
    fun apiPathSubst() {
        val path = IUVAPIPath("/api/{id}/{id1}", listOf())

        assertEquals("/api/\$id/\$id1", path.pathSubst)
    }

    @Test
    fun apiPathSubstWithNoParams() {
        val path = IUVAPIPath("/api", listOf())

        assertEquals("/api", path.pathSubst)
    }

    @Test
    fun petstore() {
        val api = OpenAPIReader.parse(getResource("/petstore.json"), "PetStore", context)
        assertEquals("https://petstore.swagger.io/v2", api?.baseUrl)
    }

    private fun getResource(resource: String) = this.javaClass.getResource(resource)

}