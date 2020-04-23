package io.minepkg.companion.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moandjiezana.toml.Toml;
import io.minepkg.companion.CustomServerMetadata;
import io.minepkg.companion.MinepkgCompanion;
import io.minepkg.companion.Modpack;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

class MinepkgCompanionTest {
    private final String MANIFEST_PATH = "./test-manifests/1.15/minepkg.toml";

    @Test
    @DisplayName("Get manifest")
    void getToml() {
        Toml toml = Objects.requireNonNull(MinepkgCompanion.getToml(MANIFEST_PATH));
        assertEquals(toml.getLong("manifestVersion"), 0L);
    }

    @Test
    @DisplayName("Get modpack object")
    void getModpack() {
        Modpack modpack = Objects.requireNonNull(MinepkgCompanion.getModpack(MANIFEST_PATH));
        assertEquals(modpack.getName() + "@" + modpack.getVersion(), "minepkg-companion-test@0.1.0");
    }
}
