# sculptor-intellij-bazinga
IDE Support For Sculptor DSL

## Supported IDE features

Status of the language server from Sculptor `develop` @ `4d390ca`, measured
on 2026-10-21 over stdio with a two-file workspace. Not yet verified in
IntelliJ.

✅ works · ⚠️ works with limits · ❌ missing or broken · — not tested

| Feature | IntelliJ action | LSP request | Server today | Spec |
|---|---|---|---|---|
| Syntax highlighting | — | `semanticTokens/full` | ❌ advertised, returns zero tokens; needs a client-side grammar | [highlighting](specs/highlighting.feature), #2 |
| Errors while typing | — | `publishDiagnostics` | ⚠️ cross-file only with relative imports¹ | [cross-file-navigation](specs/cross-file-navigation.feature) |
| Go to definition | Go to Declaration | `definition` | ⚠️ cross-file only with relative imports¹ | [cross-file-navigation](specs/cross-file-navigation.feature) |
| Completion | Basic Completion | `completion` | ⚠️ entities and keywords; other files only with relative imports¹ | [cross-file-navigation](specs/cross-file-navigation.feature) |
| Find usages | Find Usages | `references` | ⚠️ cross-file only with relative imports¹ | — |
| Rename | Rename | `rename` | ⚠️ works across files once references resolve, otherwise "Refactoring cannot be performed" | — |
| Structure view | File Structure | `documentSymbol` | ✅ | — |
| Symbol search | Go to Symbol | `workspace/symbol` | ✅ across all files | — |
| Highlight usages in file | Highlight Usages in File | `documentHighlight` | ✅ | — |
| Code formatting | Reformat Code | `formatting`, `rangeFormatting` | ✅ returns edits; output quality not reviewed | — |
| Quick documentation | Quick Documentation | `hover` | ❌ empty; model doc strings are not shown | — |
| Code folding | — | `foldingRange` | ⚠️ server answers but does not advertise it, so clients probably never ask | — |
| Quick fixes | Show Context Actions | `codeAction` | ❌ not provided | — |
| Signature help | Parameter Info | `signatureHelp` | — advertised | — |

¹ `import "classpath:/…"` is not resolved ("Imported resource could not be
found"), also in a Maven layout under `src/main/resources`. Real Sculptor
projects use this form. A relative `import "ordering.btdesign"` works.
