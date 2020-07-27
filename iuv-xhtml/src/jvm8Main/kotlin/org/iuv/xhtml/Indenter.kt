package org.iuv.xhtml

import java.io.PrintStream

class Indenter(val size: Int = 2, val ps: PrintStream = System.out) {
    private var level = 0;

    private fun open() {
        level++
    }

    private fun close() {
        level--
    }

    fun println(s: String) {
        ps.println(" ".repeat(level * size) + s)
    }

    fun indent(init: Indenter.() -> Unit) {
        open()
        init.invoke(this)
        close()
    }

}