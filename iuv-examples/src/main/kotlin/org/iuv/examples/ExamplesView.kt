package org.iuv.examples

import org.iuv.core.Cmd
import org.iuv.core.HTML
import org.iuv.core.IUVRouter.Companion.navigate
import org.iuv.core.View
import org.iuv.core.html.elements.Div
import org.iuv.examples.components.vBox

class ExamplesView : View<ExamplesView.Model, ExamplesView.Message> {

    companion object {
        // Messages

        private fun Div<Message>.linkToButtons(id: Int) = link("Buttons $id", "/buttons/$id")

        private fun Div<Message>.link(text: String, url: String) {
            a {
                +text
                navigate<Message>(url)
            }

        }

    }

    // Model
    object Model

    // Message
    interface Message

    override fun init() : Pair<Model, Cmd<Message>> {
        return Pair(Model, Cmd.none())
    }

    override fun update(message: Message, model: Model) : Pair<Model, Cmd<Message>> =
        Pair(model, Cmd.none())

    override fun view(model: Model): HTML<Message> =
        html {
            vBox {
                style = "margin-left: 10px;margin-top: 10px;"
                link("Lots of buttons", "/buttons/1")
                link("Grid", "/grid")
                link("Posts", "/posts")
                link("Mario", "/mario")
                link("Tabs", "/tabs")
                link("Mouse", "/mouse")
                link("Components", "/components")
            }
        }

}