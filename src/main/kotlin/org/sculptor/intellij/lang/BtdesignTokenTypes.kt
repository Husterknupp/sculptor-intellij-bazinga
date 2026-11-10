package org.sculptor.intellij.lang

import com.intellij.psi.tree.IElementType

object BtdesignTokenTypes {
    val KEYWORD = IElementType("SCULPTOR_KEYWORD", BtdesignLanguage)
    val IDENTIFIER = IElementType("SCULPTOR_IDENTIFIER", BtdesignLanguage)
    val STRING = IElementType("SCULPTOR_STRING", BtdesignLanguage)
    val NUMBER = IElementType("SCULPTOR_NUMBER", BtdesignLanguage)
    val COMMENT = IElementType("SCULPTOR_COMMENT", BtdesignLanguage)
    val OTHER = IElementType("SCULPTOR_OTHER", BtdesignLanguage)

    /**
     * Keywords that introduce a Domain Object or module structure, plus the
     * most common modifiers seen in real Sculptor models (sculptor/sculptor
     * advanced tutorial and the DDDSample project). Not exhaustive by
     * design — this only has to satisfy "readable at a glance", not parse
     * the full grammar.
     */
    val KEYWORDS: Set<String> = setOf(
        // Domain object declarations
        "Application", "ApplicationPart", "Module", "Entity", "ValueObject",
        "BasicType", "Enum", "DomainEvent", "Service", "Repository",
        "Resource", "Exception",
        // Structural / modifier keywords
        "import", "extends", "abstract", "scaffold", "gap", "nogap", "not",
        "key", "nullable", "changeable", "inverse", "cascade", "length",
        "databaseColumn", "fetch", "hint", "optimisticLocking", "auditable",
        "throws", "protected", "public", "private", "static", "final",
    )
}
