@mvp
Feature: Sculptor DSL syntax highlighting in IntelliJ (MVP)

  A developer opens a .btdesign file in IntelliJ and can read it
  efficiently because Sculptor keywords are colored.

  Highlighting is client-side, provided by this plugin. The language
  server from Sculptor `develop` advertises `semanticTokensProvider`
  but currently returns zero tokens, so it cannot color anything and
  is not needed for this feature. Starting the server in LSP4IJ is
  part of the next milestone (see cross-file-navigation.feature).

  These scenarios are checked by a human in IntelliJ.

  Background:
    Given the plugin is installed in IntelliJ
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

  @manual
  Scenario: Keywords are highlighted in IntelliJ
    When I open "shop.btdesign" in IntelliJ
    Then "Entity", "Module" and "Application" are colored as keywords
    And "Customer" and "Order" are not
