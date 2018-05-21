package org.iuv.examples

import org.iuv.core.IUVApplication
import org.iuv.core.IUVDebugger
import org.iuv.core.IUVRouter
import org.iuv.core.SnabbdomRenderer
import org.iuv.examples.buttons.ButtonsView
import org.iuv.examples.buttons.PostServiceImpl
import org.iuv.examples.buttons.PostsView
import org.iuv.examples.grid.GridView

class ExamplesMain {

    fun run() {
        val postService = PostServiceImpl()

        val router = IUVRouter(ExamplesView(postService))
        router.add("/buttons/:id") { ButtonsView(it["id"]!!.toInt(), postService) }
        router.add("/buttons1", ButtonsView(1, postService))
        router.add("/grid", GridView)
        router.add("/posts", PostsView(postService))

        val renderer = SnabbdomRenderer()
        renderer.onSubsequentPatch {
            // material lite must inject some stuff ...
            js("componentHandler.upgradeDom()")
        }
        val application = IUVApplication(IUVDebugger(router), renderer)
        application.run()
    }

}