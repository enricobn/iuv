package org.iuv.examples.buttons

import org.iuv.core.Cmd
import org.iuv.core.HTML
import org.iuv.core.UV

// MODEL
data class ButtonModel(val postId: Int, val selectedButtonModel: SelectedButtonModel)

// MESSAGES
interface ButtonComponentMessage

data class SelectedButtonMessageWrapper(val selectedButtonMessage: SelectedButtonMessage) : ButtonComponentMessage

data class PostTitle(val title: String) : ButtonComponentMessage

private object Error : ButtonComponentMessage

class ButtonComponent(private val postService: PostService) : UV<ButtonModel, ButtonComponentMessage> {

    fun init(text: String, postId: Int) : ButtonModel {
        return ButtonModel(postId, SelectedButton.init(text))
    }

    override fun update(message: ButtonComponentMessage, model: ButtonModel): Pair<ButtonModel, Cmd<ButtonComponentMessage>> {
        when (message) {
            is SelectedButtonMessageWrapper -> {
                val (updatedModel, updateCmd) = SelectedButton.update(message.selectedButtonMessage, model.selectedButtonModel)

                val updateCmdMapped = updateCmd.map(::SelectedButtonMessageWrapper)

                val cmd =
                        if (model.selectedButtonModel.selected) {
                            val postCmd = postService.getPost<ButtonComponentMessage>(model.postId)
                                    .andThen { postService.getPost<ButtonComponentMessage>(it.id + 1) }
                                    .andThen { postService.getPost<ButtonComponentMessage>(it.id + 1) }
                                    .perform({PostTitle(it.title) }, { Error } )
                            Cmd(postCmd, updateCmdMapped)
                        } else {
                            updateCmdMapped
                        }

                return Pair(model.copy(selectedButtonModel = updatedModel), cmd)
            }
            is PostTitle -> {
                val text = model.selectedButtonModel.text + " " + message.title
                return Pair(model.copy(selectedButtonModel = model.selectedButtonModel.copy(text = text)), Cmd.none())
            }
            is Error -> {
                console.log("ButtonComponent.Error")
                return Pair(model, Cmd.none())
            }
            else -> return Pair(model, Cmd.none())
        }
    }

    override fun view(model: ButtonModel): HTML<ButtonComponentMessage> {
        return html {
            add(SelectedButton.view(model.selectedButtonModel), ::SelectedButtonMessageWrapper)
        }
    }

}