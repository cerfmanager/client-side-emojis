package cerf.clientsideemojis.client

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.slf4j.LoggerFactory
import java.io.InputStreamReader
import kotlin.collections.iterator

object EmojiRegistry {

    private val logger = LoggerFactory.getLogger("client-side-emojis")
    private val triggerToGlyph: Map<String, String> = loadRegistry()

    private fun loadRegistry(): Map<String, String> {
        val stream = EmojiRegistry::class.java.getResourceAsStream("/assets/client-side-emojis/emoji_registry.json")
        if (stream == null) {
            logger.warn("emoji_registry.json not found on classpath — no emojis loaded")
            return emptyMap()
        }
        return stream.use { input ->
            InputStreamReader(input, Charsets.UTF_8).use { reader ->
                val type = object : TypeToken<Map<String, Int>>() {}.type
                val raw: Map<String, Int> = Gson().fromJson(reader, type)
                raw.mapValues { (_, codepoint) -> String(Character.toChars(codepoint)) }
            }
        }
    }

    @JvmStatic
    fun applyReplacements(text: String?): String? {
        if (text == null) return null
        var result = text
        for ((trigger, glyph) in triggerToGlyph) {
            result = result?.replace(trigger, glyph)
        }
        return result
    }
}