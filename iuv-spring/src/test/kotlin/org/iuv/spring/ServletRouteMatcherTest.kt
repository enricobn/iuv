package org.iuv.spring

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private const val emptyExpression = ""
private const val staticExpressionWithoutSlash = "search"
private const val staticExpression = "/$staticExpressionWithoutSlash"
private const val parameterizedExpression = "/{id}"
private val nullPath : String? = null

class ServletRouteMatcherTest {

    @Test
    fun givenAnEmptyExpressionWhenIMatchANullPathThenMatchSucceeds() {
        val sut = ServletRouteMatcher(emptyExpression)

        assertTrue(sut.matches(nullPath))
    }

    @Test
    fun givenAStaticExpressionWhenIMatchANullPathThenMatchFails() {
        val sut = ServletRouteMatcher(staticExpression)

        assertFalse(sut.matches(nullPath))
    }

    @Test
    fun givenAStaticExpressionWhenIMatchThatPathThenMatchSucceeds() {
        val sut = ServletRouteMatcher(staticExpression)

        assertTrue(sut.matches(staticExpression))
    }

    @Test
    fun givenAParameterizedExpressionWhenIMatchWIthNullPathThenMatchFails() {
        val sut = ServletRouteMatcher(parameterizedExpression)

        assertFalse(sut.matches(nullPath))
    }

    @Test
    fun givenAStaticExpressionWithoutSlashWhenIMatchThatPathThenMatchSucceeds() {
        val sut = ServletRouteMatcher(staticExpressionWithoutSlash)

        assertTrue(sut.matches(staticExpression))
    }

}
