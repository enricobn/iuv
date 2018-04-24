package org.iuv.examples.buttons

import org.iuv.core.Http
import org.iuv.shared.Task

class PostServiceImpl : PostService {

    override fun getPost(id: Int): Task<String, Post> {
        val url = "https://jsonplaceholder.typicode.com/posts/$id"
        return Http.GET(url, true)
    }

}