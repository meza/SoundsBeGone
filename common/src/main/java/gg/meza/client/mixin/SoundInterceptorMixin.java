package gg.meza.client.mixin;

import gg.meza.SoundsBeGone;
import gg.meza.client.SoundsBeGoneClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(SoundSystem.class)
public class SoundInterceptorMixin {

	@Inject(at = @At("HEAD"), method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", cancellable = true)
	private void run(SoundInstance sound, CallbackInfo info) {
		String id = sound.getId().toString();
		String name = sound.getId().toTranslationKey();
		if (SoundsBeGoneClient.config.isSoundDisabled(id)) {
			SoundsBeGone.LOGGER.debug("Disabling the sound: {}", id);
			SoundsBeGoneClient.analytics.blockedSound(id);
			info.cancel();
		}

		if(MinecraftClient.getInstance().world != null) {
			SoundsBeGone.LOGGER.debug("Intercepting the sound: {}", Text.translatable(name));
			SoundsBeGoneClient.SoundMap.put(id, new java.util.Date());
		}
	}
}
