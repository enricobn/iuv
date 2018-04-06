package org.iuv.examples.buttons

import org.iuv.core.GetAsync
import org.iuv.core.Task

class PostServiceImpl : PostService {

    override fun <MESSAGE> getPost(id: Int) : Task<Post,MESSAGE> {
        val url = "https://jsonplaceholder.typicode.com/posts/$id"
        return GetAsync(url)
    }

}