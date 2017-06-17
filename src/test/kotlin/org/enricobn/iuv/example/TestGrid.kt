package org.enricobn.iuv.example

import org.enricobn.iuv.Cmd
import org.enricobn.iuv.HTML
import org.enricobn.iuv.IUV
import org.enricobn.iuv.Subscription

// MESSAGES
interface TestGridMessage

class TestGridGridMessage(val gridMessage: GridMessage) : TestGridMessage

// MODEL
data class Result(val home: Int, val visitor: Int)

data class Match(val home: String, val visitor: String, val result: Result)

data class TestGridModel(val gridModel: GridModel<Match>)

class TestGrid : IUV<TestGridModel,TestGridMessage> {

    companion object {
        val rows = listOf(
                Match("Juventus", "Napoli", Result(1, 0)),
                Match("Roma", "Milan", Result(1, 1))
        )

        val columns = listOf(
                Column("Home", fn = Match::home),
                Column("Visitor", fn = Match::visitor),
                Column("Result", classes = {_ -> "Center"}) {row : Match -> "${row.result.home} - ${row.result.visitor}"}
        )
    }

    private val grid = Grid<Match>()

    override fun init(): Pair<TestGridModel, Subscription<TestGridMessage>?> {
        return Pair(TestGridModel(grid.init(rows, columns)), null)
    }

    override fun update(message: TestGridMessage, model: TestGridModel): Pair<TestGridModel, Cmd<TestGridMessage>?> {
        return Pair(model, null)
    }

    override fun view(model: TestGridModel): HTML<TestGridMessage>.() -> Unit = {
        map(grid, model.gridModel, ::TestGridGridMessage)
    }
}