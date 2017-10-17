package org.iuv.examples.grid

import org.iuv.core.Cmd
import org.iuv.core.HTML
import org.iuv.core.IUV

// MESSAGES
interface GridIUVMessage

class GridMessageWrapper(val gridMessage: GridMessage) : GridIUVMessage

// MODEL
data class Result(val home: Int, val visitor: Int)

data class Match(val home: String, val visitor: String, val result: Result)

data class TestGridModel(val gridModel: GridModel<Match>)

class GridIUV : IUV<TestGridModel, GridIUVMessage> {

    companion object {
        val rows = listOf(
                Match("Juventus", "Napoli", Result(1, 0)),
                Match("Roma", "Milan", Result(1, 1))
        )

        val columns = listOf(
                Column("Home", fn = Match::home),
                Column("Visitor", fn = Match::visitor),
                Column("Result", classes = { _ -> "Center" }) { row: Match -> "${row.result.home} - ${row.result.visitor}" }
        )
    }

    private val grid = Grid<Match>()

    override fun init(): Pair<TestGridModel, Cmd<GridIUVMessage>?> {
        return Pair(TestGridModel(grid.init(rows, columns)), null)
    }

    override fun update(message: GridIUVMessage, model: TestGridModel): Pair<TestGridModel, Cmd<GridIUVMessage>?> {
        return Pair(model, null)
    }

    override fun view(model: TestGridModel): HTML<GridIUVMessage>.() -> Unit = {
        map(grid, model.gridModel, ::GridMessageWrapper)
    }
}