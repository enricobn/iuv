package org.iuv.examples.buttons

import org.iuv.core.Cmd
import org.iuv.core.HTML
import org.iuv.core.IUV
import org.iuv.core.toCmd

class PostsIUV(private val postService: PostService) : IUV<PostsIUV.Model, PostsIUV.Message> {

    data class Model(val rows: List<Post>? = null)

    interface Message

    private data class Error(val message: String) : Message

    private data class Load(val rows: List<Post>) : Message

    override fun init(): Pair<Model, Cmd<Message>> =
        Pair(Model(), postService.getPosts().toCmd(::Error) {
            Load(it.asList())
        })

    override fun update(message: Message, model: Model): Pair<Model, Cmd<Message>> =
        when(message) {
            is Load -> Pair(model.copy(rows = message.rows), Cmd.none())
            else -> Pair(model, Cmd.none())
        }

    override fun view(model: Model): HTML<Message> =
        html {
            if (model.rows == null)
                +"Loading ..."
            else
                model.rows.forEach {
                    +("${it.id} - ${it.title}")
                    br()
                }
        }
}