package io.minepkg.companion.mixin;

import io.minepkg.companion.MinepkgCompanion;
import net.minecraft.client.gui.screen.SplashOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashOverlay.class)
public abstract class MixinSplashOverlay {
    // grab the private progress field.
    @Shadow float progress;

    // render is the function that changes progress
    @Inject(at = @At("TAIL"), method = "render")
    private void init(CallbackInfo info) {

        // simply log the progress so that the player can see it
        // TODO: Beautify the output
        MinepkgCompanion.LOGGER.warn(String.format("Loading: %-5f", this.progress));

    }
}
