
tasks.register("debugChangelog") {
    doLast {
        println("Project Version: ${project.version}")
        println("Changelog File: ${changelog.path.get()}")
        println("Available Sections:")
        val items = changelog.getAll()
        items.keys.forEach { println(" - '$it'") }
        
        val version = project.version.toString()
        val item = changelog.getOrNull(version)
        if (item == null) {
            println("❌ Could not find changelog item for version '$version'")
        } else {
             println("✅ Found item for version '$version':")
             println(changelog.renderItem(item, org.jetbrains.changelog.Changelog.OutputType.PLAIN_TEXT))
        }
    }
}
