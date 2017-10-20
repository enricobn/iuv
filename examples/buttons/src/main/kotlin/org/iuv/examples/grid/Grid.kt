package org.iuv.examples.grid

import org.iuv.core.Cmd
import org.iuv.core.HTML
import org.iuv.core.UV

// MESSAGES
interface GridMessage

data class GridOnRowClick<out ROW>(val row: ROW) : GridMessage

// MODEL
data class Column<in ROW>(val header: String, val classes: ((ROW) -> String?)? = null, val fn: (ROW) -> String)

data class GridModel<ROW>(val rows: List<ROW>, val columns: List<Column<ROW>>, val selectedRow: ROW?)

class Grid<ROW> : UV<GridModel<ROW>, GridMessage> {

    fun init(rows: List<ROW>, columns: List<Column<ROW>>) : GridModel<ROW> {
        return GridModel(rows, columns, null)
    }

    override fun update(message: GridMessage, model: GridModel<ROW>): Pair<GridModel<ROW>, Cmd<GridMessage>?> {
        when(message) {
            is GridOnRowClick<*> -> {
                return Pair(model.copy(selectedRow = message.row as ROW), null)
            }
            else -> {
                return Pair(model, null)
            }
        }
    }

    override fun view(model: GridModel<ROW>): HTML<GridMessage> {
        return html {
            table {
                thead {
                    for ((header) in model.columns) {
                        th {
                            +header
                        }
                    }
                }

                for (row in model.rows) {
                    tr {
                        if (row == model.selectedRow) {
                            classes = "SelectedRow"
                        }

                        for (column in model.columns) {
                            td {
                                onClick { _ -> GridOnRowClick(row) }

                                val cl = column.classes?.invoke(row)
                                if (cl != null) {
                                    classes = cl
                                }

                                +column.fn(row)
                            }
                        }
                    }
                }
            }
        }
    }
}