package cerf.clientsideemojis.client.mixin;

import cerf.clientsideemojis.client.EmojiRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.SignText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import java.util.List;
import java.util.Map;

@Mixin(AbstractSignRenderer.class)
public abstract class EmojiSignRenderer {

    @Shadow
    @Final
    private Font font;

    @Unique
    private static final Map<String, String> clientSideEmojis$map = EmojiRegistry.getTriggerToGlyph();

    @Inject(method = "submitSignText", at = @At("HEAD"), cancellable = true)
    private void clientSideEmojis$replaceSubmitSignText(
        final SignRenderState state,
        final PoseStack poseStack,
        final SubmitNodeCollector submitNodeCollector,
        final SignText signText,
        CallbackInfo ci
    ) {
        FormattedCharSequence[] formattedLines = signText.getRenderMessages(state.isTextFilteringEnabled, input -> {
            List<FormattedCharSequence> components = this.font.split(input, state.maxTextLineWidth);
            return components.isEmpty() ? FormattedCharSequence.EMPTY : components.get(0);
        });
        Component[] messages = signText.getMessages(false);

        int signMidpoint = 4 * state.textLineHeight / 2;
        int lightVal;
        int textColor;

        for (int i = 0; i < 4; i++) {
            int darkColor = 0;
            boolean drawOutline = false;
            FormattedCharSequence actualLine = formattedLines[i];

            if (signText.hasGlowingText()) {
                textColor = signText.getColor().getTextColor();
                drawOutline = textColor == DyeColor.BLACK.getTextColor() || state.drawOutline;
                lightVal = 15728880;
            } else {
                lightVal = state.lightCoords;
                int color = signText.getColor().getTextColor();
                darkColor = ARGB.scaleRGB(color, 0.4F);
                textColor = darkColor;
            }

            String lineText = messages[i].getString();
            if (!lineText.isEmpty() && clientSideEmojis$map.containsValue(lineText)) {
                textColor = DyeColor.WHITE.getTextColor();
            }

            float x1 = (float) -this.font.width(actualLine) / 2;
            submitNodeCollector.submitText(
                poseStack,
                x1,
                i * state.textLineHeight - signMidpoint,
                actualLine,
                false,
                Font.DisplayMode.POLYGON_OFFSET,
                lightVal,
                textColor,
                0,
                drawOutline ? darkColor : 0
            );
        }

        ci.cancel();
    }
}