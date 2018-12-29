package org.iuv.examples.buttons

import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.serializer

class PostServiceImpl : PostService {

    @ImplicitReflectionSerializer
    override fun getPost(id: Int): Task<String, Post> {
        val url = "https://jsonplaceholder.typicode.com/posts/$id"
        return Http.GET(url, Post::class.serializer())
    }

    @ImplicitReflectionSerializer
    override fun getPosts(): Task<String, List<Post>> {
        val url = "https://jsonplaceholder.typicode.com/posts"
        return Http.GET(url, ArrayListSerializer(Post::class.serializer()))
    }
}