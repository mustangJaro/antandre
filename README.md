# antandre
IntelliJ Plugin to sort Clojure required namespaces

### Build
NOTE: This is built using [gradle-intellij-plugin](https://github.com/JetBrains/gradle-intellij-plugin)
- Run Gradle `buildPlugin` task

### Test
- Run Gradle `runIde` task
- Open appropriate files in new IDE
- Right-click and select option to sort namespaces

### Deploy
- Update the `build.gradle` version
- Add change notes
- Run the `buildPlugin` task
- Find `jar` and upload to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/13222-clojure-namespace-sort)
