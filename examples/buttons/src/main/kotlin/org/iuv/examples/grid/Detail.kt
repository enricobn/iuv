package org.iuv.examples.grid

import org.iuv.core.Cmd
import org.iuv.core.HTML
import org.iuv.core.UV

// MESSAGES
interface DetailMessage

// MODEL
data class DetailModel<ROW>(val row: ROW?, val columns: List<Column<ROW>>)

class Detail<ROW> : UV<DetailModel<ROW>, DetailMessage> {

    fun init(row: ROW?, columns: List<Column<ROW>>) : DetailModel<ROW> {
        return DetailModel(row, columns)
    }

    override fun update(message: DetailMessage, model: DetailModel<ROW>): Pair<DetailModel<ROW>, Cmd<DetailMessage>?> {
        return Pair(model, null)
    }

    override fun view(model: DetailModel<ROW>): HTML<DetailMessage>.() -> Unit = {
        table {
            style = "border: solid; border-width: thin;"

            for (column in model.columns) {
                tr {
                    td {
                        b {
                            +column.header
                        }
                    }
                    td {
                        if (model.row == null) {
                            +" "
                        } else {
                            +column.fn(model.row)
                        }
                    }
                }
            }
        }
    }
}