package org.iuv.examples.grid

import org.iuv.core.Cmd
import org.iuv.core.HTML
import org.iuv.core.IUV
import kotlin.reflect.KFunction1

// MESSAGES
interface GridIUVMessage

class GridMessageWrapper(val gridMessage: GridMessage) : GridIUVMessage

class DetailMessageWrapper(val detailMessage: DetailMessage) : GridIUVMessage

// MODEL
data class Result(val home: Int, val visitor: Int)

data class Match(val home: String, val visitor: String, val result: Result)

data class GridIUVModel(val gridModel: GridModel<Match>)

class GridIUV : IUV<GridIUVModel, GridIUVMessage> {

    companion object {
        val rows = listOf(
                Match("Juventus", "Napoli", Result(1, 0)),
                Match("Roma", "Milan", Result(1, 1))
        )

        val columns : List<Column<Match>> = listOf(
                Column("Home") { it.home },
                Column("Visitor") { it.visitor },
                Column("Result", { _ -> "Center" }) { "${it.result.home} - ${it.result.visitor}" }
        )
    }

    private val grid = Grid<Match>()
    private val detail = Detail<Match>()

    override fun init(): Pair<GridIUVModel, Cmd<GridIUVMessage>?> {
        val gridModel = grid.init(rows, columns)
        return Pair(GridIUVModel(gridModel), null)
    }

    override fun update(message: GridIUVMessage, model: GridIUVModel): Pair<GridIUVModel, Cmd<GridIUVMessage>?> {
        return when(message) {
            is GridMessageWrapper -> {
                val (updatedModel, updateCmd) = grid.update(message.gridMessage, model.gridModel)
                Pair(model.copy(gridModel = updatedModel), updateCmd?.map(::GridMessageWrapper) )
            }
            else -> {
                Pair(model, null)
            }
        }
    }

    override fun view(model: GridIUVModel): HTML<GridIUVMessage> {
        return html {
            div {
                style = "float: left; margin-right: 10px;"
                grid.view(model.gridModel).map(this, ::GridMessageWrapper)
            }
            div {
                style = "float: none;"
                // TODO I don't like to make the init every time, better to have a detailModel in GridIUVModel then update it.
                detail.view(detail.init(model.gridModel.selectedRow, columns)).map(this, ::DetailMessageWrapper)
            }
        }
    }
}