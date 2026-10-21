@mvp
Feature: Sculptor DSL syntax highlighting in IntelliJ (MVP)

  A developer opens a Sculptor model split across several .btdesign
  files in IntelliJ, with the language server from Sculptor `develop`
  registered in LSP4IJ as a user-defined language server:
  https://github.com/redhat-developer/lsp4ij/blob/main/docs/UserDefinedLanguageServer.md

  The server advertises `semanticTokensProvider` but currently returns
  zero tokens, so keyword highlighting has to come from a client-side
  source (TextMate grammar or a custom highlighting annotator), not
  from the language server itself. Starting the server is still
  required so the next milestone (see cross-file-navigation.feature)
  has something to talk to; its status is visible in LSP4IJ's "LSP
  Consoles" view: https://github.com/redhat-developer/lsp4ij/blob/main/docs/UserGuide.md

  These scenarios are checked by a human in IntelliJ.

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

  @manual
  Scenario: The server is active for .btdesign files in IntelliJ
    Given the server is registered in LSP4IJ as a user-defined language server for "*.btdesign"
    When I open "shop.btdesign" in IntelliJ
    Then the LSP4IJ console shows the server as started

  @manual
  Scenario: Keywords are highlighted in IntelliJ
    When I open "shop.btdesign" in IntelliJ
    Then "Entity", "Module" and "Application" are colored as keywords
    And "Customer" and "Order" are not
