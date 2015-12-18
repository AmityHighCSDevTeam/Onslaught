package org.amityregion5.ZombieGame.client.asset;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.amityregion5.ZombieGame.ZombieGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

/**
 * A registry for all textures
 * @author sergeys
 *
 */
public class TextureRegistry {
	private static HashMap<String, Texture> textures = new HashMap<String, Texture>();

	/**
	 * Try to register a texture under something under another path
	 * 
	 * @param path the path to load the texture from
	 * @param toReplace the thing to load it under
	 * @return did it work
	 */
	public static boolean tryRegisterAs(String path, String toReplace) {
		if (textures.containsKey(path)) { return false; }
		FileHandle handle = ZombieGame.instance.gameData.child(path);
		if (handle.exists() && handle.extension().equals("png")) {
			ZombieGame.log("Texture Registry: registering: " + path + " as: " + toReplace);
			register(toReplace, handle);
			return true;
		}
		return false;
	}

	/**
	 * Try to register a texture
	 * 
	 * @param path the path to load the texture from
	 * @return did it work
	 */
	public static boolean tryRegister(String path) {
		if (textures.containsKey(path)) { return false; }
		FileHandle handle = ZombieGame.instance.gameData.child(path);
		if (handle.exists() && handle.extension().equals("png")) {
			ZombieGame.log("Texture Registry: registering: " + path);
			register(path, handle);
			return true;
		}
		return false;
	}

	/**
	 * Register a texture
	 * 
	 * @param path the path
	 * @param file the file to register
	 */
	public static void register(String path, FileHandle file) {
		textures.put(path, null);
		Gdx.app.postRunnable(() -> {
			Texture t = new Texture(file, true);
			t.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
			textures.put(path, t);
		});
	}

	/**
	 * Get the textures that exist for a certain string
	 * 
	 * @param str the texture to search for
	 * @return a list of textures that matched the string
	 */
	public static List<Texture> getTexturesFor(String str) {
		List<Texture> t = textures.keySet().stream().sequential().filter((s) -> s.matches(regexify(str))).map((k) -> textures.get(k))
				.collect(Collectors.toList());
		if (t == null || t.size() == 0) { return Arrays.asList(new Texture[] {ZombieGame.instance.missingTexture}); }
		return t;
	}

	/**
	 * Get the texture names that exist for a certain string
	 * 
	 * @param str the texture name to search for
	 * @return a list of texture names that matched the string
	 */
	public static List<String> getTextureNamesFor(String str) {
		List<String> t = textures.keySet().stream().sequential().filter((s) -> s.matches(regexify(str))).collect(Collectors.toList());
		if (t == null || t.size() == 0) { return Arrays.asList(new String[] {"--Null Texture--"}); }
		return t;
	}

	/**
	 * Regexify a string
	 * 
	 * @param str the string to regexify
	 * @return the regexified string
	 */
	private static String regexify(String str) {
		if (str == null) { return ""; }
		String finalString = "";

		String[] split = str.split(Pattern.quote("**"));
		for (int i = 0; i < split.length; i++) {
			String[] split2 = split[i].split(Pattern.quote("*"));
			for (int i2 = 0; i2 < split2.length; i2++) {
				String[] split3 = split2[i2].split(Pattern.quote("?"));
				for (int i3 = 0; i3 < split3.length; i3++) {
					finalString += split3[i3] + (i3 == split3.length - 1 ? "" : "[^/]?");
				}
				finalString += (split2[i2].endsWith("?") ? "[^/]?" : "");
				finalString += (i2 == split2.length - 1 ? "" : "[^/]*");
			}
			finalString += (split[i].endsWith("*") ? "[^/]*" : "");
			finalString += (i == split.length - 1 ? "" : ".*");
		}
		finalString += (str.endsWith("**") ? ".*" : "");

		return finalString;
	}

	public static void dispose() {
		for (Texture a : textures.values()) {
			a.dispose();
		}
		textures.clear();
		textures = null;
	}
}
