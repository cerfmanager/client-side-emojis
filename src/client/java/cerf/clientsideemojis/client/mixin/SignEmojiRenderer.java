package cerf.clientsideemojis.client.mixin;

import cerf.clientsideemojis.client.EmojiRegistry;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSignEditScreen.class)
public abstract class SignEmojiRenderer {


    @Shadow
    @Final
    private String[] messages;

    @Inject(method = "removed", at = @At("HEAD"))
    private void clientSideEmojis$replaceTriggersOnClose(CallbackInfo ci) {
        for (int i = 0; i < this.messages.length; i++) {
            if (this.messages[i] != null) {
                this.messages[i] = EmojiRegistry.applyReplacements(this.messages[i]);
            }
        }
    }
}


