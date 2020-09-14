package org.iuv.spring

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import javax.servlet.ServletException


class ServiceVOServletTest {

    @Test
    fun testSimpleServiceVO() {
        val routes = ServiceVOServlet.getRoutes(SimpleServiceVO::class)

        val countRoute = routes.first { it.function.name == "count" }

        assertTrue(countRoute.asynch)
    }

    @Test
    fun testInterface() {
        val routes = ServiceVOServlet.getRoutes(TestServiceVOImpl::class)

        val getByIdRoute = routes.first { it.function.name == "getById" }

        assertTrue(getByIdRoute.asynch)

        assertEquals("id", getByIdRoute.pathVariableParameters.first().variableName)

    }

    @Test
    fun pathVariableWithANameThatDoesNotMatchMapping() {

        val exception = assertThrows(
                ServletException::class.java
        ) {
            ServiceVOServlet.getRoutes(WrongPathVariableNameServiceVO::class)
        }

        assertTrue(exception.message!!.contains("does not match mapping", true))

    }

}

private class TestServiceVOImpl : ServiceVOServlet(), TestServiceVO {

    override fun getById(id: String): String {
        TODO("not implemented")
    }
}

private interface TestServiceVO {

    @GetMapping("/{id}")
    @RouteSerializer(StringIUVSerializer::class)
    @WebSocketAsync
    fun getById(@PathVariable id: String) : String

}

private class SimpleServiceVO : ServiceVOServlet() {

    @GetMapping("/count")
    @RouteSerializer(StringIUVSerializer::class)
    @WebSocketAsync
    fun count() = 1

}

private class WrongPathVariableNameServiceVO : ServiceVOServlet() {

    @GetMapping("/{id}")
    @RouteSerializer(StringIUVSerializer::class)
    @WebSocketAsync
    fun getById(@PathVariable("ad") id: String) = ""

}