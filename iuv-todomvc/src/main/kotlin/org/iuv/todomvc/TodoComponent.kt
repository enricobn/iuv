package org.iuv.todomvc

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import org.iuv.core.Cmd
import org.iuv.core.Component
import org.iuv.core.HTML
import org.iuv.core.appendClasses
import org.iuv.core.html.enums.Checked
import org.iuv.core.html.enums.InputType
import org.iuv.todomvc.TodoMVC.onEnter

@InternalSerializationApi
class TodoComponent : Component<TodoComponent.TodoModel, TodoComponent.TodoMessage> {
    interface TodoMessage
    object Check : TodoMessage
    object Edit : TodoMessage
    object Delete : TodoMessage
    object EditCancel : TodoMessage
    data class EditCommit(val message: String) : TodoMessage
    object None : TodoMessage

    @Serializable
    data class TodoModel(
        val inputName: String, val message: String, val completed: Boolean = false,
        val edit: Boolean = false, val deleted: Boolean = false
    )

    override fun update(message: TodoMessage, model: TodoModel): Pair<TodoModel, Cmd<TodoMessage>> =
        when (message) {
            is Check -> Pair(model.copy(completed = !model.completed), Cmd.none())
            is Edit -> Pair(model.copy(edit = true), Cmd.none())
            is Delete -> Pair(model.copy(deleted = true), Cmd.none())
            is EditCancel -> Pair(model.copy(edit = false), Cmd.none())
            is EditCommit -> Pair(model.copy(message = message.message), Cmd.none())
            else -> Pair(model, Cmd.none())
        }

    override fun view(model: TodoModel): HTML<TodoMessage> = html {
        if (model.completed) {
            classes = "completed"
        }

        if (model.edit) {
            appendClasses("editing")
            runJs("document.getElementById(\"${model.inputName}\").focus(); ")
        }

        div {
            classes = "view"

            input {
                classes = "toggle"
                type = InputType.checkbox
                checked = if (model.completed)
                    Checked.checked
                else
                    Checked.empty

                onclick { _, _ -> Check }
            }

            label {
                +model.message

                ondblclick(Edit)
            }

            button {
                classes = "destroy"
                onclick(Delete)
            }
        }

        input {
            id = model.inputName
            classes = "edit"
            type = InputType.text
            value = model.message

            onEnter(None, { EditCancel }) { v -> EditCommit(v) }

            if (model.edit) {
                onblur { _, v -> EditCommit(v) }
            }
        }
    }
}