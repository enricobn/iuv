package org.enricobn.iuv.example

import org.enricobn.iuv.HTML
import org.enricobn.iuv.IUV
import org.enricobn.iuv.MessageBus
import kotlin.browser.document

// MODEL
data class TestModel(val buttonModels: List<ButtonModel>)

// MESSAGES
interface TestMessage

class TestButtonMessage(val message: ButtonComponentMessage, val index: Int) : TestMessage

class TestIUV : IUV<TestModel,TestMessage, TestMessage>() {
    companion object {
        private val height = 500
        private val width = 10
        private val buttonComponent = ButtonComponent<TestMessage>()
        private val time = document.ontimeupdate
    }

    fun init(): TestModel {
        return TestModel(
                (1..height).map { y ->
                    (1..width).map { x -> ButtonModel("Button " + index(y, x), false) }
                }
            .flatten())
    }

    override fun update(messageBus: MessageBus<TestMessage>, map: (TestMessage) -> TestMessage, message: TestMessage, model: TestModel): Pair<TestModel, (() -> Unit)?> {
        if (message is TestButtonMessage) {
            val newButtonModels = model.buttonModels.toMutableList()

            val updatedButton = buttonComponent.update(messageBus, {click -> TestButtonMessage(click, message.index)}, message.message, model.buttonModels[message.index])
            newButtonModels[message.index] = updatedButton.first

            val cmd : (() -> Unit)? =
                if (updatedButton.second != null) {
                    { -> updatedButton.second!!.invoke() }
                } else {
                    null
                }

            return Pair(TestModel(newButtonModels), cmd)
        } else {
            return Pair(model, null)
        }
    }

    override fun view(messageBus: MessageBus<TestMessage>, map: (TestMessage) -> TestMessage, model: TestModel): HTML.() -> Unit = {
        table {
            for (y in 1..height) {
                tr {
                    for (x in 1..width) {
                        val index = index(y, x)
                        td {
                            buttonComponent(messageBus, model.buttonModels[index], {click -> TestButtonMessage(click, index)} )
                        }
                    }
                }
            }
        }
    }

    private fun index(y: Int, x: Int) = (y - 1) * width + x - 1

}