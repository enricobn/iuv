package org.iuv.examples.tabs

import org.iuv.core.Cmd
import org.iuv.core.HTML
import org.iuv.core.View
import org.iuv.examples.components.Tab
import org.iuv.examples.components.TabMessage
import org.iuv.examples.components.TabModel
import org.iuv.examples.components.mtButton

class TabsView : View<TabsView.Model, TabsView.Message> {
    private val tab : Tab = Tab()

    data class Model(val tabModel: TabModel)

    interface Message

    private data class TabMessageWrapper(val message: TabMessage) : Message

    init {
        tab.add("First", TabView("Hello"))
        tab.add("Second", TabView("World!"))
    }

    override fun init(): Pair<Model, Cmd<Message>> {
        val (tabModel,tabCmd) = tab.init()
        return Pair(Model(tabModel), tabCmd.map(::TabMessageWrapper))
    }

    override fun update(message: Message, model: Model): Pair<Model, Cmd<Message>> =
        when (message) {
            is TabMessageWrapper -> {
                val (tabModel, tabCmd) = tab.update(message.message, model.tabModel)
                Pair(model.copy(tabModel = tabModel), tabCmd.map(::TabMessageWrapper))
            }
            else -> Pair(model, Cmd.none())
        }

    override fun view(model: Model): HTML<Message> = html {
        add(tab.view(model.tabModel), ::TabMessageWrapper)
    }
}

class TabView(val msg: String) : View<TabView.Model, TabView.Message> {
    data class Model(val msg: String, val count: Int = 0)

    interface Message

    private object Add : Message

    override fun init(): Pair<Model, Cmd<Message>> {
        return Pair(Model(msg), Cmd.none())
    }

    override fun update(message: Message, model: Model): Pair<Model, Cmd<Message>> =
        when(message) {
            is Add -> {
                Pair(model.copy(count = model.count + 1), Cmd.none())
            }
            else -> Pair(model, Cmd.none())
    }

    override fun view(model: Model): HTML<Message> = html {
        br()

        mtButton {
            +"Add"
            onClick(Add)
        }

        br()

        0.rangeTo(model.count).forEach { _ ->
            +model.msg
        }
    }

}