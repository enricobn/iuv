package org.iuv.examples

import org.iuv.core.Cmd
import org.iuv.core.HTML
import org.iuv.core.View
import org.iuv.examples.buttons.ButtonsView
import org.iuv.examples.buttons.PostService
import org.iuv.examples.components.Tab
import org.iuv.examples.components.TabMessage
import org.iuv.examples.components.TabModel
import org.iuv.examples.components.vBox
import org.iuv.examples.grid.GridView

// Model
data class ExamplesModel(val tabModel: TabModel, val x: Int, val y: Int)

// Message
interface ExamplesMessage

class ExamplesView(postService: PostService) : View<ExamplesModel, ExamplesMessage> {
    private val tab : Tab = Tab()

    companion object {
        // Messages

        private data class TabMessageWrapper(val message: TabMessage) : ExamplesMessage

        private fun HTML<ExamplesMessage>.linkToButtons(id: Int) = link("Buttons $id", "/buttons/$id")

        private fun HTML<ExamplesMessage>.link(text: String, url: String) {
            a {
                +text
                navigate(url)
            }
        }

    }

    init {
        tab.add("Buttons", ButtonsView(1, postService))
        tab.add("Grid", GridView)
    }

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
            else -> Pair(model, Cmd.none())
        }

    override fun view(model: ExamplesModel): HTML<ExamplesMessage> =
        html {
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
                link("Tabs", "/tabs")
                link("Mouse", "/mouse")
            }
        }

}