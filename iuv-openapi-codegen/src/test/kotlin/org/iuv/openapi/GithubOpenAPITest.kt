package org.iuv.openapi

import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import java.io.StringWriter

class GithubOpenAPITest {

    companion object {
        private val context = OpenAPIWriteContext("org.iuv.test.controllers",
                "org.iuv.test.client", "org.iuv.test.models")
        private lateinit var server : IUVAPIServer

        @BeforeClass @JvmStatic
        fun setUp() {
            server = OpenAPIReader.parse(getResource("/github.yaml"), "Github", context)!!
        }

        private fun getResource(resource: String) = GithubOpenAPITest::class.java.getResource(resource)

    }

    @Test
    fun githubClientImpl() {
        val sw = StringWriter()

        val api = server.apis.firstOrNull { it.name == "Networks" }

        if (api == null) {
            Assert.fail()
            return
        }

        sw.use {
            OpenAPIReader.runTemplate(getResource("/openapi/templates/clientImpl.mustache"), api, context, it)
            Assert.assertEquals("package org.iuv.test.client\n" +
                    "\n" +
                    "import kotlinx.serialization.ImplicitReflectionSerializer\n" +
                    "import kotlinx.serialization.internal.ArrayListSerializer\n" +
                    "import kotlinx.serialization.serializer\n" +
                    "import org.iuv.core.Http\n" +
                    "import org.iuv.core.HttpMethod\n" +
                    "import org.iuv.shared.Task\n" +
                    "import org.iuv.test.models.Event\n" +
                    "import org.iuv.test.models.Events\n" +
                    "\n" +
                    "class NetworksApiImpl(private val baseUrl : String = \"https://api.github.com/\") : NetworksApi {\n" +
                    "\n" +
                    "    @ImplicitReflectionSerializer\n" +
                    "    override fun getNetworksByOwnerByRepoEvents(owner : String, repo : String, Accept : String) : Task<String,Events> =\n" +
                    "        Http.runner(HttpMethod.Get, \"\$baseUrl/networks/\$owner/\$repo/events\", ArrayListSerializer(Event::class.serializer()))\n" +
                    "            .headers(\"Accept\" to Accept)\n" +
                    "            .run()\n" +
                    "\n" +
                    "}", it.toString())
        }
    }

    @Test
    fun githubComponents() {
        val sw = StringWriter()
        sw.use {
            OpenAPIReader.runTemplate(getResource("/openapi/templates/components.mustache"), server, context, it)
            Assert.assertEquals("", it.toString())
        }
    }

}