package org.iuv.examples.buttons

import org.iuv.core.Cmd
import org.iuv.core.HTML
import org.iuv.core.IUVTest
import org.iuv.core.UV
import org.iuv.examples.components.vBox
import org.w3c.dom.events.Event
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LayoutTest : IUVTest<TestLayoutMessage>() {

    @Test
    fun childButtonClick() {
        val html = TestLayoutUV.view(TestLayoutModel(TestLayoutChildModel(0)))

        val testHtml = test(html)

        val testButton = testHtml.find(withName("button"))

        assertNotNull(testButton)

        testButton!!.callHandler("click", Event("click"))

        assertEquals(TestLayoutChildMessageWrapper(TestLayoutChildClick), testHtml.getMessages().first())
    }

    @Test
    fun attributesOnvBox() {
        val html = TestLayoutUV.view(TestLayoutModel(TestLayoutChildModel(0)))

        val testHtml = test(html)

        val testButton = testHtml.find(withName("button"))

        assertNotNull(testButton)

        assertEquals("testclass", testButton!!.parent!!.parent!!.html.getAttrs()["class"])
    }

    @Test
    fun view() {
        val html = TestLayoutUV.view(TestLayoutModel(TestLayoutChildModel(0)))

        val expectedHtml = html {
            div {
                classes = "testclass"
                div {
                    button {
                        +"clicked 0 times"

                        onClick { TestLayoutChildMessageWrapper(TestLayoutChildClick) }

                    }
                }
            }
        }

        assertSameHTML(expectedHtml, html)

    }
}

interface TestLayoutMessage

data class TestLayoutChildMessageWrapper(val message: TestLayoutChildMessage) :TestLayoutMessage

data class TestLayoutModel(val childModel: TestLayoutChildModel)

object TestLayoutUV : UV<TestLayoutModel, TestLayoutMessage> {

    override fun update(message: TestLayoutMessage, model: TestLayoutModel): Pair<TestLayoutModel, Cmd<TestLayoutMessage>> =
            Pair(model, Cmd.none())

    override fun view(model: TestLayoutModel): HTML<TestLayoutMessage> =
        html {
            vBox {
                classes = "testclass"
                add(TestChildUV.view(model.childModel), ::TestLayoutChildMessageWrapper)
            }
        }

}

interface TestLayoutChildMessage

object TestLayoutChildClick : TestLayoutChildMessage

data class TestLayoutChildModel(val clicked: Int)

object TestChildUV : UV<TestLayoutChildModel, TestLayoutChildMessage> {

    override fun update(message: TestLayoutChildMessage, model: TestLayoutChildModel): Pair<TestLayoutChildModel, Cmd<TestLayoutChildMessage>> =
        when (message) {
            is TestLayoutChildClick -> Pair(model.copy(clicked = model.clicked +1), Cmd.none())
            else -> Pair(model, Cmd.none())
        }

    override fun view(model: TestLayoutChildModel): HTML<TestLayoutChildMessage> =
        html {
            button {
                +"clicked ${model.clicked} times"
                onClick { TestLayoutChildClick }
            }
        }

}