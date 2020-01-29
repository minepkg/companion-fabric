package io.minepkg.companion;

import com.google.gson.*;
import net.minecraft.server.ServerMetadata;
import net.minecraft.util.JsonHelper;

import java.lang.reflect.Type;

public class CustomServerMetadata extends ServerMetadata {
    public Modpack minepkgModpack;

    public CustomServerMetadata (ServerMetadata metadata) {
        super();

        // Copy the original metadata to the custom metadata
        setDescription(metadata.getDescription());
        setPlayers(metadata.getPlayers());
        setVersion(metadata.getVersion());
        setFavicon(metadata.getFavicon());
    }

    /** Handles (de)serialization of the custom & original metadata **/
    public static class Deserializer implements JsonDeserializer<CustomServerMetadata>, JsonSerializer<CustomServerMetadata> {
        public Deserializer() {
        }

        public CustomServerMetadata deserialize (JsonElement elem, Type type_1, JsonDeserializationContext ctx) throws JsonParseException {
            JsonObject obj = JsonHelper.asObject(elem, "status");
            ServerMetadata metadata = new ServerMetadata.Deserializer().deserialize(elem, type_1, ctx);
            CustomServerMetadata customMetadata = new CustomServerMetadata(metadata);

            if (obj.has("minepkgModpack")) {
                customMetadata.minepkgModpack = ctx.deserialize(obj.get("minepkgModpack"), Modpack.class);
            }

            return customMetadata;
        }

        public JsonElement serialize (CustomServerMetadata metadata, Type type, JsonSerializationContext ctx) {
            JsonObject obj = (new ServerMetadata.Deserializer().serialize(metadata, type, ctx)).getAsJsonObject();

            if (metadata.minepkgModpack != null) {
                obj.add("minepkgModpack", ctx.serialize(metadata.minepkgModpack));
            }

            return obj;
        }
    }
}
