package org.sculptor.intellij.lang

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * "Jump to Colors and Fonts" looks each key up in the registered color
 * settings pages. A key without a descriptor there is invisible to the action.
 */
class BtdesignColorSettingsPageTest {

    private val tokenTypes = listOf(
        BtdesignTokenTypes.KEYWORD,
        BtdesignTokenTypes.IDENTIFIER,
        BtdesignTokenTypes.CLASS_NAME,
        BtdesignTokenTypes.CLASS_REFERENCE,
        BtdesignTokenTypes.STRING,
        BtdesignTokenTypes.NUMBER,
        BtdesignTokenTypes.COMMENT,
        BtdesignTokenTypes.OTHER,
    )

    @Test
    fun `every key the highlighter returns has a descriptor`() {
        val described = BtdesignColorSettingsPage().attributeDescriptors.map { it.key }.toSet()
        val used = tokenTypes.flatMap { BtdesignSyntaxHighlighter().getTokenHighlights(it).toList() }.toSet()
        assertTrue(used.isNotEmpty())
        assertEquals(emptySet<Any>(), used - described, "keys without a descriptor")
    }

    @Test
    fun `plugin xml registers the settings page`() {
        val pluginXml = javaClass.classLoader.getResourceAsStream("META-INF/plugin.xml")!!
            .use { it.readBytes().decodeToString() }
        assertTrue(
            Regex("""colorSettingsPage[^>]*implementation="${BtdesignColorSettingsPage::class.java.name}"""")
                .containsMatchIn(pluginXml),
            "colorSettingsPage is not registered in plugin.xml",
        )
    }

    @Test
    fun `demo text produces class names and references`() {
        val lexer = BtdesignLexer()
        val text = BtdesignColorSettingsPage().demoText
        lexer.start(text)
        val seen = mutableSetOf<Any?>()
        while (lexer.tokenType != null) {
            seen += lexer.tokenType
            lexer.advance()
        }
        assertTrue(BtdesignTokenTypes.CLASS_NAME in seen)
        assertTrue(BtdesignTokenTypes.CLASS_REFERENCE in seen)
    }
}
