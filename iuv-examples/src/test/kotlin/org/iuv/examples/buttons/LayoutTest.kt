package org.iuv.examples.buttons

import org.iuv.core.Cmd
import org.iuv.core.Component
import org.iuv.core.HTML
import org.iuv.core.IUVTest
import org.iuv.examples.components.vBox
import org.w3c.dom.events.Event
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LayoutTest : IUVTest<TestLayoutMessage>() {

    @Test
    fun childButtonClick() {
        val html = TestLayoutComponent.view(TestLayoutModel(TestLayoutChildModel(0)))

        val testHtml = test(html)

        val testButton = testHtml.find(withName("button"))

        assertNotNull(testButton)

        testButton.callHandler("click", Event("click"))

        assertEquals(TestLayoutChildMessageWrapper(TestLayoutChildClick), testHtml.getMessages().first())
    }

    @Test
    fun attributesOnvBox() {
        val html = TestLayoutComponent.view(TestLayoutModel(TestLayoutChildModel(0)))

        val testHtml = test(html)

        val testButton = testHtml.find(withName("button"))

        assertNotNull(testButton)

        assertEquals("testclass", testButton.parent!!.parent!!.html.getAttrs()["class"])
    }

    @Test
    fun view() {
        val html = TestLayoutComponent.view(TestLayoutModel(TestLayoutChildModel(0)))

        val expectedHtml = html {
            div {
                classes = "testclass"
                div {
                    button {
                        +"clicked 0 times"

                        onclick(TestLayoutChildMessageWrapper(TestLayoutChildClick))

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

object TestLayoutComponent : Component<TestLayoutModel, TestLayoutMessage> {

    override fun update(message: TestLayoutMessage, model: TestLayoutModel): Pair<TestLayoutModel, Cmd<TestLayoutMessage>> =
            Pair(model, Cmd.none())

    override fun view(model: TestLayoutModel): HTML<TestLayoutMessage> =
        html {
            vBox {
                classes = "testclass"
                add(TestChildComponent.view(model.childModel), ::TestLayoutChildMessageWrapper)
            }
        }

}

interface TestLayoutChildMessage

object TestLayoutChildClick : TestLayoutChildMessage

data class TestLayoutChildModel(val clicked: Int)

object TestChildComponent : Component<TestLayoutChildModel, TestLayoutChildMessage> {

    override fun update(message: TestLayoutChildMessage, model: TestLayoutChildModel): Pair<TestLayoutChildModel, Cmd<TestLayoutChildMessage>> =
        when (message) {
            is TestLayoutChildClick -> Pair(model.copy(clicked = model.clicked +1), Cmd.none())
            else -> Pair(model, Cmd.none())
        }

    override fun view(model: TestLayoutChildModel): HTML<TestLayoutChildMessage> =
        html {
            button {
                +"clicked ${model.clicked} times"
                onclick(TestLayoutChildClick)
            }
        }

}