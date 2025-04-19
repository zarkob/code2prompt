package com.code2prompt

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import java.io.File

class CreateToPromptIgnoreAction : AnAction() {

    companion object {
        private val NOTIFICATION_GROUP = NotificationGroupManager.getInstance()
            .getNotificationGroup("CreateToPromptIgnoreGroup")
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val baseDir = project.basePath?.let { File(it) } ?: return

        val gitIgnoreFile = File(baseDir, ".gitignore")
        val copyIgnoreFile = File(baseDir, ".topromptignore")

        if (!copyIgnoreFile.exists()) {
            val ignoreContent = StringBuilder()

            // Include content from .gitignore if it exists
            if (gitIgnoreFile.exists()) {
                ignoreContent.append(gitIgnoreFile.readText())
            }

            ignoreContent.append("\n\n### Code2Prompt ###\n")

            // Add common patterns for image formats and binary files
            ignoreContent.append("*.jpg\n*.jpeg\n*.png\n*.gif\n*.bmp\n*.webp\n*.svg\n")
            ignoreContent.append("*.exe\n*.dll\n*.so\n*.bin\n*.class\n*.jar\n*.zip\n*.tar\n*.gz\n")

            copyIgnoreFile.writeText(ignoreContent.toString())

            // Notify the user
            NOTIFICATION_GROUP.createNotification(
                ".topromptignore file created successfully.",
                NotificationType.INFORMATION
            ).notify(project)
        } else {
            NOTIFICATION_GROUP.createNotification(
                ".topromptignore file already exists.",
                NotificationType.INFORMATION
            ).notify(project)
        }
    }
}
