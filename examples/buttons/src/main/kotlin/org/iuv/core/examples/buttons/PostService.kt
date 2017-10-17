package org.iuv.core.examples.buttons

import org.iuv.core.Cmd

interface PostService {

    fun <MESSAGE> getPost(id: Int, handler: (Post) -> MESSAGE) : Cmd<MESSAGE>

}