package io.minepkg.companion;

import com.google.gson.*;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;

import java.lang.reflect.Type;

public class CustomServerMetadata extends ServerMetadata {
    public Modpack modpack;

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
            ServerMetadata metadata = new ServerMetadata();

            if (obj.has("description")) {
                metadata.setDescription(ctx.deserialize(obj.get("description"), Text.class));
            }

            if (obj.has("players")) {
                metadata.setPlayers(ctx.deserialize(obj.get("players"), ServerMetadata.Players.class));
            }

            if (obj.has("version")) {
                metadata.setVersion(ctx.deserialize(obj.get("version"), ServerMetadata.Version.class));
            }

            if (obj.has("favicon")) {
                metadata.setFavicon(JsonHelper.getString(obj, "favicon"));
            }

            CustomServerMetadata customMetadata = new CustomServerMetadata(metadata);

            if (obj.has("modpack")) {
                customMetadata.modpack = ctx.deserialize(obj.get("modpack"), Modpack.class);
            }

            return customMetadata;
        }

        public JsonElement serialize (CustomServerMetadata metadata, Type type, JsonSerializationContext ctx) {
            JsonObject obj = new JsonObject();

            if (metadata.getDescription() != null) {
                obj.add("description", ctx.serialize(metadata.getDescription()));
            }

            if (metadata.getPlayers() != null) {
                obj.add("players", ctx.serialize(metadata.getPlayers()));
            }

            if (metadata.getVersion() != null) {
                obj.add("version", ctx.serialize(metadata.getVersion()));
            }

            if (metadata.getFavicon() != null) {
                obj.addProperty("favicon", metadata.getFavicon());
            }

            if (metadata.modpack != null) {
                obj.add("modpack", ctx.serialize(metadata.modpack));
            }

            return obj;
        }
    }
}
