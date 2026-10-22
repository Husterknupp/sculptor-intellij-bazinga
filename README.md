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
./gradlew build
```

Runs the lexer unit tests and produces an installable plugin zip under
`build/distributions/`.

## Trying it out

```
./gradlew runIde
```

`runIde` is provided by the Gradle IntelliJ Plugin itself (see
`build.gradle.kts`'s `plugins {}` block) — there's no explicit `runIde { }`
task definition in this project to look for. It launches a sandboxed
IntelliJ IDEA Community instance with this plugin installed; first run
downloads the target IDE version and can take a few minutes. Once it's up,
open `samples/shop.btdesign` (via File > Open, or drag it into the window)
to see `Entity`, `Module` and `Application` highlighted as keywords.
