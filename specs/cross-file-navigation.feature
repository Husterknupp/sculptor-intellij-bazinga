Feature: Sculptor cross-file model support in IntelliJ

  Not implemented yet. Tracked in
  https://github.com/Husterknupp/sculptor-intellij-bazinga/issues/8

  A developer opens a Sculptor model that is split across several
  .btdesign files and gets errors, navigation and completion across
  file boundaries, using the language server from Sculptor `develop`
  registered in LSP4IJ.

  Scenarios tagged @server run in CI against the real language server
  over stdio: no IntelliJ involved, just plain LSP requests/notifications
  (initialize, textDocument/didOpen, textDocument/definition, ...)
  sent directly to the server process.

  The @manual scenarios are checked by a human in IntelliJ, with the
  server registered as a user-defined language server:
  https://github.com/redhat-developer/lsp4ij/blob/main/docs/UserDefinedLanguageServer.md
  Its status is visible in LSP4IJ's Language Servers view (the "LSP
  console", see the comment on that scenario):
  https://github.com/redhat-developer/lsp4ij/blob/main/docs/UserGuide.md#lsp-console

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

  # Written from the developer's point of view; the same scenario is checked
  # twice.
  # @manual: a human looks at the editor. By default IntelliJ draws errors as
  # a red wavy underline; hover the mark to see the message.
  # @server: automated, no IntelliJ. After textDocument/didOpen for
  # "shop.btdesign" and "ordering.btdesign", the server must publish one error
  # diagnostic on the line "- @Missing broken" whose message mentions
  # "Missing", and none on "- @Order lastOrder". The registration step is
  # needed for the @manual run only.
  # A reference counts as unresolved when its entity is declared neither in
  # the open file nor in a file it imports. "Missing" is deliberately never
  # declared anywhere in the workspace, so there is something unresolvable
  # to report. Which exact characters carry the mark is the server's
  # diagnostic range; the scenario only requires that the unresolved
  # reference is marked.
  @manual @server
  Scenario: An unresolved reference is marked as an error
    Given the server is registered in LSP4IJ as a user-defined language server for "*.btdesign"
    And "shop.btdesign" has the line "- @Missing broken" added to entity "Customer"
    When I open "shop.btdesign"
    Then "@Missing" in "- @Missing broken" is marked as an error, for example with a red wavy underline
    And hovering the mark shows a message that mentions "Missing"
    And "@Order" in "- @Order lastOrder" is not marked as an error

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
