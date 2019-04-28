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
    fun controller() {
        val server = OpenAPIReader.parse(getResource("/petstore-expanded.yaml"), "PetStore",
                context, false)

        if (server == null) {
            fail()
            return
        }

        val sw = StringWriter()
        sw.use {
            OpenAPIReader.runTemplate(getResource("/openapi/templates/controller.mustache"), server.apis[0], context, it)
            assertEquals("package org.iuv.test.controllers\n" +
                    "\n" +
                    "import org.springframework.web.bind.annotation.DeleteMapping\n" +
                    "import org.springframework.web.bind.annotation.GetMapping\n" +
                    "import org.springframework.web.bind.annotation.PathVariable\n" +
                    "import org.springframework.web.bind.annotation.PostMapping\n" +
                    "import org.springframework.web.bind.annotation.RequestBody\n" +
                    "import org.springframework.web.bind.annotation.RequestParam\n" +
                    "import org.iuv.test.models.NewPet\n" +
                    "import org.iuv.test.models.Pet\n" +
                    "\n" +
                    "interface PetStoreController {\n" +
                    "\n" +
                    "    @GetMapping(\"/pets\")\n" +
                    "    fun findPets(@RequestParam tags : List<String>, @RequestParam limit : Int) : List<Pet>\n" +
                    "\n" +
                    "    @PostMapping(\"/pets\")\n" +
                    "    fun addPet(@RequestBody body : NewPet) : Pet\n" +
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
            assertEquals("package org.iuv.test.client\n" +
                    "\n" +
                    "import kotlinx.serialization.ImplicitReflectionSerializer\n" +
                    "import kotlinx.serialization.internal.ArrayListSerializer\n" +
                    "import kotlinx.serialization.internal.UnitSerializer\n" +
                    "import kotlinx.serialization.serializer\n" +
                    "import org.iuv.core.Http\n" +
                    "import org.iuv.core.HttpMethod\n" +
                    "import org.iuv.shared.Task\n" +
                    "import org.iuv.test.models.NewPet\n" +
                    "import org.iuv.test.models.Pet\n" +
                    "\n" +
                    "class PetStoreApiImpl(private val baseUrl : String = \"http://petstore.swagger.io/api\") : PetStoreApi {\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override fun findPets(tags : List<String>, limit : Int) : Task<String,List<Pet>> =\n" +
                    "        Http.runner(HttpMethod.Get, \"\$baseUrl/pets\", ArrayListSerializer(Pet::class.serializer()))\n" +
                    "            .queryParams(\n" +
                    "                \"tags\" to tags, \n" +
                    "                \"limit\" to limit\n" +
                    "            )\n" +
                    "            .run()\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override fun addPet(body : NewPet) : Task<String,Pet> =\n" +
                    "        Http.runner(HttpMethod.Post, \"\$baseUrl/pets\", Pet::class.serializer())\n" +
                    "            .body(body, NewPet::class.serializer())\n" +
                    "            .run()\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override fun findPetById(id : Long) : Task<String,Pet> =\n" +
                    "        Http.runner(HttpMethod.Get, \"\$baseUrl/pets/\$id\", Pet::class.serializer())\n" +
                    "            .run()\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override fun deletePet(id : Long) : Task<String,Unit> =\n" +
                    "        Http.runner(HttpMethod.Delete, \"\$baseUrl/pets/\$id\", UnitSerializer)\n" +
                    "            .run()\n" +
                    "\n" +
                    "}", sw.toString())
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
            assertEquals("package org.iuv.test.client\n" +
                    "\n" +
                    "import kotlinx.serialization.ImplicitReflectionSerializer\n" +
                    "import kotlinx.serialization.internal.ArrayListSerializer\n" +
                    "import kotlinx.serialization.internal.HashMapSerializer\n" +
                    "import kotlinx.serialization.internal.IntSerializer\n" +
                    "import kotlinx.serialization.internal.StringSerializer\n" +
                    "import kotlinx.serialization.internal.UnitSerializer\n" +
                    "import kotlinx.serialization.serializer\n" +
                    "import org.iuv.core.Http\n" +
                    "import org.iuv.core.HttpMethod\n" +
                    "import org.iuv.core.MultiPartData\n" +
                    "import org.iuv.core.MultipartFile\n" +
                    "import org.iuv.shared.Task\n" +
                    "import org.iuv.test.models.ApiResponse\n" +
                    "import org.iuv.test.models.Order\n" +
                    "import org.iuv.test.models.Pet\n" +
                    "import org.iuv.test.models.User\n" +
                    "\n" +
                    "class PetstoreApiImpl(private val baseUrl : String = \"https://petstore.swagger.io/v2\") : PetstoreApi {\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override fun addPet(body : Pet) : Task<String,Pet> =\n" +
                    "        Http.runner(HttpMethod.Post, \"\$baseUrl/pet\", Pet::class.serializer())\n" +
                    "            .body(body, Pet::class.serializer())\n" +
                    "            .run()\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override fun updatePet(body : Pet) : Task<String,Pet> =\n" +
                    "        Http.runner(HttpMethod.Put, \"\$baseUrl/pet\", Pet::class.serializer())\n" +
                    "            .body(body, Pet::class.serializer())\n" +
                    "            .run()\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override fun findPetsByStatus(status : List<String>) : Task<String,List<Pet>> =\n" +
                    "        Http.runner(HttpMethod.Get, \"\$baseUrl/pet/findByStatus\", ArrayListSerializer(Pet::class.serializer()))\n" +
                    "            .queryParams(\n" +
                    "                \"status\" to status\n" +
                    "            )\n" +
                    "            .run()\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override fun findPetsByTags(tags : List<String>) : Task<String,List<Pet>> =\n" +
                    "        Http.runner(HttpMethod.Get, \"\$baseUrl/pet/findByTags\", ArrayListSerializer(Pet::class.serializer()))\n" +
                    "            .queryParams(\n" +
                    "                \"tags\" to tags\n" +
                    "            )\n" +
                    "            .run()\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override fun getPetById(petId : Long) : Task<String,Pet> =\n" +
                    "        Http.runner(HttpMethod.Get, \"\$baseUrl/pet/\$petId\", Pet::class.serializer())\n" +
                    "            .run()\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override fun updatePetWithForm(petId : Long, name : String, status : String) : Task<String,Unit> =\n" +
                    "        Http.runner(HttpMethod.Post, \"\$baseUrl/pet/\$petId\", UnitSerializer)\n" +
                    "            .formData(\n" +
                    "                \"name\" to name, \n" +
                    "                \"status\" to status\n" +
                    "            )\n" +
                    "            .run()\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override fun deletePet(api_key : String, petId : Long) : Task<String,Unit> =\n" +
                    "        Http.runner(HttpMethod.Delete, \"\$baseUrl/pet/\$petId\", UnitSerializer)\n" +
                    "            .headers(\"api_key\" to api_key)\n" +
                    "            .run()\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override fun uploadFile(petId : Long, additionalMetadata : String, file : MultipartFile) : Task<String,ApiResponse> =\n" +
                    "        Http.runner(HttpMethod.Post, \"\$baseUrl/pet/\$petId/uploadImage\", ApiResponse::class.serializer())\n" +
                    "            .multiPartData(\n" +
                    "                MultiPartData.of(\"additionalMetadata\", additionalMetadata), \n" +
                    "                MultiPartData.of(\"file\", file)\n" +
                    "            )\n" +
                    "            .run()\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override fun getInventory() : Task<String,Map<String, Int>> =\n" +
                    "        Http.runner(HttpMethod.Get, \"\$baseUrl/store/inventory\", HashMapSerializer(StringSerializer,IntSerializer))\n" +
                    "            .run()\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override fun placeOrder(body : Order) : Task<String,Order> =\n" +
                    "        Http.runner(HttpMethod.Post, \"\$baseUrl/store/order\", Order::class.serializer())\n" +
                    "            .body(body, Order::class.serializer())\n" +
                    "            .run()\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override fun getOrderById(orderId : Long) : Task<String,Order> =\n" +
                    "        Http.runner(HttpMethod.Get, \"\$baseUrl/store/order/\$orderId\", Order::class.serializer())\n" +
                    "            .run()\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override fun deleteOrder(orderId : Long) : Task<String,Unit> =\n" +
                    "        Http.runner(HttpMethod.Delete, \"\$baseUrl/store/order/\$orderId\", UnitSerializer)\n" +
                    "            .run()\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override fun createUser(body : User) : Task<String,Unit> =\n" +
                    "        Http.runner(HttpMethod.Post, \"\$baseUrl/user\", UnitSerializer)\n" +
                    "            .body(body, User::class.serializer())\n" +
                    "            .run()\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override fun createUsersWithArrayInput(body : List<User>) : Task<String,Unit> =\n" +
                    "        Http.runner(HttpMethod.Post, \"\$baseUrl/user/createWithArray\", UnitSerializer)\n" +
                    "            .body(body, ArrayListSerializer(User::class.serializer()))\n" +
                    "            .run()\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override fun createUsersWithListInput(body : List<User>) : Task<String,Unit> =\n" +
                    "        Http.runner(HttpMethod.Post, \"\$baseUrl/user/createWithList\", UnitSerializer)\n" +
                    "            .body(body, ArrayListSerializer(User::class.serializer()))\n" +
                    "            .run()\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override fun loginUser(username : String, password : String) : Task<String,String> =\n" +
                    "        Http.runner(HttpMethod.Get, \"\$baseUrl/user/login\", StringSerializer)\n" +
                    "            .queryParams(\n" +
                    "                \"username\" to username, \n" +
                    "                \"password\" to password\n" +
                    "            )\n" +
                    "            .run()\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override fun logoutUser() : Task<String,Unit> =\n" +
                    "        Http.runner(HttpMethod.Get, \"\$baseUrl/user/logout\", UnitSerializer)\n" +
                    "            .run()\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override fun getUserByName(username : String) : Task<String,User> =\n" +
                    "        Http.runner(HttpMethod.Get, \"\$baseUrl/user/\$username\", User::class.serializer())\n" +
                    "            .run()\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override fun updateUser(username : String, body : User) : Task<String,User> =\n" +
                    "        Http.runner(HttpMethod.Put, \"\$baseUrl/user/\$username\", User::class.serializer())\n" +
                    "            .body(body, User::class.serializer())\n" +
                    "            .run()\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override fun deleteUser(username : String) : Task<String,Unit> =\n" +
                    "        Http.runner(HttpMethod.Delete, \"\$baseUrl/user/\$username\", UnitSerializer)\n" +
                    "            .run()\n" +
                    "\n" +
                    "}", sw.toString())
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
            assertEquals("package org.iuv.test.client\n" +
                    "\n" +
                    "import org.iuv.core.MultipartFile\n" +
                    "import org.iuv.shared.Task\n" +
                    "import org.iuv.test.models.ApiResponse\n" +
                    "import org.iuv.test.models.Order\n" +
                    "import org.iuv.test.models.Pet\n" +
                    "import org.iuv.test.models.User\n" +
                    "\n" +
                    "interface PetStoreApi {\n" +
                    "\n" +
                    "    fun addPet(body : Pet) : Task<String,Pet>\n" +
                    "\n" +
                    "    fun updatePet(body : Pet) : Task<String,Pet>\n" +
                    "\n" +
                    "    fun findPetsByStatus(status : List<String>) : Task<String,List<Pet>>\n" +
                    "\n" +
                    "    fun findPetsByTags(tags : List<String>) : Task<String,List<Pet>>\n" +
                    "\n" +
                    "    fun getPetById(petId : Long) : Task<String,Pet>\n" +
                    "\n" +
                    "    fun updatePetWithForm(petId : Long, name : String, status : String) : Task<String,Unit>\n" +
                    "\n" +
                    "    fun deletePet(api_key : String, petId : Long) : Task<String,Unit>\n" +
                    "\n" +
                    "    fun uploadFile(petId : Long, additionalMetadata : String, file : MultipartFile) : Task<String,ApiResponse>\n" +
                    "\n" +
                    "    fun getInventory() : Task<String,Map<String, Int>>\n" +
                    "\n" +
                    "    fun placeOrder(body : Order) : Task<String,Order>\n" +
                    "\n" +
                    "    fun getOrderById(orderId : Long) : Task<String,Order>\n" +
                    "\n" +
                    "    fun deleteOrder(orderId : Long) : Task<String,Unit>\n" +
                    "\n" +
                    "    fun createUser(body : User) : Task<String,Unit>\n" +
                    "\n" +
                    "    fun createUsersWithArrayInput(body : List<User>) : Task<String,Unit>\n" +
                    "\n" +
                    "    fun createUsersWithListInput(body : List<User>) : Task<String,Unit>\n" +
                    "\n" +
                    "    fun loginUser(username : String, password : String) : Task<String,String>\n" +
                    "\n" +
                    "    fun logoutUser() : Task<String,Unit>\n" +
                    "\n" +
                    "    fun getUserByName(username : String) : Task<String,User>\n" +
                    "\n" +
                    "    fun updateUser(username : String, body : User) : Task<String,User>\n" +
                    "\n" +
                    "    fun deleteUser(username : String) : Task<String,Unit>\n" +
                    "\n" +
                    "}", sw.toString())
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
            assertEquals("package org.iuv.test.controllers\n" +
                    "\n" +
                    "import org.springframework.web.bind.annotation.DeleteMapping\n" +
                    "import org.springframework.web.bind.annotation.GetMapping\n" +
                    "import org.springframework.web.bind.annotation.PathVariable\n" +
                    "import org.springframework.web.bind.annotation.PostMapping\n" +
                    "import org.springframework.web.bind.annotation.PutMapping\n" +
                    "import org.springframework.web.bind.annotation.RequestBody\n" +
                    "import org.springframework.web.bind.annotation.RequestHeader\n" +
                    "import org.springframework.web.bind.annotation.RequestParam\n" +
                    "import org.springframework.web.bind.annotation.RequestPart\n" +
                    "import org.springframework.web.multipart.MultipartFile\n" +
                    "import org.iuv.test.models.ApiResponse\n" +
                    "import org.iuv.test.models.Order\n" +
                    "import org.iuv.test.models.Pet\n" +
                    "import org.iuv.test.models.User\n" +
                    "\n" +
                    "interface PetStoreController {\n" +
                    "\n" +
                    "    @PostMapping(\"/pet\")\n" +
                    "    fun addPet(@RequestBody body : Pet) : Pet\n" +
                    "\n" +
                    "    @PutMapping(\"/pet\")\n" +
                    "    fun updatePet(@RequestBody body : Pet) : Pet\n" +
                    "\n" +
                    "    @GetMapping(\"/pet/findByStatus\")\n" +
                    "    fun findPetsByStatus(@RequestParam status : List<String>) : List<Pet>\n" +
                    "\n" +
                    "    @GetMapping(\"/pet/findByTags\")\n" +
                    "    fun findPetsByTags(@RequestParam tags : List<String>) : List<Pet>\n" +
                    "\n" +
                    "    @GetMapping(\"/pet/{petId}\")\n" +
                    "    fun getPetById(@PathVariable petId : Long) : Pet\n" +
                    "\n" +
                    "    @PostMapping(\"/pet/{petId}\")\n" +
                    "    fun updatePetWithForm(@PathVariable petId : Long, @RequestParam name : String, @RequestParam status : String) : Unit\n" +
                    "\n" +
                    "    @DeleteMapping(\"/pet/{petId}\")\n" +
                    "    fun deletePet(@RequestHeader api_key : String, @PathVariable petId : Long) : Unit\n" +
                    "\n" +
                    "    @PostMapping(\"/pet/{petId}/uploadImage\")\n" +
                    "    fun uploadFile(@PathVariable petId : Long, @RequestParam additionalMetadata : String, @RequestPart file : MultipartFile) : ApiResponse\n" +
                    "\n" +
                    "    @GetMapping(\"/store/inventory\")\n" +
                    "    fun getInventory() : Map<String, Int>\n" +
                    "\n" +
                    "    @PostMapping(\"/store/order\")\n" +
                    "    fun placeOrder(@RequestBody body : Order) : Order\n" +
                    "\n" +
                    "    @GetMapping(\"/store/order/{orderId}\")\n" +
                    "    fun getOrderById(@PathVariable orderId : Long) : Order\n" +
                    "\n" +
                    "    @DeleteMapping(\"/store/order/{orderId}\")\n" +
                    "    fun deleteOrder(@PathVariable orderId : Long) : Unit\n" +
                    "\n" +
                    "    @PostMapping(\"/user\")\n" +
                    "    fun createUser(@RequestBody body : User) : Unit\n" +
                    "\n" +
                    "    @PostMapping(\"/user/createWithArray\")\n" +
                    "    fun createUsersWithArrayInput(@RequestBody body : List<User>) : Unit\n" +
                    "\n" +
                    "    @PostMapping(\"/user/createWithList\")\n" +
                    "    fun createUsersWithListInput(@RequestBody body : List<User>) : Unit\n" +
                    "\n" +
                    "    @GetMapping(\"/user/login\")\n" +
                    "    fun loginUser(@RequestParam username : String, @RequestParam password : String) : String\n" +
                    "\n" +
                    "    @GetMapping(\"/user/logout\")\n" +
                    "    fun logoutUser() : Unit\n" +
                    "\n" +
                    "    @GetMapping(\"/user/{username}\")\n" +
                    "    fun getUserByName(@PathVariable username : String) : User\n" +
                    "\n" +
                    "    @PutMapping(\"/user/{username}\")\n" +
                    "    fun updateUser(@PathVariable username : String, @RequestBody body : User) : User\n" +
                    "\n" +
                    "    @DeleteMapping(\"/user/{username}\")\n" +
                    "    fun deleteUser(@PathVariable username : String) : Unit\n" +
                    "\n" +
                    "}", it.toString())
        }

    }

    private fun getResource(resource: String) = this.javaClass.getResource(resource)

}