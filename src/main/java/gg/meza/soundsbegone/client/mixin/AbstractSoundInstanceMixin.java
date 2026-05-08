package gg.meza.soundsbegone.client.mixin;

import gg.meza.soundsbegone.SoundsBeGoneConfig;
import gg.meza.soundsbegone.client.SoundEmissionRegulator;
import gg.meza.soundsbegone.client.SoundsBeGoneClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static gg.meza.soundsbegone.client.SoundsBeGoneClient.soundEmissionRegulator;

@Mixin(AbstractSoundInstance.class)
public class AbstractSoundInstanceMixin {

    private final Random random = Random.createLocal();

    @Final
    @Shadow
    protected Identifier id;

    @Inject(
            method = "getVolume()F",
            at = @At("HEAD"),
            cancellable = true)
    private void getVolume(CallbackInfoReturnable<Float> cir) {

        if (SoundsBeGoneClient.config.isSoundDisabled(id.toString())) {
            SoundsBeGoneConfig.LOGGER.debug("Disabling the sound: {}", id);
            SoundsBeGoneClient.telemetry.blockedSound(id.toString());
            cir.setReturnValue(0.0F);
            cir.cancel();
        } else if (SoundsBeGoneClient.config.isSoundInfrequent(id.toString())) {
            double value = random.nextDouble()*100;
            double weightedChance = soundEmissionRegulator.weightedChance(id.toString(), SoundsBeGoneClient.config.getFrequencyPercentage());
            boolean playSound = value < weightedChance;

            if (!playSound) {
                SoundsBeGoneConfig.LOGGER.debug("Reducing the sound: {}", id);
                SoundsBeGoneClient.telemetry.blockedSound(id.toString());
                cir.setReturnValue(0.0F);
                cir.cancel();
            }
        }

        if (MinecraftClient.getInstance().world != null) {
            SoundsBeGoneClient.SoundMap.put(id.toString(), new java.util.Date());
        }

    }
}
