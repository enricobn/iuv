package org.enricobn.iuv.example

import kotlinx.html.dom.create
import kotlinx.html.js.div
import org.enricobn.iuv.IUV
import org.enricobn.iuv.Message
import org.enricobn.iuv.MessageBus
import org.w3c.dom.HTMLElement
import kotlin.browser.document

data class TestModel(val button1Model: ButtonModel,
                     val button2Model: ButtonModel)

class TestIUV : IUV<TestModel>() {

    private val button1 = ButtonComponent("Button 1")
    private val button2 = ButtonComponent("Button 2")

    override fun init(): TestModel {
        return TestModel(ButtonModel(false), ButtonModel(false))
    }

    override fun update(message: Message, model: TestModel): TestModel {
        val button1Model = button1.update(message, model.button1Model)
        val button2Model = button2.update(message, model.button2Model)
        return TestModel(button1Model, button2Model)
    }

    override fun view(messageBus: MessageBus, model: TestModel): HTMLElement {
        val div = document.create.div {
        }

        div.append(
                button1.view(messageBus, model.button1Model),
                button2.view(messageBus, model.button2Model)
        )

        return div
    }

}