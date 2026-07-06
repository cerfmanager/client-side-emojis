package cerf.clientsideemojis.client.mixin;


import cerf.clientsideemojis.client.EmojiRegistry;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.SignText;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(AbstractSignRenderer.class)
public class EmojiSignRenderer {
    @Unique
    private static Map<String, String> map =  EmojiRegistry.getTriggerToGlyph();

    @Inject(method = "getDarkColor", at = @At("TAIL"), cancellable = true)
    private static void clientSideEmojis$replacerDarkColor(SignText signText, CallbackInfoReturnable<Integer> cir) {
        Component[] messages = signText.getMessages(false);
        for (Component component : messages) {
            if(map.containsValue(component.getString())&& !(component.getString().isEmpty())){
                cir.setReturnValue(DyeColor.WHITE.getTextColor());
            }
        }

    }
}
