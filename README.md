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

Launches a sandboxed IntelliJ IDEA instance with the plugin installed; open
a `.btdesign` file to see the highlighting.
