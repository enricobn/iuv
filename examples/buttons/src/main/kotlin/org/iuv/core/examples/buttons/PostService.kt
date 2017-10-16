package org.iuv.core.examples.buttons

import org.iuv.core.GetAsync

interface PostService {

    fun <MESSAGE> getPost(id: Int, handler: (Post) -> MESSAGE) : GetAsync<Post,MESSAGE>

}