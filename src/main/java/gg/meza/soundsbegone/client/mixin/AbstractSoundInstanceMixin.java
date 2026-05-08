package gg.meza.soundsbegone.client.mixin;

import gg.meza.soundsbegone.SoundsBeGoneConfig;
import gg.meza.soundsbegone.client.SoundsBeGoneClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static gg.meza.soundsbegone.client.SoundsBeGoneClient.soundEmissionRegulator;

@Mixin(AbstractSoundInstance.class)
public class AbstractSoundInstanceMixin {

    private final RandomSource random = RandomSource.create();

    @Final
    @Shadow
    protected Identifier identifier;

    @Inject(
            method = "getVolume()F",
            at = @At("HEAD"),
            cancellable = true)
    private void getVolume(CallbackInfoReturnable<Float> cir) {

        if (SoundsBeGoneClient.config.isSoundDisabled(identifier.toString())) {
            SoundsBeGoneConfig.LOGGER.debug("Disabling the sound: {}", identifier);
            SoundsBeGoneClient.telemetry.blockedSound(identifier.toString());
            cir.setReturnValue(0.0F);
            cir.cancel();
        } else if (SoundsBeGoneClient.config.isSoundInfrequent(identifier.toString())) {
            double value = random.nextDouble()*100;
            double weightedChance = soundEmissionRegulator.weightedChance(identifier.toString(), SoundsBeGoneClient.config.getFrequencyPercentage());
            boolean playSound = value < weightedChance;

            if (!playSound) {
                SoundsBeGoneConfig.LOGGER.debug("Reducing the sound: {}", identifier);
                SoundsBeGoneClient.telemetry.blockedSound(identifier.toString());
                cir.setReturnValue(0.0F);
                cir.cancel();
            }
        }

        if (Minecraft.getInstance().level != null) {
            SoundsBeGoneClient.SoundMap.put(identifier.toString(), new java.util.Date());
        }

    }
}
