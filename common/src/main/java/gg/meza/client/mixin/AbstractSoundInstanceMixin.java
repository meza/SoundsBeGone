package gg.meza.client.mixin;

import gg.meza.SoundsBeGone;
import gg.meza.client.SoundsBeGoneClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSoundInstance.class)
public class AbstractSoundInstanceMixin {

    @Final
    @Shadow
    protected Identifier id;

    @Inject(
            method = "getVolume()F",
            at = @At("HEAD"),
            cancellable = true)
    private void getVolume(CallbackInfoReturnable<Float> cir) {

        if (SoundsBeGoneClient.config.isSoundDisabled(id.toString())) {
            SoundsBeGone.LOGGER.debug("Disabling the sound: {}", id);
            SoundsBeGoneClient.analytics.blockedSound(id.toString());
            cir.setReturnValue(0.0F);
            cir.cancel();
        }

        if(MinecraftClient.getInstance().world != null) {
            SoundsBeGone.LOGGER.debug("Intercepting the sound: {}", Text.translatable(id.toTranslationKey()));
            SoundsBeGoneClient.SoundMap.put(id.toString(), new java.util.Date());
        }

    }
}