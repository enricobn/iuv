package org.iuv.xhtml

class Indenter(val size: Int = 2) {
    private var level = 0;

    private fun open() {
        level++
    }

    private fun close() {
        level--
    }

    fun println(s: String) {
        kotlin.io.println(" ".repeat(level * size) + s)
    }

    fun indent(init: Indenter.() -> Unit) {
        open()
        init.invoke(this)
        close()
    }

}