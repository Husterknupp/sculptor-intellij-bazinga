package org.sculptor.intellij.lang

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object BtdesignFileType : LanguageFileType(BtdesignLanguage) {
    override fun getName(): String = "Sculptor DSL"
    override fun getDescription(): String = "Sculptor DSL (.btdesign) file"
    override fun getDefaultExtension(): String = "btdesign"
    override fun getIcon(): Icon? = null
}
