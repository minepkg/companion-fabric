package io.minepkg.companion.mc1_17;

import com.google.gson.*;
import io.minepkg.companion.common.Modpack;
import net.minecraft.util.JsonHelper;

import java.lang.reflect.Type;

public record ModpackDeserializer() implements JsonDeserializer<Modpack>, JsonSerializer<Modpack> {
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