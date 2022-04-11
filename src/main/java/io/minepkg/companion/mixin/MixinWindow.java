package io.minepkg.companion.mixin;

import io.minepkg.companion.MinepkgCompanion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.MonitorTracker;
import net.minecraft.client.util.Window;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sun.misc.Signal;

import java.nio.IntBuffer;
import java.util.concurrent.TimeUnit;

@Mixin(Window.class)
public abstract class MixinWindow{
    private static boolean startupComplete = false;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwDefaultWindowHints()V", shift = At.Shift.AFTER))
    private void injected(CallbackInfo ci) {
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_FOCUS_ON_SHOW, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_FOCUSED, GLFW.GLFW_FALSE);
        //GLFW.glfwWindowHint(0x00020002, GLFW.GLFW_TRUE);
        // make our window transparent
//        GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, GLFW.GLFW_TRUE);
//        GLFW.glfwWindowHint(GLFW.GLFW_ALPHA_BITS, 8);
//        GLFW.glfwWindowHint(GLFW.GLFW_RED_BITS, 8);
//        GLFW.glfwWindowHint(GLFW.GLFW_GREEN_BITS, 8);
//        GLFW.glfwWindowHint(GLFW.GLFW_BLUE_BITS, 8);
//        GLFW.glfwWindowHint(GLFW.GLFW_REFRESH_RATE, 0);
    }


    @Inject(method = "<init>", at = @At("TAIL"))
    public void constructorTail (WindowEventHandler windowEventHandler, MonitorTracker monitorTracker, WindowSettings windowSettings, @Nullable String string, String string2, CallbackInfo ci) throws InterruptedException {
        String startMinimized = System.getenv("MINEPKG_COMPANION_START_MINIMIZED");
        String startFullscreen = System.getenv("MINEPKG_COMPANION_START_FULLSCREEN");
        long handle = ((Window) (Object) this).getHandle();

        if (startFullscreen != null) {
            GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
        }
        // if (startMinimized == null || startMinimized.equals("0") || startMinimized.equals("false")) return;

        // some platforms support this before showing the window
        GLFW.glfwIconifyWindow(handle);

        // make window tiny to avoid flickering
        // GLFW.glfwSetWindowSize(handle, 1, 1);

        // GLFW.glfwShowWindow(handle);
        GLFW.glfwSetWindowOpacity(handle, 0);
        GLFW.glfwIconifyWindow(handle);



        // start the thread that will wait for the window to be shown
        Thread thread = new Thread(() -> {
            try {
                // sleep for a bit to give the window time to show
                TimeUnit.MILLISECONDS.sleep(1000);
                System.out.println("waiting for window to show");
                GLFW.glfwShowWindow(handle);
                GLFW.glfwIconifyWindow(handle);
                // GLFW.glfwSetWindowOpacity(handle, 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread.start();
        // GLFW.glfwSetWindowOpacity(handle, 1);
        // set size back
        // GLFW.glfwSetWindowSize(handle, windowSettings.width, windowSettings.height);



        // resume sound when focused
        GLFW.glfwSetWindowIconifyCallback(handle, (window, iconified) -> {
            SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
            if (soundManager == null) return;
            if (!iconified) soundManager.resumeAll();
        });

        // maximize on process signal "SIGUSR1"
        Signal.handle(new Signal("USR1"), signal -> {
            GLFW.glfwRestoreWindow(handle);
            GLFW.glfwFocusWindow(handle);
            GLFW.glfwSetWindowOpacity(handle, 1);
        });
    }
}
