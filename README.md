# Code2Prompt 📋🚀

**Code2Prompt** is a JetBrains IntelliJ plugin that allows developers to copy the context of their projects including full file paths, contents, and error messages from IntelliJ-based IDEs to the clipboard in a format optimized for Large Language Models (LLMs).

## 📥 Installation

### Install from JetBrains Marketplace (Recommended)
1. Open **IntelliJ IDEA** (or another JetBrains IDE).
2. Go to **File → Settings → Plugins**.
3. Search for **Code2Prompt** and install it.
4. Restart your IDE.

### Manual Installation
1. Download the latest **Code2Prompt.jar** from [Releases](https://github.com/zarkob/code2prompt/releases).
2. Open IntelliJ IDEA.
3. Navigate to **File → Settings → Plugins → Install Plugin from Disk**.
4. Select the downloaded `.jar` file.
5. Restart your IDE.

## 🎯 Features
🔹 **Seamless File & Content Copying**: Copy full file paths and contents efficiently.\
🔹 **Error Message Extraction**: Extract errors, including file paths and line numbers.\
🔹 **Right-Click Actions**: Available in the **Project View** and **Editor** context menu.\
🔹 **Designed for LLMs**: Optimized output format for AI-based workflows.

📂 **Project View Actions**:
- ✅ Right-click a file/folder → **"Files to Prompt"** to copy its contents.
- ✅ Right-click in the **Project View** → **"Create .topromptignore"** to exclude unwanted files.

📝 **Editor Actions**:
- ✅ Right-click code, underlined by code inspection, in the **Editor View** → **"Error to Prompt"**.
  - The error message, line number, file path and the complete file content will be copied.
- ✅ Right-click code, underlined by code inspection, in the **Editor View** → **"Error to Prompt - No Code"**.
	- The error message, line number and file path will be copied.


## 🚀 How to Use

### Copy Files & Contents
1. Right-click any file or folder in the **Project View**.
2. Select **"Files to Prompt"** from the context menu.
3. The contents will be copied to the clipboard.

### Copy Errors from the Editor
1. Right-click on an error in the **Editor View**.
2. Select **"Copy Error to Prompt"**.
3. The error message, file path, and line number will be copied.

### Create `.topromptignore`
1. Right-click in the **Project View**.
2. Select **"Create .topromptignore"**.
3. A `.topromptignore` file will be created in the project root (or next to `.gitignore` if present).
4. By default, the content of the .gitignore will be copied and the extensions for binary and image files will be added.
5. Modify `.topromptignore` to add the additional exclusions (e.g., `*.svg`, `*.png`, `*.class`).

## 🔧 Configuration
### `.topromptignore` File Format
This file works similarly to `.gitignore`. Example:
```
# Ignore all images
*.jpg
*.png
*.svg

# Ignore build folders
build/
out/

# Ignore IntelliJ config files
.idea/
```

## 🔗 Links
- 📌 **JetBrains Plugin Marketplace**: [Marketplace Link](https://plugins.jetbrains.com/)
- 🛠 **Source Code**: [GitHub Repository](https://github.com/zarkob/code2prompt)
- 🚀 **Releases**: [Download Latest](https://github.com/zarkob/code2prompt/releases)
- ❓ **Report Issues**: [GitHub Issues](https://github.com/zarkob/code2prompt/issues)

## 📜 Credits
**Inspired by:** [files-to-prompt](https://github.com/simonw/files-to-prompt) 🔗

**Supported by:** [SFEIR](https://sfeir.com) 🔗

## 🤝 Contributing
1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Commit your changes (`git commit -m "Added a cool feature"`).
4. Push the branch (`git push origin feature-branch`).
5. Open a Pull Request.

## 📄 License
This project is licensed under the **MIT License** - see the [LICENSE](https://github.com/zarkob/code2prompt/tree/master?tab=MIT-1-ov-file) file for details.

🚀 Happy coding with **Code2Prompt**! 🎯
