package org.iuv.examples.buttons

import org.iuv.core.Http
import org.iuv.core.Task

class PostServiceImpl : PostService {

    override fun <MESSAGE> getPost(id: Int) : Task<Post,String,MESSAGE> {
        val url = "https://jsonplaceholder.typicode.com/posts/$id"
        return Http.GET(url, true)
    }

}