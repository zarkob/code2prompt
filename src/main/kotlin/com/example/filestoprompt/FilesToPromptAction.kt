package com.example

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import java.awt.datatransfer.StringSelection
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

class FilesToPromptAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        // Get the selected files or folders
        val dataContext = e.dataContext
        val selectedFiles = CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext)

        if (selectedFiles.isNullOrEmpty()) {
            return
        }

        // Load ignore patterns from .copyignore file
        val project = e.project ?: return
        val ignorePatterns = loadIgnorePatterns(project)

        // Collect the paths and contents
        val fileDataList = mutableListOf<String>()

        for (file in selectedFiles) {
            collectFileData(file, fileDataList, ignorePatterns)
        }

        // Format the data
        val result = fileDataList.joinToString("\n\n")

        // Copy to clipboard
        CopyPasteManager.getInstance().setContents(StringSelection(result))

        // Notify the user
        Messages.showInfoMessage(project, "Files data copied to clipboard.", "Files to Prompt")
    }

    val MAX_FILE_SIZE: Long = (5 * 1024 * 1024 // 5 MB
            ).toLong()

    private fun loadIgnorePatterns(project: Project): List<Pattern> {
        val copyIgnoreFile = findCopyIgnoreFile(project)
        if (copyIgnoreFile != null && copyIgnoreFile.exists()) {
            return copyIgnoreFile.readLines().mapNotNull { line ->
                if (line.isNotBlank() && !line.startsWith("#")) {
                    val regexPattern = line.trim()
                        .replace(".", "\\.")
                        .replace("*", ".*")
                        .replace("?", ".")
                    Pattern.compile("^$regexPattern\$")
                } else {
                    null
                }
            }
        }
        return emptyList()
    }

    private fun findCopyIgnoreFile(project: Project): File? {
        val baseDir = project.basePath?.let { File(it) } ?: return null
        val gitIgnoreFile = File(baseDir, ".gitignore")
        return if (gitIgnoreFile.exists()) {
            File(baseDir, ".copyignore")
        } else {
            File(baseDir, ".copyignore")
        }
    }

    private fun collectFileData(file: VirtualFile, fileDataList: MutableList<String>, ignorePatterns: List<Pattern>) {
        if (isIgnored(file, ignorePatterns)) {
            return
        }

        if (file.isDirectory) {
            file.children.forEach { child ->
                // Recursively collect data from all children, including META-INF
                collectFileData(child, fileDataList, ignorePatterns)
            }
        } else {
            try {
                if (file.getLength() > MAX_FILE_SIZE) {
                    // Skip large files
                    return
                }
                if (!file.fileType.isBinary) {
                    val path = file.path
                    val content = String(file.contentsToByteArray(), StandardCharsets.UTF_8)
                    val fileData = formatFileData(path, content)
                    fileDataList.add(fileData)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun isIgnored(file: VirtualFile, ignorePatterns: List<Pattern>): Boolean {
        val relativePath = file.path.replace("\\", "/")
        return ignorePatterns.any { it.matcher(relativePath).matches() }
    }

    private fun formatFileData(path: String, content: String): String {
        return "### File: " + path + "\n````````````" + getFileExtension(path) + "\n" + content + "\n````````````\n"
    }

    private fun getFileExtension(path: String): String {
        val lastIndex: Int = path.lastIndexOf('.')
        if (lastIndex != -1 && lastIndex != path.length - 1) {
            return path.substring(lastIndex + 1)
        }
        return ""
    }
}

class CreateCopyIgnoreAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val baseDir = project.basePath?.let { File(it) } ?: return

        val gitIgnoreFile = File(baseDir, ".gitignore")
        val copyIgnoreFile = File(baseDir, ".copyignore")

        if (!copyIgnoreFile.exists()) {
            val ignoreContent = StringBuilder()

            // Include content from .gitignore if it exists
            if (gitIgnoreFile.exists()) {
                ignoreContent.append(gitIgnoreFile.readText()).append("\n")
            }

            // Add common patterns for image formats and binary files
            ignoreContent.append("*.jpg\n*.jpeg\n*.png\n*.gif\n*.bmp\n*.webp\n*.svg\n")
            ignoreContent.append("*.exe\n*.dll\n*.so\n*.bin\n*.class\n*.jar\n*.zip\n*.tar\n*.gz\n")

            copyIgnoreFile.writeText(ignoreContent.toString())

            Messages.showInfoMessage(project, ".copyignore file created successfully.", "Create .copyignore")
        } else {
            Messages.showInfoMessage(project, ".copyignore file already exists.", "Create .copyignore")
        }
    }
}
