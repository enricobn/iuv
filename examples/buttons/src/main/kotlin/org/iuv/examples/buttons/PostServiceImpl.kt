package org.iuv.examples.buttons

import org.iuv.core.Cmd
import org.iuv.core.GetAsync

class PostServiceImpl : PostService {

    override fun <MESSAGE> getPost(id: Int, handler: (Post) -> MESSAGE) : Cmd<MESSAGE> {
        val url = "https://jsonplaceholder.typicode.com/posts/$id"
        return GetAsync(url, handler)
    }

}