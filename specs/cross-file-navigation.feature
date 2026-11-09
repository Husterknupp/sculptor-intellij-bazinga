Feature: Sculptor cross-file model support in IntelliJ

  A developer opens a Sculptor model that is split across several
  .btdesign files and gets errors, navigation and completion across
  file boundaries, using the language server from Sculptor `develop`
  registered in LSP4IJ.

  Next milestone after syntax highlighting (see highlighting.feature).
  Blocked today: `import "classpath:/..."` is not resolved by the
  server ("Imported resource could not be found"), also in a Maven
  layout under src/main/resources, so 3 of the 4 @server scenarios currently
  fail against the real server. A relative `import "ordering.btdesign"`
  already works, including cross-file definition, completion and
  rename. Real Sculptor projects (e.g. DDDSample) use `classpath:`
  imports, so this needs a fix or workaround before these scenarios
  can be claimed as done.

  Scenarios tagged @server run in CI against the real language server
  over stdio: no IntelliJ involved, just plain LSP requests/notifications
  (initialize, textDocument/didOpen, textDocument/definition, ...)
  sent directly to the server process.

  The @manual scenario is checked by a human in IntelliJ, with the
  server registered as a user-defined language server:
  https://github.com/redhat-developer/lsp4ij/blob/main/docs/UserDefinedLanguageServer.md
  Its status is visible in LSP4IJ's Language Servers view (the "LSP
  console", see the comment on that scenario):
  https://github.com/redhat-developer/lsp4ij/blob/main/docs/UserGuide.md#lsp-console

  Out of scope for now, each gets its own feature file later:
  hover documentation, find usages, rename, formatting, quick fixes.

  Background:
    Given the language server built from Sculptor "develop" at commit "4d390ca"
    And a workspace containing "shop.btdesign":
      """
      import "classpath:/ordering.btdesign"

      Application Shop {
        basePackage=org.example.shop
        Module customer {
          Entity Customer {
            String name
            - @Order lastOrder
          }
        }
      }
      """
    And a workspace containing "ordering.btdesign":
      """
      ApplicationPart OrderPart {
        Module ordering {
          Entity Order {
            String orderNumber
          }
        }
      }
      """

  # How to check "started": open LSP4IJ's Language Servers view (the "LSP
  # console") inside IntelliJ. It lists every registered server with its
  # state; a server that has been started for the open file shows as started.
  # Anything the server reports via window/logMessage appears on that
  # server's `Logs` tab, and the right-click menu offers stop and restart.
  # Per-server tracing is configured under Settings | Languages & Frameworks
  # | Language Servers. LSP4IJ's docs do not describe a separate log file for
  # this, so the view above is the place to look.
  # https://github.com/redhat-developer/lsp4ij/blob/main/docs/UserGuide.md#lsp-console
  @manual
  Scenario: The server is active for .btdesign files in IntelliJ
    Given the server is registered in LSP4IJ as a user-defined language server for "*.btdesign"
    When I open "shop.btdesign" in IntelliJ
    Then the LSP4IJ console shows the server as started

  @server
  Scenario: A valid multi-file model has no errors
    When I open "shop.btdesign" and "ordering.btdesign"
    Then "shop.btdesign" has no diagnostics
    And "ordering.btdesign" has no diagnostics

  # "- @Missing broken" is not real Sculptor syntax, it's made up for this
  # scenario. In Sculptor DSL, an "@"-prefixed type marks a reference to
  # another Domain Object (Entity/ValueObject), the same way "@Order" does
  # above in "- @Order lastOrder": https://sculptor.github.io/documentation/advanced-tutorial
  # "Missing" is deliberately never declared anywhere in the workspace, so
  # the server has something unresolvable to report.
  @server
  Scenario: A broken reference is reported
    Given "ordering.btdesign" has the line "- @Missing broken" added to entity "Order"
    When I open "shop.btdesign" and "ordering.btdesign"
    Then "ordering.btdesign" has exactly one error
    And the error mentions "Missing"

  @server
  Scenario: Go to definition across files
    When I open "shop.btdesign" and "ordering.btdesign"
    And I go to the definition of "Order" in "- @Order lastOrder"
    Then I land in "ordering.btdesign" on the name "Order" in "Entity Order"

  @server
  Scenario: Completion offers entities from other files
    When I open "shop.btdesign" and "ordering.btdesign"
    And I request completion right after "- @" in entity "Customer"
    Then the proposals include "Customer" and "Order"
