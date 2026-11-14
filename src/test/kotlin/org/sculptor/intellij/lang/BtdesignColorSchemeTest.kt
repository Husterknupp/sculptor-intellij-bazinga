package org.sculptor.intellij.lang

import javax.xml.parsers.DocumentBuilderFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.w3c.dom.Element

/**
 * The platform has no default color for DEFAULT_CLASS_NAME, so a plugin key
 * that only falls back to it looks like plain text. These tests pin the
 * plugin's own default colors for the light and dark schemes.
 */
class BtdesignColorSchemeTest {

    private fun foregrounds(resource: String): Map<String, String> {
        val stream = javaClass.classLoader.getResourceAsStream(resource)
        assertNotNull(stream, "$resource is missing from the plugin resources")
        val document = stream!!.use { DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(it) }
        val options = document.documentElement.childNodes
        val result = mutableMapOf<String, String>()
        for (i in 0 until options.length) {
            val option = options.item(i) as? Element ?: continue
            val values = option.getElementsByTagName("option")
            for (j in 0 until values.length) {
                val inner = values.item(j) as Element
                if (inner.getAttribute("name") == "FOREGROUND") {
                    result[option.getAttribute("name")] = inner.getAttribute("value")
                }
            }
        }
        return result
    }

    private val classKeys = listOf(
        BtdesignSyntaxHighlighter.CLASS_NAME.externalName,
        BtdesignSyntaxHighlighter.CLASS_REFERENCE.externalName,
    )

    @Test
    fun `light scheme gives class names and references their own color`() {
        val colors = foregrounds("colorSchemes/SculptorDefault.xml")
        classKeys.forEach { assertTrue(colors.containsKey(it), "no foreground for $it") }
        assertTrue(colors.values.toSet().size == classKeys.size, "class name and reference share a color")
    }

    @Test
    fun `dark scheme gives class names and references their own color`() {
        val colors = foregrounds("colorSchemes/SculptorDarcula.xml")
        classKeys.forEach { assertTrue(colors.containsKey(it), "no foreground for $it") }
        assertTrue(colors.values.toSet().size == classKeys.size, "class name and reference share a color")
    }

    @Test
    fun `plugin xml registers both scheme files`() {
        val pluginXml = javaClass.classLoader.getResourceAsStream("META-INF/plugin.xml")!!
            .use { it.readBytes().decodeToString() }
        assertEquals(
            listOf("colorSchemes/SculptorDarcula.xml", "colorSchemes/SculptorDefault.xml"),
            Regex("""additionalTextAttributes[^>]*file="([^"]+)"""").findAll(pluginXml)
                .map { it.groupValues[1] }.sorted().toList(),
        )
    }
}
