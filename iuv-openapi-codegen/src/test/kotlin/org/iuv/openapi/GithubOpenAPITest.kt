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

            val expected = getResource("/GithubApiImpl.kt.expected").readText()

            Assert.assertEquals(expected, it.toString())
        }
    }

    @Test
    fun githubComponents() {
        val sw = StringWriter()
        sw.use {
            OpenAPIReader.runTemplate(getResource("/openapi/templates/components.mustache"), server, context, it)

            val expected = getResource("/Github.kt.expected").readText()

            Assert.assertEquals(expected, it.toString())
        }
    }

}

private fun getResource(resource: String) = GithubOpenAPITest::class.java.getResource(resource)