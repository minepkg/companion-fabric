package io.minepkg.companion;

import com.google.gson.*;
import net.minecraft.util.JsonHelper;

import java.lang.reflect.Type;

public class Modpack {
    private final String name;
    private final String version;
    private final String platform;

    public Modpack(String name, String version, String platform) {
        this.name = name;
        this.version = version;
        this.platform = platform;
    }

    public String getName () {
        return name;
    }

    public String getVersion () {
        return version;
    }

    public String getPlatform () {
        return platform;
    }

    /** Handles (de)serialization of the modpack info **/
    public static class Serializer implements JsonDeserializer<Modpack>, JsonSerializer<Modpack> {
        public Serializer () {
        }

        public Modpack deserialize (JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            JsonObject obj = JsonHelper.asObject(json, "minepkgModpack");
            return new Modpack(JsonHelper.getString(obj, "name"), JsonHelper.getString(obj, "version"),
                               JsonHelper.getString(obj, "platform"));
        }

        public JsonElement serialize (Modpack modpack, Type type, JsonSerializationContext ctx) {
            JsonObject json = new JsonObject();

            json.addProperty("name", modpack.getName());
            json.addProperty("version", modpack.getVersion());
            json.addProperty("platform", modpack.getPlatform());

            return json;
        }
    }
}
