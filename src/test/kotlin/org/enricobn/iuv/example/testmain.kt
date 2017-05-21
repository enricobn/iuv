package org.enricobn.iuv.example

import org.enricobn.iuv.IUVLoop

fun main(args: Array<String>) {
    val vu = TestIUV()

    val loop = IUVLoop(vu)
    loop.run()
}
