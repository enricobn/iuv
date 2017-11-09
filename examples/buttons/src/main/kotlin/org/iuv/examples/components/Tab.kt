package org.iuv.examples.components

import org.iuv.core.*

interface TabMessage

internal data class TabMessageWrapper(val tab: Int, val childMessage: Any) : TabMessage

data class SelectTab(val tab: Int) : TabMessage

data class TabModel(val activeTab: Int, val childModels: Map<Int,Any>)

class Tab : IUV<TabModel, TabMessage> {
    private val tabs = mutableListOf<TabData>()

    fun add(text: String, iuv: IUV<*,*>) {
        tabs.add(TabData(text, iuv))
    }

    override fun init(): Pair<TabModel, Cmd<TabMessage>> =
        Pair(TabModel(0, emptyMap()), Cmd.cmdOf { it.send(SelectTab(0)) })

    override fun update(message: TabMessage, model: TabModel): Pair<TabModel, Cmd<TabMessage>> =
        when (message) {
            is TabMessageWrapper -> createChildIUV(message.tab).update(message.childMessage, model)
            is SelectTab -> {
                val (newModel,newCmd) =
                    if (model.childModels.containsKey(message.tab)) {
                        Pair(model, Cmd.none())
                    } else {
                        val iuv = createChildIUV(message.tab)
                        iuv.init(model)
                    }
//                console.log(newModel.toString())
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

                            classes = "mdl-button--accent"

                            onClick { SelectTab(index) }
                        }
                    }
                }

                if (model.childModels.containsKey(model.activeTab)) {
                    createChildIUV(model.activeTab).view(model, this)
                }

            }
        }

    private fun createChildIUV(tab: Int): ChildIUV<TabModel, TabMessage, Any, Any> {
        val iuv = tabs[tab].iuv

        return ChildIUV(
                iuv as IUV<Any, Any>,
                { TabMessageWrapper(tab, it) },
                { it.childModels[tab]!! },
                { parentModel, childModel ->
                    parentModel.copy(childModels = parentModel.childModels.plus(Pair(tab, childModel)))
                }
        )
    }

}

private data class TabData(val text: String, val iuv: IUV<*,*>)