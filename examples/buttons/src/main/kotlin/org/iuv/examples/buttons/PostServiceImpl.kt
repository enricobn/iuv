package org.iuv.examples.buttons

import kotlinx.serialization.serializer
import org.iuv.core.Http
import org.iuv.shared.Task

class PostServiceImpl : PostService {

    override fun getPost(id: Int): Task<String, Post> {
        val url = "https://jsonplaceholder.typicode.com/posts/$id"
        return Http.GET(url, true, Post::class.serializer())
    }

    override fun getPosts(): Task<String, Array<Post>> {
        val url = "https://jsonplaceholder.typicode.com/posts"
        return Http.GET(url, true, Array<Post>::class.serializer())
    }
}