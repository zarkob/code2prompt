package com.code2prompt

import com.code2prompt.ErrorToPromptUtil.formatOutput
import com.code2prompt.ErrorToPromptUtil.getErrorDescriptionAtOffset
import com.code2prompt.ErrorToPromptUtil.getLineText
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.ide.CopyPasteManager
import java.awt.datatransfer.StringSelection

class ErrorToPromptNoCodeAction : AnAction() {

    companion object {
        private val NOTIFICATION_GROUP = NotificationGroupManager.getInstance()
            .getNotificationGroup("ErrorToPromptGroup")
    }

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) as? EditorEx ?: return
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return

        val caretOffset = editor.caretModel.offset
        val errorDescription = getErrorDescriptionAtOffset(editor, caretOffset) ?: return // Exit if no error

        val lineNumber = editor.document.getLineNumber(caretOffset) + 1
        val errorLine = getLineText(editor, lineNumber)
        if (errorDescription.isEmpty()) return
        val filePath = psiFile.virtualFile?.path ?: "N/A"
        val fileType = psiFile.virtualFile?.fileType?.name.toString()


        val formattedOutput = formatOutput(errorDescription, lineNumber, errorLine, "", filePath, fileType)

        CopyPasteManager.getInstance().setContents(StringSelection(formattedOutput))
        // Notify the user
        NOTIFICATION_GROUP.createNotification(
            "Error information copied to clipboard.",
            NotificationType.INFORMATION
        ).notify(e.project)
    }



    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) as? EditorEx
        val caretOffset = editor?.caretModel?.offset ?: -1
        val errorDescription = editor?.let { getErrorDescriptionAtOffset(it, caretOffset) }
        e.presentation.isEnabled = errorDescription != null
    }
}
