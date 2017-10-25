package org.iuv.examples

import org.iuv.core.Cmd
import org.iuv.core.GotoMessage
import org.iuv.core.HTML
import org.iuv.core.IUV

// Model
class ExamplesModel

// Messages
interface ExamplesMessage

private data class ExamplesGoto(override val url: String) : GotoMessage, ExamplesMessage

class ExamplesIUV : IUV<ExamplesModel, ExamplesMessage> {
    override fun init() : Pair<ExamplesModel, Cmd<ExamplesMessage>> {
        return Pair(ExamplesModel(), Cmd.none())
    }

    override fun update(message: ExamplesMessage, model: ExamplesModel) : Pair<ExamplesModel, Cmd<ExamplesMessage>> =
        Pair(model, Cmd.none())

    override fun view(model: ExamplesModel): HTML<ExamplesMessage> =
        html {
            div {
                button {
                    +"Buttons"
                    onClick { _ -> ExamplesGoto("/buttons") }
                }
            }
            div {
                button {
                    +"Grid"
                    onClick { _ -> ExamplesGoto("/grid") }
                }
            }
            div {
                button {
                    +"Error"
                    onClick { _ -> ExamplesGoto("/error") }
                }
            }

        }

}