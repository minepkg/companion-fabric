package io.minepkg.companion.mixin;

import com.google.gson.*;
import io.minepkg.companion.MinepkgServerMetadata;
import io.minepkg.companion.Modpack;
import net.minecraft.server.ServerMetadata;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Type;

@Mixin(ServerMetadata.Deserializer.class)
public abstract class ServerMetadataDeserializerMixin {
	@Inject(
		method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/server/ServerMetadata;",
		at = @At("RETURN"),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void deserializeMinepkg(JsonElement jsonElement, Type type, JsonDeserializationContext ctx, CallbackInfoReturnable<ServerMetadata> cir, JsonObject jsonObject, ServerMetadata serverMetadata) throws JsonParseException {
		MinepkgServerMetadata minepkgMetadata = (MinepkgServerMetadata) serverMetadata;

		if (jsonObject.has("minepkgModpack")) {
			Modpack modpack = ctx.deserialize(jsonObject.get("minepkgModpack"), Modpack.class);
			minepkgMetadata.setModpack(modpack);
		}
	}

	@Inject(
		method = "serialize(Lnet/minecraft/server/ServerMetadata;Ljava/lang/reflect/Type;Lcom/google/gson/JsonSerializationContext;)Lcom/google/gson/JsonElement;",
		at = @At("RETURN"),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void serializeMinepkg(ServerMetadata serverMetadata, Type type, JsonSerializationContext ctx, CallbackInfoReturnable<JsonElement> cir, JsonObject jsonObject) throws JsonParseException {
		MinepkgServerMetadata minepkgMetadata = (MinepkgServerMetadata) serverMetadata;

		if (minepkgMetadata.getModpack() != null) {
			jsonObject.add("minepkgModpack", ctx.serialize(minepkgMetadata.getModpack()));
		}
	}
}
