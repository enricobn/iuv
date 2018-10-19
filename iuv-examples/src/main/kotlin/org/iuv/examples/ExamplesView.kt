package org.iuv.examples

import org.iuv.core.Cmd
import org.iuv.core.HTML
import org.iuv.core.View
import org.iuv.examples.components.vBox

class ExamplesView() : View<ExamplesView.Model, ExamplesView.Message> {

    companion object {
        // Messages

        private fun HTML<Message>.linkToButtons(id: Int) = link("Buttons $id", "/buttons/$id")

        private fun HTML<Message>.link(text: String, url: String) {
            a {
                +text
                navigate(url)
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
                link("Components", "/components")
            }
        }

}