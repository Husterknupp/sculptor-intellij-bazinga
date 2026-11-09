package org.sculptor.intellij.lang

import com.intellij.psi.TokenType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class BtdesignLexerTest {

    private fun tokenize(text: String): List<Pair<String, String>> {
        val lexer = BtdesignLexer()
        lexer.start(text, 0, text.length, 0)
        val tokens = mutableListOf<Pair<String, String>>()
        while (lexer.tokenType != null) {
            val tokenText = text.substring(lexer.tokenStart, lexer.tokenEnd)
            tokens += tokenText to lexer.tokenType!!.toString()
            lexer.advance()
        }
        return tokens
    }

    private fun tokenTypeOf(text: String, word: String): String =
        tokenize(text).single { it.first == word }.second

    @Test
    fun `Entity, Module and Application are tokenized as keywords`() {
        val model = """
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
        """.trimIndent()

        assertEquals(BtdesignTokenTypes.KEYWORD.toString(), tokenTypeOf(model, "Entity"))
        assertEquals(BtdesignTokenTypes.KEYWORD.toString(), tokenTypeOf(model, "Module"))
        assertEquals(BtdesignTokenTypes.KEYWORD.toString(), tokenTypeOf(model, "Application"))
    }

    private val shop = """
        Application Shop {
          basePackage=org.example.shop
          Module customer {
            Entity Customer {
              String name
              - @Order lastOrder
            }
            Entity Order {
              String orderNumber
            }
          }
        }
    """.trimIndent()

    private fun typeAt(text: String, offset: Int): String {
        val lexer = BtdesignLexer()
        lexer.start(text, 0, text.length, 0)
        while (lexer.tokenType != null && lexer.tokenEnd <= offset) lexer.advance()
        return lexer.tokenType.toString()
    }

    @Test
    fun `Customer and Order are not tokenized as keywords`() {
        val types = tokenize(shop).filter { it.first == "Customer" || it.first == "Order" }.map { it.second }

        assertEquals(3, types.size)
        types.forEach { assertNotEquals(BtdesignTokenTypes.KEYWORD.toString(), it) }
    }

    @Test
    fun `the name after a class declaration keyword is a class name`() {
        val tokens = tokenize(shop)

        assertEquals(BtdesignTokenTypes.CLASS_NAME.toString(), tokens.single { it.first == "Customer" }.second)
        assertEquals(
            BtdesignTokenTypes.CLASS_NAME.toString(),
            typeAt(shop, shop.indexOf("Entity Order") + "Entity ".length),
        )
    }

    @Test
    fun `the name after an at sign is a class reference`() {
        assertEquals(
            BtdesignTokenTypes.CLASS_REFERENCE.toString(),
            typeAt(shop, shop.indexOf("@Order") + 1),
        )
    }

    @Test
    fun `property and module names are neither class names nor class references`() {
        val tokens = tokenize(shop)

        for (word in listOf("name", "lastOrder", "orderNumber", "customer", "Shop", "basePackage", "String")) {
            assertEquals(BtdesignTokenTypes.IDENTIFIER.toString(), tokens.first { it.first == word }.second, word)
        }
    }

    @Test
    fun `a reference to an entity that is declared nowhere is colored like any other`() {
        val model = "Entity Customer {\n  - @Missing broken\n}"

        assertEquals(BtdesignTokenTypes.CLASS_REFERENCE.toString(), typeAt(model, model.indexOf("Missing")))
    }

    @Test
    fun `other declaration keywords also introduce class names`() {
        for (keyword in listOf("ValueObject", "BasicType", "Enum", "DomainEvent", "Service", "Repository", "Resource", "Exception")) {
            val model = "$keyword Foo"
            assertEquals(BtdesignTokenTypes.CLASS_NAME.toString(), typeAt(model, model.indexOf("Foo")), keyword)
        }
    }

    @Test
    fun `a declaration keyword on an earlier line does not color the next line`() {
        val model = "Entity\nFoo"

        assertEquals(BtdesignTokenTypes.IDENTIFIER.toString(), typeAt(model, model.indexOf("Foo")))
    }

    @Test
    fun `a comment or string mentioning a class construct stays a comment or string`() {
        val model = "// Entity Foo @Bar\nString s = \"Entity Baz @Qux\""

        assertEquals(BtdesignTokenTypes.COMMENT.toString(), typeAt(model, model.indexOf("Foo")))
        assertEquals(BtdesignTokenTypes.STRING.toString(), typeAt(model, model.indexOf("Baz")))
    }

    @Test
    fun `restarting the lexer in the middle of a line gives the same class tokens`() {
        val start = shop.indexOf("Customer")
        val lexer = BtdesignLexer()
        lexer.start(shop, start, shop.length, 0)

        assertEquals(BtdesignTokenTypes.CLASS_NAME.toString(), lexer.tokenType.toString())
    }

    @Test
    fun `whitespace, strings and line comments are tokenized separately from identifiers`() {
        val model = "// a comment\nEntity Foo { String name = \"bar\" }"
        val tokens = tokenize(model)

        assertEquals(TokenType.WHITE_SPACE.toString(), tokens.first { it.first == " " }.second)
        assertEquals(BtdesignTokenTypes.COMMENT.toString(), tokens.first { it.first == "// a comment" }.second)
        assertEquals(BtdesignTokenTypes.STRING.toString(), tokens.first { it.first == "\"bar\"" }.second)
    }
}
