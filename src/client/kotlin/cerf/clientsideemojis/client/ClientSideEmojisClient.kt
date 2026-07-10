package cerf.clientsideemojis.client


import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents



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



        ClientReceiveMessageEvents.CHAT.register { message, _, sender, _, timestamp ->

            val senderName = sender?.name() ?: "Unknown"
            val textVal = message.string.substringAfter(" ")
            val playerUsername = Minecraft.getInstance().user.name

            if (senderName != playerUsername && textVal.contains("@${playerUsername}")) {
                // check if the sender isnt yourself , and if it contains the string, if yes ping the player
                Minecraft.getInstance().soundManager.play(
                    SimpleSoundInstance.forUI(
                        SoundEvents.ARROW_HIT_PLAYER,
                        0.5F
                        ,2.0F
                    )
                )

            }


        }


        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            dispatcher.register(
                Commands.literal("test_command").executes { context ->
                    context.source.sendSuccess({ Component.literal("Called /test_command.") }, true)
                    1
                }
            )
        }

    }
}