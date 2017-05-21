package org.enricobn.iuv.example

import kotlinx.html.HtmlBlockTag
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.tr
import org.enricobn.iuv.IUV
import org.enricobn.iuv.Message
import org.enricobn.iuv.MessageBus

data class TestModel(val button1Model: ButtonModel,
                     val button2Model: ButtonModel)

class TestIUV : IUV<TestModel>() {
    private val height = 50
    private val width = 10
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

    override fun view(messageBus: MessageBus, model: TestModel): HtmlBlockTag.() -> Unit = {
        table {
            for (y in 1..height) {
                tr {
                    for (x in 1..width) {
                        td {
                            button1.render(this, messageBus, model.button1Model)
                            button2.render(this, messageBus, model.button2Model)
                        }
                    }
                }
            }
        }
    }

}