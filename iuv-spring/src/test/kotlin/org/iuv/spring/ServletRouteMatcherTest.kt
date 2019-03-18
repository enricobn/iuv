package org.iuv.spring

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ServletRouteMatcherTest {

    companion object {
        private val nullPath : String? = null
        private const val emptyExpression = ""
        private const val staticExpression = "/search"
        private const val staticExpressionWithoutSlash = "search"
        private const val parameterizedExpression = "/{id}"
    }

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
