package org.iuv.core.examples.buttons

import org.iuv.core.*
import org.iuv.core.Cmd.Companion.cmdOf
import org.w3c.dom.events.MouseEvent
import kotlin.browser.document

// MODEL
data class TestModel(val country: String, val buttonModels: List<ButtonModel>, val x : Int, val y : Int)

// MESSAGES
interface TestMessage

data class TestButtonMessage(val message: ButtonComponentMessage, val index: Int) : TestMessage

data class TestMouseMove(val x: Int, val y: Int) : TestMessage

data class CountryChanged(val country: String) : TestMessage

class TestIUV(private val initialCountry: String) : IUV<TestModel, TestMessage> {
    private val height = 500
    private val width = 10
    private val handleMouseMove = false

    private fun index(y: Int, x: Int) = (y - 1) * width + x - 1

    override fun init(): Pair<TestModel, Cmd<TestMessage>?> {
        val model = TestModel(initialCountry,
            (1..height)
                .map { y ->
                    (1..width).map { x -> ButtonComponent.init("Button " + index(y, x), initialCountry) }
                }
                .flatten(), 0, 0)

        return Pair(model, if (handleMouseMove) mouseMove() else null)
    }

    private fun mouseMove(): Cmd<TestMessage> {
        return cmdOf { messageBus ->
            document.onmousemove = { event ->
                if (event is MouseEvent) {
                    messageBus.send(TestMouseMove(event.screenX, event.screenY))
                }
            }
        }
    }

    override fun update(message: TestMessage, model: TestModel): Pair<TestModel, Cmd<TestMessage>?> {
        when (message) {
            is TestButtonMessage -> {
                val (updateModel, updateCmd) = ButtonComponent
                        .update(message.message, model.buttonModels[message.index])

                val updateCmdMapped = updateCmd?.map {TestButtonMessage(it, message.index) }

                val newButtonModels = model.buttonModels.toMutableList()
                newButtonModels[message.index] = updateModel

                return Pair(model.copy(buttonModels = newButtonModels), updateCmdMapped)
            }
            is TestMouseMove -> return Pair(model.copy(x = message.x, y = message.y), null)
            is CountryChanged -> {
                val newButtonModels = model.buttonModels.map { it.copy(country = message.country) }
                return Pair(model.copy(country = message.country, buttonModels = newButtonModels), null)
            }
            else -> return Pair(model, null)
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
                        (1..width)
                            .map { index(y, it) }
                            .forEach {
                                td {
                                    map(ButtonComponent, model.buttonModels[it]) { message: ButtonComponentMessage ->
                                        TestButtonMessage(message, it)
                                    }
                                }
                            }
                    }
                }
            }
        }
    }

}