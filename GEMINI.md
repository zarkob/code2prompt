# Code2Prompt - IntelliJ Plugin Context

## Project Overview
**Code2Prompt** is a JetBrains IntelliJ Platform plugin designed to bridge the gap between IDEs and Large Language Models (LLMs). It allows developers to seamlessly copy project files, content, and error messages into the clipboard, formatted specifically for LLM prompts.

### Key Features
*   **Files to Prompt**: Copies file paths and contents from the Project View.
*   **Error to Prompt**: Extracts error messages, line numbers, and file context from the Editor.
*   **Error to Prompt - No Code**: Extracts error details without the full file content.
*   **.topromptignore**: Supports a custom ignore file (similar to `.gitignore`) to exclude specific files or directories from being copied.

## Technical Architecture
*   **Language**: Kotlin (Target JVM 21)
*   **Build System**: Gradle (Kotlin DSL)
*   **Framework**: IntelliJ Platform SDK (using `org.jetbrains.intellij.platform` version 2.x)
*   **Target Platform**: IntelliJ IDEA Community (`IC`), Version `2025.1`

### Key Files & Locations
*   `build.gradle.kts`: Main build configuration.
*   `src/main/resources/META-INF/plugin.xml`: Plugin descriptor (actions, extensions, metadata).
*   `src/main/kotlin/com/code2prompt/`: Source code package.
    *   `FilesToPromptAction.kt`: Logic for copying files from Project View.
    *   `ErrorToPromptAction.kt`: Logic for capturing errors from the Editor.
    *   `CreateToPromptIgnoreAction.kt`: Logic for creating the ignore file.

## Development & Usage

### Building and Running
The project uses the standard Gradle wrapper.

*   **Run IDE**: Launches a sandboxed instance of IntelliJ with the plugin installed.
    ```bash
    ./gradlew runIde
    ```
*   **Build Plugin**: Compiles and packages the plugin.
    ```bash
    ./gradlew build
    ```
*   **Verify Plugin**: Runs compatibility and verification checks.
    ```bash
    ./gradlew verifyPlugin
    ```

### Configuration
*   **Platform Version**: Defined in `gradle.properties` (`platformVersion = 2025.1`).
*   **Dependencies**: Managed in `build.gradle.kts`.

### Contribution Guidelines
*   **Conventions**: Follow standard Kotlin and IntelliJ Platform SDK conventions.
*   **UI Threading**: Actions interacting with the UI or indexes should respect IntelliJ's threading rules (e.g., using `ActionUpdateThread.BGT`).
*   **Testing**: Ensure changes are tested by running the plugin in the sandboxed IDE (`runIde`).

## User Workflow
1.  **Select Files**: Right-click in Project View -> "Files to Prompt".
2.  **Capture Errors**: Right-click on an error in Editor -> "Error to Prompt".
3.  **Ignore Files**: Create/Edit `.topromptignore` to filter output.
