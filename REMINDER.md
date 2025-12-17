# 🚀 Code2Prompt Release Checklist & Guide

This file serves as a reminder for the steps required to update, build, test, and publish the plugin.

## 📦 Release Workflow

**IMPORTANT**: This checklist should only be followed *after* all development, changes, and modifications for the new version are complete and thoroughly tested locally.

1.  **Update Version**:
    *   Open `build.gradle.kts` and update `version = "x.y.z"`.

2.  **Update Changelog (`CHANGELOG.md`)**:
    *   Add a new section at the top for the new version.
    *   **Format**: The header **MUST** match the regex config in `build.gradle.kts`.
    *   **Correct Format**: `### 🚀 Code2Prompt – Version x.y.z – What's New?`
    *   Write your release notes below the header.

3.  **Update Readme (`README.md`)**:
    *   Update the plugin description if necessary.
    *   Ensure content intended for the plugin description is between `<!-- Plugin description -->` and `<!-- Plugin description end -->` markers.

4.  **Verify & Test**:
    *   Run `./gradlew clean buildPlugin` to build the artifact.
    *   Run `./gradlew runIde` to test the plugin in a sandboxed IDE.
    *   Check `build/resources/main/META-INF/plugin.xml` to ensure description and change notes were populated correctly.

5.  **Publish to Marketplace**:
    *   The build artifact is located at: `build/distributions/code2prompt-x.y.z.zip`.
    *   **Manual Upload**: Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/26440-code2prompt) -> Manage -> Upload Update -> Upload the `.zip` file.
    *   **Automated Publish** (Requires Config): Run `./gradlew publishPlugin` (Ensure `PUBLISH_TOKEN`, `CERTIFICATE_CHAIN`, etc., are set in environment variables).

---

## 🛠️ Common Commands

| Task | Command | Description |
| :--- | :--- | :--- |
| **Clean Build** | `./gradlew clean` | Cleans previous build artifacts. |
| **Build Plugin** | `./gradlew buildPlugin` | Compiles and packages the plugin `.zip`. |
| **Run Sandbox** | `./gradlew runIde` | Launches a test instance of IntelliJ with the plugin installed. |
| **Verify Plugin**| `./gradlew verifyPlugin` | Checks compatibility and valid structure. |

---

## ☕ JDK Configuration (Troubleshooting)

If you encounter `java.lang.UnsupportedClassVersionError` or need to force a specific JDK:

**Method 1: Project-Specific (Recommended)**
Add to `gradle.properties`:
```properties
org.gradle.java.home=C:/Path/To/Your/JDK-21
```

**Method 2: Command-Line Override**
```powershell
./gradlew clean -Dorg.gradle.java.home="C:/Path/To/Your/JDK-21"
```
