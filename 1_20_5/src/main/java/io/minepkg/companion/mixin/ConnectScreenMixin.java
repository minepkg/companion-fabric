package io.minepkg.companion.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.CookieStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ConnectScreen.class)
public interface ConnectScreenMixin {
    @Invoker("connect")
    static void invokeConnect(MinecraftClient client, ServerAddress address, ServerInfo info, CookieStorage storage) {
        throw new AssertionError();
    }
}
