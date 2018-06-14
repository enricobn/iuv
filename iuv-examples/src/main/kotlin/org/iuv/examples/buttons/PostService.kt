package org.iuv.examples.buttons

import kotlinx.serialization.Serializable
import org.iuv.shared.Task

@Serializable
data class Post(val userId: Int, val id: Int, val title: String, val body: String)

interface PostService {

    fun getPost(id: Int): Task<String, Post>

    fun getPosts(): Task<String, List<Post>>
}