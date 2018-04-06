package org.iuv.examples.buttons

import org.iuv.core.Task

data class Post(val userId: Int, val id: Int, val title: String, val body: String)

interface PostService {

    fun <MESSAGE> getPost(id: Int) : Task<Post, MESSAGE>

}