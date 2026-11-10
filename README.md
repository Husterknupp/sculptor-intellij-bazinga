# sculptor-intellij-bazinga
IDE Support For Sculptor DSL

## Status

Syntax highlighting for `.btdesign` files (see `specs/highlighting.feature`
on [PR #1](https://github.com/Husterknupp/sculptor-intellij-bazinga/pull/1)
and [#2](https://github.com/Husterknupp/sculptor-intellij-bazinga/issues/2)):
domain object keywords (`Entity`, `Module`, `Application`, ...) are colored
as keywords, identifiers are not. No language server integration yet.

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
to see `Entity`, `Module` and `Application` highlighted as keywords.
