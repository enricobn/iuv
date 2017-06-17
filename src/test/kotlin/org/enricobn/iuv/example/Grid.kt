package org.enricobn.iuv.example

import org.enricobn.iuv.Cmd
import org.enricobn.iuv.HTML
import org.enricobn.iuv.UV

// MESSAGES
interface GridMessage

// MODEL
data class Column<in ROW>(val header: String, val classes: ((ROW) -> String?)? = null, val fn: (ROW) -> String)

data class GridModel<ROW>(val rows: List<ROW>, val columns: List<Column<ROW>>)

class Grid<ROW> : UV<GridModel<ROW>,GridMessage> {

    fun init(rows: List<ROW>, columns: List<Column<ROW>>) : GridModel<ROW> {
        return GridModel(rows, columns)
    }

    override fun update(message: GridMessage, model: GridModel<ROW>): Pair<GridModel<ROW>, Cmd<GridMessage>?> {
        return Pair(model, null)
    }

    override fun view(model: GridModel<ROW>): HTML<GridMessage>.() -> Unit = {
        table {
            thead {
                for (column in model.columns) {
                    th {
                        +column.header
                    }
                }
            }

            for (row in model.rows) {
                tr {
                    for (column in model.columns) {
                        td {
                            classes = column.classes?.invoke(row)
                            +column.fn(row)
                        }
                    }
                }
            }
        }
    }
}