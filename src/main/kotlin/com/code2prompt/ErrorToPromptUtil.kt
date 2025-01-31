package com.code2prompt

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType.HighlightInfoTypeSeverityByKey
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import java.nio.charset.StandardCharsets

object ErrorToPromptUtil {

    /**
     * Retrieves the error or warning description at a given caret offset in the editor.
     *
     * This method scans the syntax highlighting markers (highlighters) in the editor
     * and filters them based on predefined severity levels. It then attempts to find
     * the first relevant highlight within the same line as the given offset.
     *
     * **Why Use Both `HighlightSeverity` and `HighlightInfoTypeSeverityByKey`?**
     * - `HighlightSeverity` is a core JetBrains API representing standard severities such as
     *   ERROR, WARNING, and INFO. It provides a **direct** mapping to well-known issue types.
     * - `HighlightInfoTypeSeverityByKey` defines **custom, tool-specific severities** used by
     *   IntelliJ inspections, such as `TODO`, `UNUSED_SYMBOL`, and `DEPRECATED`. These are
     *   dynamically registered and not always present in `HighlightSeverity`.
     * - Some inspections **only register severity via `HighlightInfoTypeSeverityByKey`**, meaning
     *   that filtering with `HighlightSeverity` alone would miss important highlights.
     * - By including both, we ensure **comprehensive coverage** of all relevant issues.
     *
     * @param editor The editor instance where the error/warning might be present.
     * @param offset The caret offset position in the document to check for errors.
     * @return A formatted string containing the highlighted text and its description,
     *         or `null` if no relevant error or warning is found.
     */
    fun getErrorDescriptionAtOffset(editor: EditorEx, offset: Int): String? {
        val highlights = editor.filteredDocumentMarkupModel.allHighlighters
        val severitiesToKeep = setOf(
            HighlightSeverity.ERROR,
            HighlightSeverity.WARNING,
            HighlightSeverity.WEAK_WARNING,
            HighlightSeverity.GENERIC_SERVER_ERROR_OR_WARNING,
            HighlightInfoTypeSeverityByKey.ERROR,
            HighlightInfoTypeSeverityByKey.WARNING,
            HighlightInfoTypeSeverityByKey.WEAK_WARNING,
            HighlightInfoTypeSeverityByKey.GENERIC_WARNINGS_OR_ERRORS_FROM_SERVER,
            HighlightInfoTypeSeverityByKey.DEPRECATED,
            HighlightInfoTypeSeverityByKey.UNUSED_SYMBOL,
            HighlightInfoTypeSeverityByKey.UNUSED_SYMBOL_SHORT_NAME,
            HighlightInfoTypeSeverityByKey.UNHANDLED_EXCEPTION,
            HighlightInfoTypeSeverityByKey.MARKED_FOR_REMOVAL,
            HighlightInfoTypeSeverityByKey.WRONG_REF,
            HighlightInfoTypeSeverityByKey.POSSIBLE_PROBLEM,
            HighlightInfoTypeSeverityByKey.TODO
        )

        // Get the current line's start and end offsets
        val document = editor.document
        val lineNumber = document.getLineNumber(offset)
        val lineStartOffset = document.getLineStartOffset(lineNumber)
        val lineEndOffset = document.getLineEndOffset(lineNumber)

        // Filter highlights within the current line range and matching severities
        val relevantHighlight = highlights.firstOrNull { highlighter ->
            val tooltip = highlighter.errorStripeTooltip as? HighlightInfo
            tooltip != null &&
                    tooltip.severity in severitiesToKeep &&
                    highlighter.startOffset >= lineStartOffset &&
                    highlighter.endOffset <= lineEndOffset
        }

        // Return the text and description for the first relevant highlight on the current line
        return relevantHighlight?.let { highlighter ->
            val tooltip = highlighter.errorStripeTooltip as? HighlightInfo
            if (tooltip != null) {
                "${tooltip.text}: ${tooltip.description ?: "No description"}"
            } else {
                null
            }
        }
    }


    fun getLineText(editor: Editor, lineNumber: Int): String {
        val document = editor.document
        if (lineNumber <= 0 || lineNumber > document.lineCount) return ""
        val lineStartOffset = document.getLineStartOffset(lineNumber - 1)
        val lineEndOffset = document.getLineEndOffset(lineNumber - 1)
        return document.getText(TextRange(lineStartOffset, lineEndOffset)).trim()
    }

    fun getFileContent(psiFile: PsiFile): String {
        val document = PsiDocumentManager.getInstance(psiFile.project).getDocument(psiFile) ?: return ""
        return String(document.text.toByteArray(), StandardCharsets.UTF_8)
    }

    fun formatOutput(
        errorDescription: String,
        lineNumber: Int,
        errorLine: String,
        fileContent: String,
        filePath: String,
        fileType: String
    ): String {
        val fileContentDisplay = if (fileContent.isNotEmpty()) """
            
            OF THE FILE:
            ### File: $filePath
            ```$fileType
                    $fileContent
            ```
        """.trimIndent() else ""
        return """
            -----------------------
            THERE IS AN ERROR:
            $errorDescription

            AT THE LINE $lineNumber:
            $errorLine
            $fileContentDisplay
            -----------------------
        """.trimIndent()
    }

}
