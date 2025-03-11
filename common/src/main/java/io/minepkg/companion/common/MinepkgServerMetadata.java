package io.minepkg.companion.common;

import org.jetbrains.annotations.Nullable;

public interface MinepkgServerMetadata {
    void setModpack(Modpack modpack);
    @Nullable
    Modpack getModpack();
}
