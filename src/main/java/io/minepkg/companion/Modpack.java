package io.minepkg.companion;

import com.google.gson.*;
import net.minecraft.util.JsonHelper;

import java.lang.reflect.Type;

public class Modpack {
    private final String name;
    private final String version;

    public Modpack(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName () {
        return name;
    }

    public String getVersion () {
        return version;
    }

    /** Handles (de)serialization of the modpack info **/
    public static class Serializer implements JsonDeserializer<Modpack>, JsonSerializer<Modpack> {
        public Serializer () {

        }

        public Modpack deserialize (JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            JsonObject obj = JsonHelper.asObject(json, "modpack");
            return new Modpack(JsonHelper.getString(obj, "name"), JsonHelper.getString(obj, "version"));
        }

        public JsonElement serialize (Modpack modpack, Type type, JsonSerializationContext ctx) {
            JsonObject json = new JsonObject();
            json.addProperty("name", modpack.getName());
            json.addProperty("version", modpack.getVersion());
            return json;
        }
    }
}
