package cerf.clientsideemojis.client

import cerf.clientsideemojis.client.EmojiRegistry
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents
import net.minecraft.client.Minecraft

object ClientSideEmojisClient : ClientModInitializer {

    override fun onInitializeClient() {
        ClientSendMessageEvents.ALLOW_CHAT.register(ClientSendMessageEvents.AllowChat { message ->
            val replaced = EmojiRegistry.applyReplacements(message)
            if (replaced != message) {
                Minecraft.getInstance().connection?.sendChat(replaced ?: message)
                false
            } else {
                true
            }
        })



        ClientReceiveMessageEvents.CHAT.register{ message, _, sender, _, timestamp ->

            val senderName = sender?.name() ?: "Unknown"
            val textContent = message.string.substringAfter(" ")

            println("(${timestamp.toString()}) [CHAT] $senderName: $textContent")

        }
    }
}