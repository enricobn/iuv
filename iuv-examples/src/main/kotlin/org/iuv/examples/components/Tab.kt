package org.iuv.examples.components

import org.iuv.core.ChildView
import org.iuv.core.Cmd
import org.iuv.core.HTML
import org.iuv.core.View

interface TabMessage

internal data class TabMessageWrapper(val tab: Int, val childMessage: Any) : TabMessage

data class SelectTab(val tab: Int) : TabMessage

data class TabModel(val activeTab: Int, val childModels: Map<Int,Any>)

class Tab : View<TabModel, TabMessage> {
    private val tabs = mutableListOf<TabData>()

    fun <MODEL : Any, MESSAGE: Any> add(text: String, view: View<MODEL,MESSAGE>) {
        tabs.add(TabData(text, view as View<Any,Any>))
    }

    override fun init(): Pair<TabModel, Cmd<TabMessage>> =
        Pair(TabModel(0, emptyMap()), Cmd { it.send(SelectTab(0)) })

    override fun update(message: TabMessage, model: TabModel): Pair<TabModel, Cmd<TabMessage>> =
        when (message) {
            is TabMessageWrapper -> createChildView(message.tab).update(message.childMessage, model)
            is SelectTab -> {
                val (newModel,newCmd) =
                    if (model.childModels.containsKey(message.tab)) {
                        Pair(model, Cmd.none())
                    } else {
                        val iuv = createChildView(message.tab)
                        iuv.initAndUpdate(message, model)
                    }
                Pair(newModel.copy(activeTab = message.tab), newCmd)
            }
            else -> Pair(model, Cmd.none())
        }


    override fun view(model: TabModel): HTML<TabMessage> =
        html {
            vBox {
                div {
                    tabs.forEachIndexed { index, data ->
                        mtButton {
                            +data.text

                            if (index == model.activeTab) classes = "mdl-button--accent"

                            onclick(SelectTab(index))
                        }
                    }
                }

                if (model.childModels.containsKey(model.activeTab)) {
                    add(createChildView(model.activeTab), model)
                }

            }
        }

    private fun createChildView(tab: Int): ChildView<TabModel, TabMessage, Any, Any> {
        val iuv = tabs[tab].view

        return ChildView(
                iuv,
                { TabMessageWrapper(tab, it) },
                { it.childModels[tab] ?: error("Cannot find tab '$tab'") },
                { parentModel, childModel ->
                    parentModel.copy(childModels = parentModel.childModels.plus(tab to childModel))
                }
        )
    }

}

private data class TabData(val text: String, val view: View<Any,Any>)