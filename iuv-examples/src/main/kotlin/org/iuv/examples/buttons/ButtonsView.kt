package org.iuv.examples.buttons

import org.iuv.core.Cmd
import org.iuv.core.HTML
import org.iuv.core.View
import org.w3c.dom.events.MouseEvent
import kotlin.browser.document

class ButtonsView(private val initialPostId: Int, postService: PostService) : View<ButtonsView.Model, ButtonsView.Message> {

    companion object {
        private val height = 500
        private val width = 10
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
                if (event is MouseEvent) {
                    messageBus.send(ButtonsMouseMove(event.screenX, event.screenY))
                }
            }
        }
    }

    override fun update(message: Message, model: Model): Pair<Model, Cmd<Message>> {
        when (message) {
            is ButtonsButtonMessage -> {
                val (updateModel, updateCmd) = buttonComponent
                        .update(message.message, model.buttonModels[message.index])

                val updateCmdMapped = updateCmd.map { ButtonsButtonMessage(it, message.index) }

                val newButtonModels = model.buttonModels.toMutableList()
                newButtonModels[message.index] = updateModel

                return Pair(model.copy(buttonModels = newButtonModels), updateCmdMapped)
            }
            is ButtonsMouseMove -> return Pair(model.copy(x = message.x, y = message.y), Cmd.none())
            is PostIdChanged -> {
                val newButtonModels = model.buttonModels.map { it.copy(postId = message.postId) }
                return Pair(model.copy(postId = message.postId, buttonModels = newButtonModels), Cmd.none())
            }
            else -> return Pair(model, Cmd.none())
        }
    }

    override fun view(model: Model): HTML<Message> {
        return html {
            +"Post ID: "
            input {
                autofocus = true
                value = model.postId.toString()
                onBlur { _,value -> PostIdChanged(value.toInt()) }
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
                                        add(buttonComponent.view(model.buttonModels[it])) { message: ButtonComponentMessage ->
                                            ButtonsButtonMessage(message, it)
                                        }
                                    }
                                }
                        }
                    }
                }
            }
        }
    }

}