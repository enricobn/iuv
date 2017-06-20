package org.iuv.core.example

import org.iuv.core.*
import org.w3c.dom.events.MouseEvent
import kotlin.browser.document

// MODEL
data class TestModel(val country: String, val buttonModels: List<ButtonModel>, val x : Int, val y : Int)

// MESSAGES
interface TestMessage

data class TestButtonMessage(val message: ButtonComponentMessage, val index: Int) : TestMessage

data class TestMouseMove(val x: Int, val y: Int) : TestMessage

data class CountryChanged(val country: String) : TestMessage

class TestIUV(val initialCountry: String) : IUV<TestModel, TestMessage> {
    private val height = 500
    private val width = 10
    private val handleMouseMove = false

    private fun index(y: Int, x: Int) = (y - 1) * width + x - 1

    override fun init(): Pair<TestModel, Subscription<TestMessage>?> {
        return Pair(TestModel(initialCountry,
                (1..height).map { y ->
                    (1..width).map { x -> ButtonComponent.init("Button " + index(y, x), initialCountry) }
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

    override fun update(message: TestMessage, model: TestModel): Pair<TestModel, Cmd<TestMessage>?> {
        if (message is TestButtonMessage) {
            val newButtonModels = model.buttonModels.toMutableList()

            val updatedButton = ButtonComponent.update(message.message,
                    model.buttonModels[message.index])

            val updateButtonCmd = updatedButton.second?.map {msg -> TestButtonMessage(msg, message.index) }

            newButtonModels[message.index] = updatedButton.first

            return Pair(model.copy(buttonModels = newButtonModels), updateButtonCmd)
        } else if (message is TestMouseMove) {
            return Pair(model.copy(x = message.x,y = message.y), null)
        } else if (message is CountryChanged) {
            return Pair(model.copy(country = message.country, buttonModels = model.buttonModels.map { buttonModel -> buttonModel.copy(country = message.country) }), null)
        } else {
            return Pair(model, null)
        }
    }

    override fun view(model: TestModel): HTML<TestMessage>.() -> Unit = {
        +"Country: "
        input {
            autofocus = true
            value = model.country
            onBlur { CountryChanged(it.value) }
        }
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
                                map(ButtonComponent, model.buttonModels[index]) { message: ButtonComponentMessage ->
                                    TestButtonMessage(message, index)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}