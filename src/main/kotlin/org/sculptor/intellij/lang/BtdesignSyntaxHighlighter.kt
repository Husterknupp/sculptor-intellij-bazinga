package org.sculptor.intellij.lang

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType

class BtdesignSyntaxHighlighter : SyntaxHighlighterBase() {

    companion object {
        val KEYWORD: TextAttributesKey =
            createTextAttributesKey("SCULPTOR_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        val STRING: TextAttributesKey =
            createTextAttributesKey("SCULPTOR_STRING", DefaultLanguageHighlighterColors.STRING)
        val NUMBER: TextAttributesKey =
            createTextAttributesKey("SCULPTOR_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        val COMMENT: TextAttributesKey =
            createTextAttributesKey("SCULPTOR_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)

        private val KEYWORD_KEYS = arrayOf(KEYWORD)
        private val STRING_KEYS = arrayOf(STRING)
        private val NUMBER_KEYS = arrayOf(NUMBER)
        private val COMMENT_KEYS = arrayOf(COMMENT)
        private val EMPTY_KEYS = emptyArray<TextAttributesKey>()
    }

    override fun getHighlightingLexer(): Lexer = BtdesignLexer()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> = when (tokenType) {
        BtdesignTokenTypes.KEYWORD -> KEYWORD_KEYS
        BtdesignTokenTypes.STRING -> STRING_KEYS
        BtdesignTokenTypes.NUMBER -> NUMBER_KEYS
        BtdesignTokenTypes.COMMENT -> COMMENT_KEYS
        else -> EMPTY_KEYS
    }
}
