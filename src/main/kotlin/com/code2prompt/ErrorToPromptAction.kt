package com.code2prompt

import com.code2prompt.ErrorToPromptUtil.formatOutput
import com.code2prompt.ErrorToPromptUtil.getErrorDescriptionAtOffset
import com.code2prompt.ErrorToPromptUtil.getFileContent
import com.code2prompt.ErrorToPromptUtil.getLineText
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.ide.CopyPasteManager
import java.awt.datatransfer.StringSelection

class ErrorToPromptAction : AnAction() {

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
        val fileContent = getFileContent(psiFile)
        val filePath = psiFile.virtualFile?.path ?: "N/A"
        val fileType = psiFile.virtualFile?.fileType?.name.toString()


        val formattedOutput = formatOutput(errorDescription, lineNumber, errorLine, fileContent, filePath, fileType)

        CopyPasteManager.getInstance().setContents(StringSelection(formattedOutput))
        // Notify the user
        NOTIFICATION_GROUP.createNotification(
            "Error information copied to clipboard.",
            NotificationType.INFORMATION
        ).notify(e.project)
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) as? EditorEx
        val caretOffset = editor?.caretModel?.offset ?: -1
        val errorDescription = editor?.let { getErrorDescriptionAtOffset(it, caretOffset) }
        e.presentation.isEnabled = errorDescription != null
    }
}
