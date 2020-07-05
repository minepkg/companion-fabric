package io.minepkg.companion.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.moandjiezana.toml.Toml;
import io.minepkg.companion.MinepkgCompanion;
import io.minepkg.companion.Modpack;
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
        // TODO: this probably could look nicer
        MinepkgCompanion.INSTANCE = new MinepkgCompanion();
        MinepkgCompanion.INSTANCE.fallbackManifestPath = MANIFEST_PATH;
        Modpack modpack = Objects.requireNonNull(MinepkgCompanion.getModpack());
        assertEquals(modpack.getName() + "@" + modpack.getVersion(), "minepkg-companion-test@0.1.0");
    }
}
