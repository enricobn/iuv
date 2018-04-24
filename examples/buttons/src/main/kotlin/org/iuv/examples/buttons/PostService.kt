package org.iuv.examples.buttons

import org.iuv.shared.Task

data class Post(val userId: Int, val id: Int, val title: String, val body: String)

interface PostService {

    fun getPost(id: Int): Task<String, Post>

}