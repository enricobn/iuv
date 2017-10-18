package org.iuv.examples.buttons

import org.iuv.core.*
import org.iuv.core.Cmd.Companion.cmdOf
import org.w3c.dom.events.MouseEvent
import kotlin.browser.document

// MODEL
data class ButtonsModel(val postId: Int, val buttonModels: List<ButtonModel>, val x : Int, val y : Int)

// MESSAGES
interface ButtonsMessage

data class ButtonsButtonMessage(val message: ButtonComponentMessage, val index: Int) : ButtonsMessage

data class ButtonsMouseMove(val x: Int, val y: Int) : ButtonsMessage

data class PostIdChanged(val postId: Int) : ButtonsMessage

class ButtonsIUV(private val initialPostId: Int, postService: PostService) : IUV<ButtonsModel, ButtonsMessage> {

    companion object {
        private val height = 500
        private val width = 10
    }

    private val handleMouseMove = false
    private val buttonComponent = ButtonComponent(postService)

    private fun index(y: Int, x: Int) = (y - 1) * width + x - 1

    override fun init(): Pair<ButtonsModel, Cmd<ButtonsMessage>?> {
        val model = ButtonsModel(initialPostId,
                (1..height)
                        .map { y ->
                            (1..width).map { x -> buttonComponent.init("Button " + index(y, x), initialPostId) }
                        }
                        .flatten(), 0, 0)

        return Pair(model, if (handleMouseMove) mouseMove() else null)
    }

    private fun mouseMove(): Cmd<ButtonsMessage> {
        return cmdOf { messageBus ->
            document.onmousemove = { event ->
                if (event is MouseEvent) {
                    messageBus.send(ButtonsMouseMove(event.screenX, event.screenY))
                }
            }
        }
    }

    override fun update(message: ButtonsMessage, model: ButtonsModel): Pair<ButtonsModel, Cmd<ButtonsMessage>?> {
        when (message) {
            is ButtonsButtonMessage -> {
                val (updateModel, updateCmd) = buttonComponent
                        .update(message.message, model.buttonModels[message.index])

                val updateCmdMapped = updateCmd?.map { ButtonsButtonMessage(it, message.index) }

                val newButtonModels = model.buttonModels.toMutableList()
                newButtonModels[message.index] = updateModel

                return Pair(model.copy(buttonModels = newButtonModels), updateCmdMapped)
            }
            is ButtonsMouseMove -> return Pair(model.copy(x = message.x, y = message.y), null)
            is PostIdChanged -> {
                val newButtonModels = model.buttonModels.map { it.copy(postId = message.postId) }
                return Pair(model.copy(postId = message.postId, buttonModels = newButtonModels), null)
            }
            else -> return Pair(model, null)
        }
    }

    override fun view(model: ButtonsModel): HTML<ButtonsMessage>.() -> Unit = {
        +"Post ID: "
        input {
            autofocus = true
            value = model.postId.toString()
            onBlur { PostIdChanged(it.value.toInt()) }
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
                                    map(buttonComponent, model.buttonModels[it]) { message: ButtonComponentMessage ->
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