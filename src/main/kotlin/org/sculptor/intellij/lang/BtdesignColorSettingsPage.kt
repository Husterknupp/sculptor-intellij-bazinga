package org.sculptor.intellij.lang

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

/**
 * Registers the plugin's highlighting keys under Settings > Editor > Color
 * Scheme > Sculptor DSL. Without a page, "Jump to Colors and Fonts" finds no
 * descriptor for any key and reports "No text attributes found".
 */
class BtdesignColorSettingsPage : ColorSettingsPage {

    companion object {
        /** One entry per key that BtdesignSyntaxHighlighter can return. */
        val DESCRIPTORS: Array<AttributesDescriptor> = arrayOf(
            AttributesDescriptor("Keyword", BtdesignSyntaxHighlighter.KEYWORD),
            AttributesDescriptor("Class name", BtdesignSyntaxHighlighter.CLASS_NAME),
            AttributesDescriptor("Class reference", BtdesignSyntaxHighlighter.CLASS_REFERENCE),
            AttributesDescriptor("String", BtdesignSyntaxHighlighter.STRING),
            AttributesDescriptor("Number", BtdesignSyntaxHighlighter.NUMBER),
            AttributesDescriptor("Comment", BtdesignSyntaxHighlighter.COMMENT),
        )

        private const val DEMO_TEXT = """// Sculptor DSL sample
Application Shop {
  basePackage=org.example.shop
  Module customer {
    Entity Customer {
      String name length="40"
      - @Order lastOrder
    }
    Entity Order {
      String orderNumber
    }
  }
}
"""
    }

    override fun getDisplayName(): String = "Sculptor DSL"

    override fun getIcon(): Icon? = null

    override fun getHighlighter(): SyntaxHighlighter = BtdesignSyntaxHighlighter()

    override fun getDemoText(): String = DEMO_TEXT

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? = null

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
}
