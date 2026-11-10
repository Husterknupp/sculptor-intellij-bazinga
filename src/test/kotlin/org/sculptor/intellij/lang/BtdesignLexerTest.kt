package org.sculptor.intellij.lang

import com.intellij.psi.TokenType
import org.junit.jupiter.api.Assertions.assertEquals
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

    @Test
    fun `Customer and Order are not tokenized as keywords`() {
        val model = """
            Application Shop {
              Module customer {
                Entity Customer {
                  - @Order lastOrder
                }
              }
            }
        """.trimIndent()

        assertEquals(BtdesignTokenTypes.IDENTIFIER.toString(), tokenTypeOf(model, "Customer"))
        assertEquals(BtdesignTokenTypes.IDENTIFIER.toString(), tokenTypeOf(model, "Order"))
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
