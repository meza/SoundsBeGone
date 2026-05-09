package gg.meza.soundsbegone.client.mixin;

import gg.meza.soundsbegone.SoundsBeGoneConfig;
import gg.meza.soundsbegone.client.SoundsBeGoneClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static gg.meza.soundsbegone.client.SoundsBeGoneClient.soundEmissionRegulator;

@Mixin(AbstractSoundInstance.class)
public class AbstractSoundInstanceMixin {

    private final RandomSource random = RandomSource.create();

    @Inject(
            method = "getVolume()F",
            at = @At("HEAD"),
            cancellable = true)
    private void getVolume(CallbackInfoReturnable<Float> cir) {

        if (SoundsBeGoneClient.config.isSoundDisabled(soundsBeGone$sound().toString())) {
            SoundsBeGoneConfig.LOGGER.debug("Disabling the sound: {}", soundsBeGone$sound());
            SoundsBeGoneClient.telemetry.blockedSound(soundsBeGone$sound().toString());
            cir.setReturnValue(0.0F);
            cir.cancel();
        } else if (SoundsBeGoneClient.config.isSoundInfrequent(soundsBeGone$sound().toString())) {
            double value = random.nextDouble()*100;
            double weightedChance = soundEmissionRegulator.weightedChance(soundsBeGone$sound().toString(), SoundsBeGoneClient.config.getFrequencyPercentage());
            boolean playSound = value < weightedChance;

            if (!playSound) {
                SoundsBeGoneConfig.LOGGER.debug("Reducing the sound: {}", soundsBeGone$sound());
                SoundsBeGoneClient.telemetry.blockedSound(soundsBeGone$sound().toString());
                cir.setReturnValue(0.0F);
                cir.cancel();
            }
        }

        if (Minecraft.getInstance().level != null) {
            SoundsBeGoneClient.SoundMap.put(soundsBeGone$sound().toString(), new java.util.Date());
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
