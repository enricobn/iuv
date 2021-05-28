package org.iuv.todomvc

import kotlinx.browser.localStorage
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.iuv.core.Cmd
import org.iuv.core.HTML
import org.iuv.core.View
import org.iuv.core.appendClasses
import org.iuv.core.html.elements.Input
import org.iuv.core.html.elements.Ul
import org.iuv.core.html.enums.Checked
import org.iuv.core.html.enums.InputType
import org.iuv.core.html.groups.FlowContent
import org.w3c.dom.get
import org.w3c.dom.set

@InternalSerializationApi
object TodoMVC : View<TodoMVC.Model, TodoMVC.Message> {

    enum class Filter(val isValid: (Todo) -> Boolean) {
        All({ true }),
        Active({ !it.completed }),
        Completed({ it.completed })
    }

    @Serializable
    data class Todo(val message: String, val completed: Boolean)

    @Serializable
    data class Model(val todos: List<Todo>, val filter: Filter, val edit: Set<Int>)

    interface Message

    private object None : Message

    private data class Delete(val index: Int) : Message

    private data class Add(val value: String) : Message

    private data class Check(val index: Int) : Message

    private object All : Message

    private object Active : Message

    private object Completed : Message

    private object ClearCompleted : Message

    private object ToggleAll : Message

    private data class Edit(val index: Int) : Message

    private data class EditCommit(val index: Int, val message: String) : Message

    private data class EditCancel(val index: Int) : Message

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
                Pair(model.copy(todos = model.todos + Todo(message.value, completed = false)), Cmd.none())
            }
            is Delete -> {
                Pair(model.copy(todos = model.todos.filterIndexed { index, _ -> index != message.index }), Cmd.none())
            }
            is Check -> {
                val todos = model.todos.mapIndexed { index, todo ->
                    if (index == message.index) {
                        todo.copy(completed = !todo.completed)
                    } else {
                        todo
                    }
                }
                Pair(model.copy(todos = todos), Cmd.none())
            }
            is All -> Pair(model.copy(filter = Filter.All), Cmd.none())
            is Active -> Pair(model.copy(filter = Filter.Active), Cmd.none())
            is Completed -> Pair(model.copy(filter = Filter.Completed), Cmd.none())
            is ClearCompleted -> Pair(model.copy(todos = model.todos.filter { !it.completed }), Cmd.none())
            is ToggleAll -> {
                val allCompleted = model.todos.all { it.completed }
                Pair(model.copy(todos = model.todos.map { it.copy(completed = !allCompleted) }), Cmd.none())
            }
            is Edit -> Pair(model.copy(edit = model.edit + message.index), Cmd.none())
            is EditCommit -> Pair(model.copy(edit = model.edit - message.index,
                    todos = model.todos.mapIndexed { index, todo ->
                        if (index == message.index)
                            todo.copy(message = message.message)
                        else
                            todo
                    }), Cmd.none())
            is EditCancel -> Pair(model.copy(edit = model.edit - message.index), Cmd.none())
            else -> Pair(model, Cmd.none())
        }

        if (model != result.first) {
            localStorage["todos"] = Json.encodeToString(Model::class.serializer(), result.first)
        }

        return result
    }

    override fun view(model: Model): HTML<Message> = html {
        println(model)

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

                    model.todos.forEachIndexed { index, todo ->
                        if (model.filter.isValid(todo)) {
                            renderTodo(index, todo, model.edit.contains(index))
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

                onEnter({ None }, ::Add)
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

    private fun Ul<Message>.renderTodo(index: Int, todo: Todo, edit: Boolean) {
        li {
            if (todo.completed) {
                classes = "completed"
            }

            if (edit) {
                println("editing $index")
                appendClasses("editing")
                runJs("document.getElementById(\"todo-$index\").focus(); ")
            }

            div {
                classes = "view"

                input {
                    classes = "toggle"
                    type = InputType.checkbox
                    checked = if (todo.completed)
                        Checked.checked
                    else
                        Checked.empty

                    onclick { _, _ -> Check(index) }
                }

                label {
                    +todo.message

                    ondblclick(Edit(index))
                }

                button {
                    classes = "destroy"
                    onclick(Delete(index))
                }
            }

            input {
                id = "todo-$index"
                classes = "edit"
                type = InputType.text
                value = todo.message

                onEnter({ EditCancel(index) }) { v -> EditCommit(index, v) }

                if (edit) {
                    onblur { _, v ->
                        EditCommit(index, v)
                    }
                }
            }
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

        //span {}
    }

    private fun HTML<Message>.snippet() {

    }

    private fun Input<Message>.onEnter(onFailure: (String) -> Message, onSuccess: (String) -> Message) {
        onkeydown { event, value ->
            if (event.keyCode == 13) {
                val input = event.currentTarget.asDynamic()
                val message = onSuccess(value)
                input.value = ""
                message
            } else if (event.keyCode == 27) {
                onFailure(value)
            } else {
                None
            }
        }
    }
}