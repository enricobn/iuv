package org.iuv.examples.grid

import org.iuv.core.Cmd
import org.iuv.core.HTML
import org.iuv.core.UV
import org.iuv.examples.components.IUVMDL
import org.iuv.examples.components.mdlTable
import org.iuv.examples.components.mdlTableCheckbox

// MESSAGES
interface GridMessage

data class GridOnRowClick(val row: Int) : GridMessage

object GridOnAllRowsClick : GridMessage

// MODEL
data class Column<in ROW>(val header: String, val classes: ((ROW) -> String?)? = null, val fn: (ROW) -> String)

data class GridModel<ROW>(val rows: List<ROW>, val columns: List<Column<ROW>>, val selectedRows: Set<Int>) {
    fun getSelectedRow() : ROW? = if (selectedRows.size == 1) {
        rows[selectedRows.first()]
    } else {
        null
    }
}

class Grid<ROW>(private val multiSelect : Boolean) : UV<GridModel<ROW>, GridMessage> {

    fun init(rows: List<ROW>, columns: List<Column<ROW>>) : GridModel<ROW> =
        GridModel(rows, columns,
            if (multiSelect || rows.isEmpty()) {
                emptySet()
            } else {
                setOf(0)
            }
        )

    override fun update(message: GridMessage, model: GridModel<ROW>): Pair<GridModel<ROW>, Cmd<GridMessage>> =
        when (message) {
            is GridOnRowClick -> {
                val newSelectedRows =
                        if (multiSelect) {
                            if (model.selectedRows.contains(message.row)) {
                                model.selectedRows.minus(message.row)
                            } else {
                                model.selectedRows.plus(message.row)
                            }
                        } else {
                            setOf(message.row)
                        }

                Pair(model.copy(selectedRows = newSelectedRows), Cmd.none())
            }
            is GridOnAllRowsClick -> Pair(model, Cmd.none()) // TODO
            else -> {
                Pair(model, Cmd.none())
            }
        }

    override fun view(model: GridModel<ROW>): HTML<GridMessage> {
        return html {
            mdlTable {
                thead {
                    tr {
                        if (multiSelect) {
                            th {
                                mdlTableCheckbox("0", model.rows.size == model.selectedRows.size) {
                                    onInput { _ -> GridOnAllRowsClick }
                                }
                            }
                        }

                        for ((header) in model.columns) {
                            th {
                                classes = IUVMDL.dataTableNonNumeric
                                +header
                            }
                        }
                    }
                }

                tbody {
                    for ((i, row) in model.rows.withIndex()) {

                        tr {
                            if (model.selectedRows.contains(i)) {
                                appendClasses(IUVMDL.isSelected)
                            }

                            if (multiSelect) {
                                td {
                                    mdlTableCheckbox((i + 1).toString(), model.selectedRows.contains(i)) {
                                        onInput { _ -> GridOnRowClick(i) }
                                    }
                                }
                            } else {
                                onClick { _ -> GridOnRowClick(i) }
                            }

                            for (column in model.columns) {
                                td {
                                    classes = IUVMDL.dataTableNonNumeric

                                    val cl = column.classes?.invoke(row)
                                    if (cl != null) {
                                        classes = classes + " " + cl
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

}