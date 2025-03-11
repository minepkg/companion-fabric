package io.minepkg.companion.mc1_19_4;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.minepkg.companion.common.Modpack;

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
