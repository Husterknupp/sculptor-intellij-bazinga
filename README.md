# sculptor-intellij-bazinga
IDE Support For Sculptor DSL

## Status

Syntax highlighting for `.btdesign` files. What the plugin is supposed to do
is specified in [`specs/`](specs/), one feature file per milestone; the specs
are the source of truth. No language server integration yet.

## Building

```
./gradlew buildPlugin
```

Produces the installable plugin zip under `build/distributions/`. The lexer
unit tests run with `./gradlew test` (and as part of `./gradlew build`).

## Trying it out

```
./gradlew runIde
```

`runIde` is provided by the IntelliJ Platform Gradle Plugin
(`org.jetbrains.intellij.platform`, see `build.gradle.kts`'s `plugins {}`
block) — there's no explicit `runIde { }` task definition in this project to
look for. It launches a sandboxed IntelliJ IDEA instance (the `platformVersion`
from `gradle.properties`) with this plugin installed; first run
downloads that IDE and can take a few minutes. Once it's up,
open `samples/shop.btdesign` (via File > Open, or drag it into the window)
and compare it with `specs/highlighting.feature`. The files in
`samples/cross-file/` belong to the cross-file milestone
(`specs/cross-file-navigation.feature`).

## Prebuilt plugin zip

Every pull request push runs `.github/workflows/build-plugin.yml`, which runs
the tests and uploads the installable zip as a workflow artifact. Open the PR's
Checks tab > "Build plugin" > Summary and use the download link there; install
it via Settings > Plugins > gear icon > Install Plugin from Disk. Artifacts are
kept for 30 days and are replaced by a new one for each new commit.

Each CI build identifies itself in the IDE: the plugin version reads
`0.1.0-pr<number>-<commit>`, and Settings > Plugins > Sculptor DSL > "What's
New" shows the PR title and number, the commit and the build time. Local
builds keep the plain version.
