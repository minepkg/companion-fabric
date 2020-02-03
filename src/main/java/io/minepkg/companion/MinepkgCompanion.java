package io.minepkg.companion;

import com.google.gson.Gson;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.TreeTypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.moandjiezana.toml.Toml;
import net.fabricmc.api.ClientModInitializer;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MinepkgCompanion implements ClientModInitializer {
	public static final int COMPATIBLE_MANIFEST_VERSION = 0;

	public boolean opened = false;
	public static MinepkgCompanion INSTANCE;

	@Override
	public void onInitializeClient () {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		INSTANCE = this;
		System.out.println("Started the minepkg companion.");
	}

	/**
	 * Checks for and parses the minepkg.toml file.
	 * @return The parsed TOML from the minepkg.toml file. Returns null when the file doesn't exist.
	 */
	public static Toml getToml (String path) {
		File file = new File(path);

		if (!file.exists()) {
			// We're not playing a modpack
			return null;
		}

		return new Toml().read(file);
	}

	/**
	 * Gets the modpack that's being ran.
	 * @return The modpack. Returns null when we aren't running a minepkg modpack.
	 */
	public static Modpack getModpack (String manifestPath) {
		Toml manifest = MinepkgCompanion.getToml(manifestPath);

		if (manifest == null) {
			// If the manifest doesn't exist, we don't have a modpack
			return null;
		}

		Long manifestVersion = manifest.getLong("manifestVersion");
		String type = manifest.getString("package.type");
		String modpackName = manifest.getString("package.name");
		String modpackVersion = manifest.getString("package.version");

		if (manifestVersion == null || manifestVersion != COMPATIBLE_MANIFEST_VERSION) {
			// Invalid manifest version
			return null;
		}

		if (type == null || !type.equalsIgnoreCase("modpack") ||
			modpackName == null || modpackVersion == null) {
			// Invalid manifest
			return null;
		}

		return new Modpack(modpackName, modpackVersion, "fabric");
	}

	/**
	 * Makes a field public and not-final.
	 * Used to modify the factories field of a Gson object.
	 * @param clazz The class of the field.
	 * @param name The name of the field.
	 * @return The data in the field.
	 * @throws NoSuchFieldException If the field doesn't exist.
	 * @throws IllegalAccessException If the field is inaccessible.
	 */
	private static SettableField makeFieldSettable (Object clazz, String name)
			throws NoSuchFieldException, IllegalAccessException {
		// Get the field and make it public
		Field field = clazz.getClass().getDeclaredField(name);
		field.setAccessible(true);

		// Make the field not-final
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

		return new SettableField(field, field.get(clazz));
	}

	/**
	 * Makes a field private and final.
	 * Used to revert making the factories Gson field settable.
	 * @param clazz The class of the field.
	 * @param name The name of the field.
	 * @throws NoSuchFieldException If the field doesn't exist.
	 * @throws IllegalAccessException If the field is inaccessible.
	 */
	private static void makeFieldPrivateFinal (Object clazz, String name)
			throws NoSuchFieldException, IllegalAccessException {
		// Get the field and make it private
		Field field = clazz.getClass().getDeclaredField(name);
		field.setAccessible(false);

		// Make the field final
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & Modifier.FINAL);
	}

	/**
	 * Adds a type adapter to an already-built Gson object.
	 * @param GSON The Gson object.
	 * @param typeAdapter The type adapter class.
	 * @param <T> The type of the type adapter class.
	 * @throws NoSuchFieldException If there is no `factories` field.
	 * @throws IllegalAccessException If the `factories` field is inaccessible.
	 * @throws InstantiationException If the type adapter can't be instantiated.
	 */
	@SuppressWarnings("unchecked")
	public static <T> void addTypeAdapterToGson (Gson GSON, Class<T> typeAdapter)
			throws NoSuchFieldException, IllegalAccessException, InstantiationException {
		// Make the factories field settable and get the SettableField
		MinepkgCompanion.SettableField settable = MinepkgCompanion.makeFieldSettable(GSON, "factories");

		// Copy the immutable list of factories to a mutable list
		List<TypeAdapterFactory> immutableFactories = (List<TypeAdapterFactory>) settable.data;
		List<TypeAdapterFactory> factories = new ArrayList<>(immutableFactories);

		// Get the TypeToken from the adapter class and add it to the mutable list of factories
		TypeToken<T> typeToken = TypeToken.get(typeAdapter);
		factories.add(TreeTypeAdapter.newFactoryWithMatchRawType(typeToken, typeAdapter.newInstance()));

		// Copy the mutable list of factories to an immutable list and set the factories field
		List<TypeAdapterFactory> newImmutableFactories = Collections.unmodifiableList(factories);
		settable.field.set(GSON, newImmutableFactories);

		// Make the field private and final again
		MinepkgCompanion.makeFieldPrivateFinal(GSON, "factories");
	}

	public static class SettableField {
		public final Field field;
		public final Object data;

		SettableField(Field field, Object data) {
			this.field = field;
			this.data = data;
		}
	}
}
