package org.iuv.examples.buttons

import kotlinx.browser.document
import org.iuv.core.ChildComponent
import org.iuv.core.Cmd
import org.iuv.core.HTML
import org.iuv.core.View
import org.iuv.core.html.enums.Autofocus

class ButtonsView(private val initialPostId: Int, postService: PostService) : View<ButtonsView.Model, ButtonsView.Message> {

    companion object {
        private const val height = 500
        private const val width = 10
    }

    // MODEL
    data class Model(val postId: Int, val buttonModels: List<ButtonModel>, val x : Int, val y : Int)

    // MESSAGES
    interface Message

    data class ButtonsButtonMessage(val message: ButtonComponentMessage, val index: Int) : Message

    data class ButtonsMouseMove(val x: Int, val y: Int) : Message

    data class PostIdChanged(val postId: Int) : Message

    private val handleMouseMove = false
    private val buttonComponent = ButtonComponent(postService)

    private fun buttonChildComponent(index: Int) =
            ChildComponent<Model, Message, ButtonModel, ButtonComponentMessage>(buttonComponent,
                    { ButtonsButtonMessage(it, index) },
                    { it.buttonModels[index] },
                    { parentModel, childModel ->
                        val newButtonModels = parentModel.buttonModels.toMutableList()
                        newButtonModels[index] = childModel
                        parentModel.copy(buttonModels = newButtonModels)
                    })

    private fun index(y: Int, x: Int) = (y - 1) * width + x - 1

    override fun init(): Pair<Model, Cmd<Message>> {
        val model = Model(initialPostId,
                (1..height)
                        .map { y ->
                            (1..width).map { x -> buttonComponent.init("Button " + index(y, x), initialPostId) }
                        }
                        .flatten(), 0, 0)

        return Pair(model, if (handleMouseMove) mouseMove() else Cmd.none())
    }

    private fun mouseMove(): Cmd<Message> {
        return Cmd { messageBus ->
            document.onmousemove = { event ->
                messageBus.send(ButtonsMouseMove(event.screenX, event.screenY))
            }
        }
    }

    override fun update(message: Message, model: Model): Pair<Model, Cmd<Message>> {
        return when (message) {
            is ButtonsButtonMessage -> {
                val buttonChildComponent = buttonChildComponent(message.index)
                buttonChildComponent.update(message.message, model)
            }
            is ButtonsMouseMove -> Pair(model.copy(x = message.x, y = message.y), Cmd.none())
            is PostIdChanged -> {
                val newButtonModels = model.buttonModels.map { it.copy(postId = message.postId) }
                Pair(model.copy(postId = message.postId, buttonModels = newButtonModels), Cmd.none())
            }
            else -> Pair(model, Cmd.none())
        }
    }

    override fun view(model: Model): HTML<Message> {
        return html {
            +"Post ID: "
            input {
                autofocus = Autofocus.autofocus
                value = model.postId.toString()
                onblur { _, value -> PostIdChanged(value.toInt() as Int) }
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
                                    .map { index(y, it) }.forEach {
                                td {
                                    val buttonChildComponent = buttonChildComponent(it)
                                    add(buttonChildComponent, model)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}