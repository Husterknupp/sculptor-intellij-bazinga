package org.sculptor.intellij.lang

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

/**
 * Hand-rolled lexer, not JFlex-generated: the token set is small (keyword
 * vs. identifier vs. string/number/comment vs. everything else) and this
 * avoids an extra build-time code generation step for now. Only used for
 * highlighting, not for PSI/parsing, so it tokenizes on a best-effort basis
 * and never fails on unexpected input — unknown characters just become
 * single-character OTHER tokens.
 */
class BtdesignLexer : LexerBase() {

    private lateinit var buffer: CharSequence
    private var bufferEnd: Int = 0
    private var position: Int = 0
    private var tokenStart: Int = 0
    private var tokenEnd: Int = 0
    private var currentTokenType: IElementType? = null

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.bufferEnd = endOffset
        this.position = startOffset
        scanNextToken()
    }

    override fun getState(): Int = 0

    override fun getTokenType(): IElementType? = currentTokenType

    override fun getTokenStart(): Int = tokenStart

    override fun getTokenEnd(): Int = tokenEnd

    override fun advance() {
        position = tokenEnd
        scanNextToken()
    }

    override fun getBufferSequence(): CharSequence = buffer

    override fun getBufferEnd(): Int = bufferEnd

    private fun scanNextToken() {
        tokenStart = position
        if (position >= bufferEnd) {
            currentTokenType = null
            tokenEnd = position
            return
        }

        val c = buffer[position]
        currentTokenType = when {
            c.isWhitespace() -> {
                while (position < bufferEnd && buffer[position].isWhitespace()) position++
                TokenType.WHITE_SPACE
            }
            c == '/' && position + 1 < bufferEnd && buffer[position + 1] == '/' -> {
                while (position < bufferEnd && buffer[position] != '\n') position++
                BtdesignTokenTypes.COMMENT
            }
            c == '"' -> {
                position++
                while (position < bufferEnd && buffer[position] != '"') {
                    if (buffer[position] == '\\' && position + 1 < bufferEnd) position++
                    position++
                }
                if (position < bufferEnd) position++
                BtdesignTokenTypes.STRING
            }
            c.isDigit() -> {
                while (position < bufferEnd && buffer[position].isDigit()) position++
                BtdesignTokenTypes.NUMBER
            }
            c.isLetter() || c == '_' -> {
                while (position < bufferEnd && (buffer[position].isLetterOrDigit() || buffer[position] == '_')) {
                    position++
                }
                val text = buffer.subSequence(tokenStart, position).toString()
                if (text in BtdesignTokenTypes.KEYWORDS) BtdesignTokenTypes.KEYWORD else BtdesignTokenTypes.IDENTIFIER
            }
            else -> {
                position++
                BtdesignTokenTypes.OTHER
            }
        }
        tokenEnd = position
    }
}
