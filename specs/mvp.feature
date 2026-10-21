@mvp
Feature: Sculptor DSL support in IntelliJ (MVP)

  A developer opens a Sculptor model that is split across several
  .btdesign files and gets errors, navigation and completion across
  file boundaries, using the language server from Sculptor `develop`
  registered in LSP4IJ.

  @server scenarios run in CI against the real language server over stdio.
  @manual scenarios are checked by a human in IntelliJ.

  Out of scope for the MVP, each gets its own feature file later:
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

  @server
  Scenario: A valid multi-file model has no errors
    When I open "shop.btdesign" and "ordering.btdesign"
    Then "shop.btdesign" has no diagnostics
    And "ordering.btdesign" has no diagnostics

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
