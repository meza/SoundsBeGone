package gg.meza.soundsbegone.client.mixin;

import gg.meza.soundsbegone.SoundsBeGoneConfig;
import gg.meza.soundsbegone.client.SoundsBeGoneClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static gg.meza.soundsbegone.client.SoundsBeGoneClient.soundEmissionRegulator;

@Mixin(AbstractSoundInstance.class)
public class AbstractSoundInstanceMixin {

    @Shadow protected float volume;
    @Unique
    private final RandomSource random = RandomSource.create();

    @Inject(
            method = "getVolume()F",
            at = @At("HEAD"),
            cancellable = true)
    private void getVolume(CallbackInfoReturnable<Float> cir) {
        String soundId = soundsBeGone$sound().toString();

        if (SoundsBeGoneClient.config.isSoundInfrequent(soundId)) {
            double value = random.nextDouble() * 100;
            double weightedChance = soundEmissionRegulator.weightedChance(soundId, SoundsBeGoneClient.config.getFrequencyPercentage());
            boolean playSound = value < weightedChance;

            if (!playSound) {
                SoundsBeGoneConfig.LOGGER.debug("Reducing the sound: {}", soundId);
                SoundsBeGoneClient.telemetry.blockedSound(soundId);
                cir.setReturnValue(0.0F);
                cir.cancel();
                track(soundId);
                return;
            }
        }

        int volumePercent = SoundsBeGoneClient.config.getSoundVolume(soundId);
        if (volumePercent == 0) {
            cir.setReturnValue(0.0F);
            cir.cancel();
            track(soundId);
            return;
        } else if (volumePercent != 100) {
            float newVolume = this.volume * (volumePercent / 100.0f);
            cir.setReturnValue(newVolume);
            cir.cancel();
        }

        track(soundId);
    }

    @Unique
    private void track(String soundId) {
        if (Minecraft.getInstance().level != null) {
            SoundsBeGoneClient.SoundMap.put(soundId, new java.util.Date());
        }
    }

    @Unique
    private Identifier soundsBeGone$sound() {
        //? >= 1.21.11 {
        return ((AbstractSoundInstance) (Object) this).getIdentifier();
         //?} else {
        /*return ((AbstractSoundInstance) (Object) this).getLocation();
        *///?}
    }
}
