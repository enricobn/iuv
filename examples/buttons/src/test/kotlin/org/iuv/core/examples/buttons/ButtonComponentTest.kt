package org.iuv.examples.buttons

import org.iuv.core.Cmd
import org.iuv.core.GetAsync
import org.iuv.core.MessageBus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ButtonComponentTest {

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
        assertNull(cmd)
    }
}

class MockedPostService : PostService {
    override fun <MESSAGE> getPost(id: Int, handler: (Post) -> MESSAGE): Cmd<MESSAGE> {
        return object: Cmd<MESSAGE> {
            override fun run(messageBus: MessageBus<MESSAGE>) {
                messageBus.send(handler.invoke(Post(1, 1, "Hello", "World")))
            }
        }
    }

}