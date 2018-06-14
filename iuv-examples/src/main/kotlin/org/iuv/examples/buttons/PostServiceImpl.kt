package org.iuv.examples.buttons

import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.serializer
import org.iuv.core.Http
import org.iuv.shared.Task

class PostServiceImpl : PostService {

    override fun getPost(id: Int): Task<String, Post> {
        val url = "https://jsonplaceholder.typicode.com/posts/$id"
        return Http.GET(url, Post::class.serializer())
    }

    override fun getPosts(): Task<String, List<Post>> {
        val url = "https://jsonplaceholder.typicode.com/posts"
        return Http.GET(url, ArrayListSerializer(Post::class.serializer()))
    }
}