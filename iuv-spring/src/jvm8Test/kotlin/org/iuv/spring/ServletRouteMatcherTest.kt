package org.iuv.spring

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

private const val staticExpressionWithoutSlash = "search"
private const val staticExpression = "/$staticExpressionWithoutSlash"

class ServletRouteMatcherTest {

    @Test
    fun givenAnEmptyExpressionWhenIMatchANullPathThenMatchSucceeds() {
        val sut = ServletRouteMatcher("")

        assertTrue(sut.matches(null))
    }

    @Test
    fun givenAStaticExpressionWhenIMatchANullPathThenMatchFails() {
        val sut = ServletRouteMatcher(staticExpression)

        assertFalse(sut.matches(null))
    }

    @Test
    fun givenAStaticExpressionWhenIMatchThatPathThenMatchSucceeds() {
        val sut = ServletRouteMatcher(staticExpression)

        assertTrue(sut.matches(staticExpression))
    }

    @Test
    fun givenAParameterizedExpressionWhenIMatchWIthNullPathThenMatchFails() {
        val sut = ServletRouteMatcher("/{id}")

        assertFalse(sut.matches(null))
    }

    @Test
    fun givenAStaticExpressionWithoutSlashWhenIMatchThatPathThenMatchSucceeds() {
        val sut = ServletRouteMatcher(staticExpressionWithoutSlash)

        assertTrue(sut.matches(staticExpression))
    }

    @Test
    fun pathVariables() {
        val sut = ServletRouteMatcher("/{id1}/{id2}")
        val pathVariables = sut.pathVariables("myId1/myId2")

        assertEquals(mapOf("id1" to "myId1", "id2" to "myId2"), pathVariables)
    }
}
