package org.iuv.examples

import org.iuv.core.Cmd
import org.iuv.core.HTML
import org.iuv.core.IUVRouter
import org.iuv.core.View
import org.iuv.examples.buttons.ButtonsView
import org.iuv.examples.buttons.PostService
import org.iuv.examples.components.*
import org.iuv.examples.grid.GridView

// Model
data class ExamplesModel(val tabModel: TabModel, val x: Int, val y: Int)

// Message
interface ExamplesMessage

class ExamplesView(postService: PostService) : View<ExamplesModel, ExamplesMessage> {
    private val tab : Tab = Tab()

    companion object {
        // Messages

        private data class Goto(val path: String) : ExamplesMessage

        private data class TabMessageWrapper(val message: TabMessage) : ExamplesMessage

        private data class MouseMove(val x: Int, val y: Int) : ExamplesMessage


        private fun HTML<ExamplesMessage>.linkToButtons(id: Int) = link("Buttons $id", "/buttons/$id")

        private fun HTML<ExamplesMessage>.link(text: String, url: String) {
            mtButton {
                +text
                onClick { Goto(url) }
            }
        }

    }

    init {
        tab.add("Buttons", ButtonsView(1, postService))
        tab.add("Grid", GridView)
    }

//    override fun subscriptions(model: ExamplesModel): Sub<ExamplesMessage> {
//        return DocumentEventSubFactoryImpl.mouseMove { MouseMove(it.screenX, it.screenY) }
//    }

    override fun init() : Pair<ExamplesModel, Cmd<ExamplesMessage>> {
        val (tabModel,tabCmd) = tab.init()
        return Pair(ExamplesModel(tabModel, 0, 0), tabCmd.map(::TabMessageWrapper))
    }

    override fun update(message: ExamplesMessage, model: ExamplesModel) : Pair<ExamplesModel, Cmd<ExamplesMessage>> =
        when (message) {
            is TabMessageWrapper -> {
                val (tabModel, tabCmd) = tab.update(message.message, model.tabModel)
                Pair(model.copy(tabModel = tabModel), tabCmd.map(::TabMessageWrapper))
            }
            is Goto -> Pair(model, IUVRouter.navigate(message.path))
            is MouseMove -> { Pair(model.copy(x = message.x, y = message.y), Cmd.none()) }
            else -> Pair(model, Cmd.none())
        }

    override fun view(model: ExamplesModel): HTML<ExamplesMessage> =
        html {
            +"${model.x},${model.y}"
            br()
            vBox {
                linkToButtons(1)
                linkToButtons(2)
                link("Fixed buttons1", "/buttons1")
                link("Invalid buttons 3 with error", "/buttons3")
                link("Grid", "/grid")
                link("Not existent route", "/notExistentRoute")
                link("Error", "/buttons/hello")
                link("Posts", "/posts")
                link("Mario", "/mario")
                add(tab.view(model.tabModel), ::TabMessageWrapper)
            }
        }

}