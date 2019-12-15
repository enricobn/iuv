package org.iuv.cli

import org.junit.Assert.assertEquals
import org.junit.Test

class HtmlToIUVCommandParserTest {
    private val sut = HtmlToIUVCommandParser()

    @Test
    fun parse() {
        val source = "<div id=\"root\">divText<input><br></div>"

        val parsed = sut.parse(source)

        val expected =
                "div {\n" +
                "    id = \"root\"\n" +
                "    +\"divText\"\n" +
                "    \n" +
                "    input {\n" +
                "    }\n" +
                "\n" +
                "    br {\n" +
                "    }\n" +
                "}\n"

        assertEquals(expected, parsed)
    }

}