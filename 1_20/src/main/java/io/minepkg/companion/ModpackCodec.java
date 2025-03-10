package io.minepkg.companion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ModpackCodec {
    /** Handles (de)serialization of Modpack **/
    public static final Codec<Modpack> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
                Codec.STRING.fieldOf("name").forGetter(Modpack::name),
                Codec.STRING.fieldOf("version").forGetter(Modpack::version),
                Codec.STRING.fieldOf("platform").forGetter(Modpack::platform)
            )
            .apply(instance, Modpack::new)
    );
}
