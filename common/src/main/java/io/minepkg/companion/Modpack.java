package io.minepkg.companion;

import com.google.gson.*;
import net.minecraft.util.JsonHelper;

import java.lang.reflect.Type;

public record Modpack(String name, String version, String platform) {
    /** Handles (de)serialization of the modpack info **/
    public static class Serializer implements JsonDeserializer<Modpack>, JsonSerializer<Modpack> {
        public Modpack deserialize (JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            JsonObject obj = JsonHelper.asObject(json, "minepkgModpack");
            return new Modpack(JsonHelper.getString(obj, "name"), JsonHelper.getString(obj, "version"),
                               JsonHelper.getString(obj, "platform"));
        }

        public JsonElement serialize (Modpack modpack, Type type, JsonSerializationContext ctx) {
            JsonObject json = new JsonObject();

            json.addProperty("name", modpack.name());
            json.addProperty("version", modpack.version());
            json.addProperty("platform", modpack.platform());

            return json;
        }
    }
}
