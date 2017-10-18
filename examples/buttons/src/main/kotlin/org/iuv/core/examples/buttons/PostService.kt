package org.iuv.core.examples.buttons

import org.iuv.core.Cmd

data class Post(val userId: Int, val id: Int, val title: String, val body: String)

interface PostService {

    fun <MESSAGE> getPost(id: Int, handler: (Post) -> MESSAGE) : Cmd<MESSAGE>

}