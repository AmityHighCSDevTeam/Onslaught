package org.amityregion5.onslaught.client.asset;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.amityregion5.onslaught.Onslaught;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * A registry for all textures
 * @author sergeys
 *
 */
public class TextureRegistry {
	
	private static Set<String> registry = new HashSet<String>();
	
	private static PixmapPacker packer;
	private static TextureAtlas atlas;
	
	static {
		packer = new PixmapPacker(1024, 1024, Pixmap.Format.RGBA8888, 0, false);
		atlas = packer.generateTextureAtlas(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest, true);
	}
	
	//private static HashMap<String, Texture> textures = new HashMap<String, Texture>();

	/**
	 * Try to register a texture under something under another path
	 * 
	 * @param path the path to load the texture from
	 * @param toReplace the thing to load it under
	 * @return did it work
	 */
	public static boolean tryRegisterAs(String path, String toReplace) {
		if (registry.contains(path)) { return false; }
		FileHandle handle = Onslaught.instance.gameData.child(path);
		if (handle.exists() && handle.extension().equals("png")) {
			Onslaught.log("Texture Registry: registering: " + path + " as: " + toReplace);
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
		if (registry.contains(path)) { return false; }
		FileHandle handle = Onslaught.instance.gameData.child(path);
		if (handle.exists() && handle.extension().equals("png")) {
			Onslaught.log("Texture Registry: registering: " + path);
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
		registry.add(path);
		packer.pack(path, new Pixmap(file));
	}

	/**
	 * Get the textures that exist for a certain string
	 * 
	 * @param str the texture to search for
	 * @return a list of textures that matched the string
	 */
	/*public static List<Texture> getTexturesFor(String str) {
		List<Texture> t = registry.stream().sequential().filter((s) -> s.matches(regexify(str))).map((k) -> textures.get(k))
				.collect(Collectors.toList());
		if (t == null || t.size() == 0) { return Arrays.asList(new Texture[] {ZombieGame.instance.missingTexture}); }
		return t;
	}*/

	/**
	 * Get the texture names that exist for a certain string
	 * 
	 * @param str the texture name to search for
	 * @return a list of texture names that matched the string
	 */
	public static List<String> getTextureNamesFor(String str) {
		List<String> t = registry.stream().sequential().filter((s) -> s.matches(regexify(str))).collect(Collectors.toList());
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
		packer.dispose();
		atlas.dispose();
		registry.clear();
	}
	
	public static TextureAtlas getAtlas() {
		return atlas;
	}

	public static void update() {
		packer.updatePageTextures(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest, true);
		packer.updateTextureAtlas(atlas, TextureFilter.MipMapLinearNearest, TextureFilter.Nearest, true);
	}
}
