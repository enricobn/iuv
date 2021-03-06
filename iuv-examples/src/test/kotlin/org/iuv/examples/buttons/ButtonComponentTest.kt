package org.iuv.examples.buttons

import org.iuv.core.Cmd
import org.iuv.core.IUVTest
import org.iuv.shared.Task
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

                onclick(SelectedButtonMessageWrapper(SelectedButtonClick))

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
    override fun getPosts(): Task<String, List<Post>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPost(id: Int): Task<String, Post> {
        return object: Task<String,Post> {
            override fun run(onFailure: (String) -> Unit, onSuccess: (Post) -> Unit) {
                onSuccess(Post(1, 1, "Hello", "World"))
            }
        }
    }

}