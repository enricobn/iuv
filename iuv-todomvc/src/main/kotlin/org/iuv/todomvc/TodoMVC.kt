package org.iuv.todomvc

import kotlinx.browser.localStorage
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.iuv.core.Cmd
import org.iuv.core.HTML
import org.iuv.core.View
import org.iuv.core.html.elements.Input
import org.iuv.core.html.elements.Ul
import org.iuv.core.html.enums.Checked
import org.iuv.core.html.enums.InputType
import org.iuv.core.html.groups.FlowContent
import org.iuv.todomvc.TodoComponent.TodoModel
import org.w3c.dom.get
import org.w3c.dom.set

@InternalSerializationApi
object TodoMVC : View<TodoMVC.Model, TodoMVC.Message> {
    val todoComponent = TodoComponent()

    enum class Filter(val isValid: (TodoModel) -> Boolean) {
        All({ true }),
        Active({ !it.completed }),
        Completed({ it.completed })
    }

    @Serializable
    data class Model(val todos: List<TodoModel>, val filter: Filter, val edit: Set<Int>)

    interface Message

    private object None : Message

    private data class Add(val value: String) : Message

    private object All : Message

    private object Active : Message

    private object Completed : Message

    private object ClearCompleted : Message

    private object ToggleAll : Message

    private data class TodoChildMessage(val index: Int, val message: TodoComponent.TodoMessage) : Message

    override fun init(): Pair<Model, Cmd<Message>> {
        val stored = localStorage["todos"]
        val model = if (stored != null) {
            Json.decodeFromString(Model::class.serializer(), stored)
        } else {
            Model(emptyList(), Filter.All, emptySet())
        }
        return Pair(model, Cmd.none())
    }

    override fun update(message: Message, model: Model): Pair<Model, Cmd<Message>> {
        val result: Pair<Model, Cmd<Message>> = when (message) {
            is Add -> {
                val id = if (model.todos.isEmpty()) 0 else (model.todos.last().inputName.drop(5).toInt() + 1)
                Pair(model.copy(todos = model.todos + TodoModel("todo-$id", message.value)), Cmd.none())
            }
            is All -> Pair(model.copy(filter = Filter.All), Cmd.none())
            is Active -> Pair(model.copy(filter = Filter.Active), Cmd.none())
            is Completed -> Pair(model.copy(filter = Filter.Completed), Cmd.none())
            is ClearCompleted -> Pair(model.copy(todos = model.todos.filter { !it.completed }), Cmd.none())
            is ToggleAll -> {
                val allCompleted = model.todos.all { it.completed }
                Pair(model.copy(todos = model.todos.map { it.copy(completed = !allCompleted) }), Cmd.none())
            }
            is TodoChildMessage -> {
                val (childModel, childCmd) = todoComponent.update(message.message, model.todos[message.index])
                Pair(model.copy(todos = model.todos.update(message.index) { childModel}.filter { !it.deleted }),
                    childCmd.map { TodoChildMessage(message.index, it) })
            }
            else -> Pair(model, Cmd.none())
        }

        if (model != result.first) {
            localStorage["todos"] = Json.encodeToString(Model::class.serializer(), result.first)
        }

        return result
    }

    override fun view(model: Model): HTML<Message> = html {
        id = "root"
        style = "display:block"

        section {
            classes = "todoapp"

            renderHeader()

            section {
                classes = "main"

                if (model.todos.isNotEmpty()) {
                    renderToggleAll(model)
                }

                ul {
                    classes = "todo-list"

                    model.todos.filter(model.filter.isValid).forEachIndexed { index, todo ->
                        li {
                            add(todoComponent.view(todo)) { TodoChildMessage(index, it) }
                        }
                    }
                }
            }

            if (model.todos.isNotEmpty()) {
                renderFooter(model)
            }
        }
    }

    private fun FlowContent<Message>.renderHeader() {
        header {
            classes = "header"

            h1 {
                +"todos"
            }

            input {
                type = InputType.text
                classes = "new-todo"
                placeholder = "What needs to be done?"
                value = ""

                onEnter(None, { None }, ::Add)
            }
        }
    }

    private fun FlowContent<Message>.renderToggleAll(model: Model) {
        input {
            id = "toggle-all"
            classes = "toggle-all"
            type = InputType.checkbox
            if (model.todos.all { it.completed })
                checked = Checked.checked

            onclick { _, _ -> ToggleAll }
        }

        label {
            for_ = "toggle-all"
            title = "Mark all as complete"
        }
    }

    private fun FlowContent<Message>.renderFooter(model: Model) {
        footer {
            classes = "footer"

            val active = model.todos.count { !it.completed }

            span {
                classes = "todo-count"
                strong { +(active.toString()) }
                +" item left"
            }

            renderFilters(model)

            val completed = model.todos.size - active

            if (completed > 0) {
                button {
                    classes = "clear-completed"
                    +"Clear completed"

                    onclick(ClearCompleted)
                }
            }
        }
    }

    private fun FlowContent<Message>.renderFilters(model: Model) {
        ul {
            classes = "filters"

            renderFilter(model, Filter.All, All)

            renderFilter(model, Filter.Active, Active)

            renderFilter(model, Filter.Completed, Completed)
        }
    }

    private fun Ul<Message>.renderFilter(model: Model, filter: Filter, message: Message) {
        li {
            a {
                if (model.filter == filter) {
                    classes = "selected"
                }
                +filter.name

                onclick(message)
            }

            span { }
        }
    }

    fun <M> Input<M>.onEnter(noneMessage: M, onFailure: (String) -> M, onSuccess: (String) -> M) {
        onkeydown { event, value ->
            if (event.keyCode == 13) {
                val input = event.currentTarget.asDynamic()
                val message = onSuccess(value)
                input.value = ""
                message
            } else if (event.keyCode == 27) {
                onFailure(value)
            } else {
                noneMessage
            }
        }
    }

    private fun <E> List<E>.update(index: Int, fn: (E) -> E) : List<E> =
        mapIndexed { i, e -> if (i == index) fn(e) else e }
}