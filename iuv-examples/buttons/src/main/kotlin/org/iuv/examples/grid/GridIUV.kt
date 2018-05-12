package org.iuv.examples.grid

import org.iuv.core.Cmd
import org.iuv.core.HTML
import org.iuv.core.IUV

// MESSAGES
interface GridIUVMessage

data class GridMessageWrapper(val gridMessage: GridMessage) : GridIUVMessage

data class DetailMessageWrapper(val detailMessage: DetailMessage) : GridIUVMessage

// MODEL
data class Result(val home: Int, val visitor: Int)

data class Match(val home: String, val visitor: String, val result: Result)

data class GridIUVModel(val gridModel: GridModel<Match>)

object GridIUV : IUV<GridIUVModel, GridIUVMessage> {

    val rows = listOf(
            Match("Juventus", "Napoli", Result(0, 0)),
            Match("Roma", "Milan", Result(1, 1))
    )

    val columns : List<Column<Match>> = listOf(
            Column("Home") { it.home },
            Column("Visitor") { it.visitor },
            Column("Result", { _ -> "Center" }) { "${it.result.home} - ${it.result.visitor}" }
    )

    private val grid = Grid<Match>(true)
    private val detail = Detail<Match>()

    override fun init(): Pair<GridIUVModel, Cmd<GridIUVMessage>> {
        val gridModel = grid.init(rows, columns)
        return Pair(GridIUVModel(gridModel), Cmd.none())
    }

    override fun update(message: GridIUVMessage, model: GridIUVModel): Pair<GridIUVModel, Cmd<GridIUVMessage>> {
        return when(message) {
            is GridMessageWrapper -> {
                val (updatedModel, updateCmd) = grid.update(message.gridMessage, model.gridModel)
                Pair(model.copy(gridModel = updatedModel), updateCmd.map(::GridMessageWrapper) )
            }
            else -> {
                Pair(model, Cmd.none())
            }
        }
    }

    override fun view(model: GridIUVModel): HTML<GridIUVMessage> {
        return html {
            div {
                style = "float: left; margin-right: 10px;"
                add(grid.view(model.gridModel), ::GridMessageWrapper)
            }
            div {
                style = "float: none;"
                // TODO I don't like to make the init every time, better to have a detailModel in GridIUVModel then update it.
                add(detail.view(detail.init(model.gridModel.getSelectedRow(), columns)), ::DetailMessageWrapper)
            }
        }
    }
}