package org.enricobn.iuv.example

import kotlinx.html.HtmlBlockTag
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.tr
import org.enricobn.iuv.IUV
import org.enricobn.iuv.Message
import org.enricobn.iuv.MessageBus

data class TestModel(val buttonModels: List<ButtonModel>)

data class IndexedButtonComponent(val buttonComponent: ButtonComponent, val index: Int)

class TestIUV : IUV<TestModel>() {
    companion object {
        private val height = 200
        private val width = 10
    }

    private val buttons = (1..height).map { y ->
        (1..width).map { x ->
            val index = index(y, x)
            IndexedButtonComponent(ButtonComponent("Button " + index), index)
        }
    }.flatten()

    private val buttonsMap = buttons.associateBy { it.buttonComponent.ID }

    override fun init(): TestModel {
        return TestModel((1..height).map {
            (1..width).map { ButtonModel(false) }
        }.flatten())
    }

    override fun update(message: Message, model: TestModel): TestModel {
//        if (message is ButtonClick) {
//            val component = buttonsMap[message.id]
//            if (component is IndexedButtonComponent) {
//                val buttonModel = component.buttonComponent.update(message, model.buttonModels[component.index])
//                val list = model.buttonModels.toMutableList().apply {
//                    this[component.index] = buttonModel
//                }
//                return TestModel(list)
//            }
//        }
//        return model

        val buttonModels = (1..height).map { y ->
            (1..width).map { x ->
                buttons[index(y, x)].buttonComponent.update(message, model.buttonModels[index(y, x)])
            }
        }.flatten()


        return TestModel(buttonModels)
    }

    override fun view(messageBus: MessageBus, model: TestModel): HtmlBlockTag.() -> Unit = {
        table {
            for (y in 1..height) {
                tr {
                    for (x in 1..width) {
                        val index = index(y, x)
                        td {
                            buttons[index].buttonComponent.render(this, messageBus, model.buttonModels[index])
                        }
                    }
                }
            }
        }
    }

    private fun index(y: Int, x: Int) = (y - 1) * width + x - 1

}