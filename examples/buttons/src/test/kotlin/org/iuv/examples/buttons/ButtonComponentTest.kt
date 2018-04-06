package org.iuv.examples.buttons

import org.iuv.core.Cmd
import org.iuv.core.IUVTest
import org.iuv.core.Task
import org.w3c.dom.events.Event
import kotlin.test.Test
import kotlin.test.assertEquals

class ButtonComponentTest : IUVTest<ButtonComponentMessage>() {

    @Test
    fun init() {
        val service = MockedPostService()
        val buttonComponent = ButtonComponent(service)

        val buttonModel = buttonComponent.init("hello", 1)

        assertEquals(1, buttonModel.postId)
    }

    @Test
    fun updatePostTitle() {
        val service = MockedPostService()
        val buttonComponent = ButtonComponent(service)
        val buttonModel = buttonComponent.init("hello", 1)

        val (updatedButtonModel, cmd) = buttonComponent.update(PostTitle("title"), buttonModel)

        assertEquals("hello title", updatedButtonModel.selectedButtonModel.text)
        assertEquals(Cmd.none(), cmd)
    }

    @Test
    fun view() {
        val service = MockedPostService()
        val buttonComponent = ButtonComponent(service)
        val selectedButtonModel = SelectedButtonModel("test", true)
        val model = ButtonModel(1, selectedButtonModel)
        val html = buttonComponent.view(model)

        val expectedHtml = html {
            button {
                +"test"

                onClick { _ -> SelectedButtonMessageWrapper(SelectedButtonClick) }

                classes = "ButtonComponentSelected"
            }
        }

        assertSameHTML(expectedHtml, html)
    }

    @Test
    fun buttonClick() {
        val service = MockedPostService()
        val buttonComponent = ButtonComponent(service)
        val selectedButtonModel = SelectedButtonModel("test", true)
        val model = ButtonModel(1, selectedButtonModel)
        val html = buttonComponent.view(model)

        val testHtml = test(html)

        val testButton = testHtml.find(withName("button"))

        testButton!!.callHandler("click", Event("click"))

        assertEquals(SelectedButtonMessageWrapper(SelectedButtonClick), testHtml.getMessages().first())
    }
}

class MockedPostService : PostService {
    override fun <MESSAGE> getPost(id: Int): Task<Post, MESSAGE> {
        return object: Task<Post,MESSAGE>() {
            override fun start(onSuccess: (Post) -> Unit, onError: () -> Unit) {
                onSuccess(Post(1, 1, "Hello", "World"))
            }
        }
    }

}