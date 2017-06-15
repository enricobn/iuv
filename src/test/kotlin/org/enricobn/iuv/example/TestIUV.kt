package org.enricobn.iuv.example

import org.enricobn.iuv.*
import org.w3c.dom.events.MouseEvent
import kotlin.browser.document

// MODEL
data class TestModel(val buttonModels: List<ButtonModel>, val x : Int, val y : Int)

// MESSAGES
interface TestMessage

data class TestButtonMessage(val message: ButtonComponentMessage, val index: Int) : TestMessage

data class TestMouseMove(val x: Int, val y: Int) : TestMessage

class TestIUV : IUV<TestModel,TestMessage, TestMessage> {
    companion object {
        private val height = 500
        private val width = 10
        private val buttonComponent = ButtonComponent<TestMessage>()
        private val handleMouseMove = false

        private fun index(y: Int, x: Int) = (y - 1) * width + x - 1
    }

    override fun init(): Pair<TestModel, Subscription<TestMessage>?> {
        return Pair(TestModel(
                (1..height).map { y ->
                    (1..width).map { x -> buttonComponent.init("Button " + index(y, x)) }
                }
            .flatten(), 0, 0), if (handleMouseMove) mouseMove() else null)
    }

    private fun mouseMove(): (MessageBus<TestMessage>) -> Unit {
        return { messageBus ->
            document.onmousemove = { event ->
                if (event is MouseEvent) {
                    messageBus.send(TestMouseMove(event.screenX, event.screenY))
                }
            }
        }
    }

    override fun update(map: (TestMessage) -> TestMessage, message: TestMessage, model: TestModel): Pair<TestModel, Cmd<TestMessage>?> {
        if (message is TestButtonMessage) {
            val newButtonModels = model.buttonModels.toMutableList()

            val updatedButton = buttonComponent.update({ buttonMessage -> TestButtonMessage(buttonMessage, message.index) },
                    message.message, model.buttonModels[message.index])
            newButtonModels[message.index] = updatedButton.first

            return Pair(TestModel(newButtonModels, model.x, model.y), updatedButton.second)
        } else if (message is TestMouseMove) {
            return Pair(TestModel(model.buttonModels, message.x, message.y), null)
        } else {
            return Pair(model, null)
        }
    }

    override fun view(messageBus: MessageBus<TestMessage>, map: (TestMessage) -> TestMessage, model: TestModel): HTML<TestMessage>.() -> Unit = {
        div {
            if (handleMouseMove) {
                button {
                    +("${model.x},${model.y}")
                }
            }
            table {
                for (y in 1..height) {
                    tr {
                        for (x in 1..width) {
                            val index = index(y, x)
                            td {
                                buttonComponent(messageBus, model.buttonModels[index], { buttonMessage -> TestButtonMessage(buttonMessage, index) })
                            }
                        }
                    }
                }
            }
        }
    }

}