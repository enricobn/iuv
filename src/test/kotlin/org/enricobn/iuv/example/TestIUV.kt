package org.enricobn.iuv.example

import org.enricobn.iuv.HTML
import org.enricobn.iuv.IUV
import org.enricobn.iuv.Message
import org.enricobn.iuv.MessageBus

// MODEL
data class TestModel(val buttonModels: List<ButtonModel>)

// MESSAGE
open class TestMessage : Message()

class TestMessageClick(val click: ButtonClick, val index: Int) : TestMessage()

class TestIUV : IUV<TestModel,TestMessage, TestMessage>() {
    companion object {
        private val height = 500
        private val width = 10
        private val buttonComponent = ButtonComponent<TestMessage>()
    }

    fun init(): TestModel {
        return TestModel(
                (1..height).map { y ->
                    (1..width).map { x -> ButtonModel("Button " + index(y, x), false) }
                }
            .flatten())
    }

    override fun update(message: TestMessage, model: TestModel): TestModel {
        if (message is TestMessageClick) {
            val newButtonModels = model.buttonModels.toMutableList()
            newButtonModels[message.index] = buttonComponent.update(message.click, model.buttonModels[message.index])

            return TestModel(newButtonModels)
        } else {
            return model
        }
    }

    override fun view(messageBus: MessageBus, model: TestModel, map: (TestMessage) -> TestMessage): HTML.() -> Unit = {
        table {
            for (y in 1..height) {
                tr {
                    for (x in 1..width) {
                        val index = index(y, x)
                        td {
                            buttonComponent(messageBus, model.buttonModels[index], {click -> TestMessageClick(click, index)} )
                        }
                    }
                }
            }
        }
    }

    private fun index(y: Int, x: Int) = (y - 1) * width + x - 1

}