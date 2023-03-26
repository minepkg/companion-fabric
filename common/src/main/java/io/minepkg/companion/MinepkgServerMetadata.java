package io.minepkg.companion;

import org.jetbrains.annotations.Nullable;

public interface MinepkgServerMetadata {
    void setModpack(Modpack modpack);
    @Nullable
    Modpack getModpack();
}
