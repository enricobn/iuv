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
                    "    private const val baseUrl = \"http://petstore.swagger.io/api\"\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    fun findPets(tags : List<String>, limit : Int) : Task<String,List<Pet>> =\n" +
                    "        Http.GET(\"\$baseUrl/pets\", ArrayListSerializer(Pet::class.serializer()), queryParams = mapOf(\"tags\" to tags, \"limit\" to limit))\n" +
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
    fun petstoreUrl() {
        val api = OpenAPIReader.parse(getResource("/petstore.json"), "PetStore", context)
        assertEquals("https://petstore.swagger.io/v2", api?.baseUrl)
    }

    @Test
    fun petstoreClient() {
        val api = OpenAPIReader.parse(getResource("/petstore.json"), "PetStore", context)

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
                    "import kotlinx.serialization.internal.StringSerializer\n" +
                    "import kotlinx.serialization.internal.UnitSerializer\n" +
                    "import kotlinx.serialization.serializer\n" +
                    "import org.iuv.test.models.Order\n" +
                    "import org.iuv.test.models.Pet\n" +
                    "import org.iuv.test.models.User\n" +
                    "\n" +
                    "object PetStoreClient {\n" +
                    "    private const val baseUrl = \"https://petstore.swagger.io/v2\"\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    fun addPet(payload : Pet) : Task<String,Pet> =\n" +
                    "        Http.POST(\"\$baseUrl/pet\", Pet::class.serializer(), payload, Pet::class.serializer())\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    fun updatePet(payload : Pet) : Task<String,Pet> =\n" +
                    "        Http.PUT(\"\$baseUrl/pet\", Pet::class.serializer(), payload, Pet::class.serializer())\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    fun findPetsByStatus(status : List<String>) : Task<String,List<Pet>> =\n" +
                    "        Http.GET(\"\$baseUrl/pet/findByStatus\", ArrayListSerializer(Pet::class.serializer()), queryParams = mapOf(\"status\" to status))\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    fun findPetsByTags(tags : List<String>) : Task<String,List<Pet>> =\n" +
                    "        Http.GET(\"\$baseUrl/pet/findByTags\", ArrayListSerializer(Pet::class.serializer()), queryParams = mapOf(\"tags\" to tags))\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    fun getPetById(petId : Long) : Task<String,Pet> =\n" +
                    "        Http.GET(\"\$baseUrl/pet/\$petId\", Pet::class.serializer())\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    fun updatePetWithForm(petId : Long, name : String, status : String) : Task<String,Unit> =\n" +
                    "        Http.POST(\"\$baseUrl/pet/\$petId\", UnitSerializer, formData = mapOf(\"name\" to name, \"status\" to status))\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    fun deletePet(api_key : String, petId : Long) : Task<String,Unit> =\n" +
                    "        Http.DELETE(\"\$baseUrl/pet/\$petId\", UnitSerializer)\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    fun placeOrder(payload : Order) : Task<String,Order> =\n" +
                    "        Http.POST(\"\$baseUrl/store/order\", Order::class.serializer(), payload, Order::class.serializer())\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    fun getOrderById(orderId : Long) : Task<String,Order> =\n" +
                    "        Http.GET(\"\$baseUrl/store/order/\$orderId\", Order::class.serializer())\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    fun deleteOrder(orderId : Long) : Task<String,Unit> =\n" +
                    "        Http.DELETE(\"\$baseUrl/store/order/\$orderId\", UnitSerializer)\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    fun createUser(payload : User) : Task<String,Unit> =\n" +
                    "        Http.POST(\"\$baseUrl/user\", UnitSerializer, payload, User::class.serializer())\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    fun createUsersWithArrayInput(payload : List<User>) : Task<String,Unit> =\n" +
                    "        Http.POST(\"\$baseUrl/user/createWithArray\", UnitSerializer, payload, ArrayListSerializer(User::class.serializer()))\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    fun createUsersWithListInput(payload : List<User>) : Task<String,Unit> =\n" +
                    "        Http.POST(\"\$baseUrl/user/createWithList\", UnitSerializer, payload, ArrayListSerializer(User::class.serializer()))\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    fun loginUser(username : String, password : String) : Task<String,String> =\n" +
                    "        Http.GET(\"\$baseUrl/user/login\", StringSerializer, queryParams = mapOf(\"username\" to username, \"password\" to password))\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    fun logoutUser() : Task<String,Unit> =\n" +
                    "        Http.GET(\"\$baseUrl/user/logout\", UnitSerializer)\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    fun getUserByName(username : String) : Task<String,User> =\n" +
                    "        Http.GET(\"\$baseUrl/user/\$username\", User::class.serializer())\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    fun updateUser(username : String, payload : User) : Task<String,User> =\n" +
                    "        Http.PUT(\"\$baseUrl/user/\$username\", User::class.serializer(), payload, User::class.serializer())\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    fun deleteUser(username : String) : Task<String,Unit> =\n" +
                    "        Http.DELETE(\"\$baseUrl/user/\$username\", UnitSerializer)\n" +
                    "\n" +
                    "}", sw.toString())
        }

    }

    private fun getResource(resource: String) = this.javaClass.getResource(resource)

}