package org.iuv.idea.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.EditorModificationUtil
import com.intellij.psi.codeStyle.CodeStyleManager
import org.iuv.cli.HtmlToIUVCommandParser
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor

class HtmlToIUVAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val editor = e.getData(CommonDataKeys.EDITOR)

        if (editor != null && project != null) {
            val offset = editor.caretModel.primaryCaret.offset
            val styleManager = CodeStyleManager.getInstance(project)

            val file = e.getData(LangDataKeys.PSI_FILE)

            val transferable = Toolkit.getDefaultToolkit().systemClipboard.getContents(null)

            if (file != null && transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {

                val html = transferable.getTransferData(DataFlavor.stringFlavor) as String

                val parsed = HtmlToIUVCommandParser().parse(html)

                WriteCommandAction.runWriteCommandAction(project) {
                    val caret = EditorModificationUtil.insertStringAtCaret(editor, parsed, false)
                    styleManager.reformatText(file, offset, caret)
                }
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabledAndVisible = project != null && editor != null
    }
}