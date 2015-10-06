package org.amityregion5.ZombieGame.client.asset;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.amityregion5.ZombieGame.ZombieGame;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

public class TextureRegistry {
	private static HashMap<String, Texture> textures = new HashMap<String, Texture>();

	// public static Array<Texture> zombieTextures = new Array<Texture>();

	public static void register(String path, FileHandle file) {
		Texture t = new Texture(file, true);
		t.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		textures.put(path, t);
	}
	
	public static List<Texture> getTexturesFor(String str) {
		List<Texture> t = textures.keySet().stream().sequential().filter((s)->s.matches(regexify(str))).map((k)->textures.get(k)).collect(Collectors.toList());
		if (t == null || t.size() == 0) {
			return Arrays.asList(new Texture[]{ZombieGame.instance.missingTexture});
		}
		return t;
	}
	
	private static String regexify(String str) {
		if (str == null) {
			return "";
		}
		String finalString = "";
		
		String[] split = str.split(Pattern.quote("**"));
		for (int i = 0; i<split.length; i++) {
			String[] split2 = split[i].split(Pattern.quote("*"));
			for (int i2 = 0; i2<split2.length; i2++) {
				String[] split3 = split2[i2].split(Pattern.quote("?"));
				for (int i3 = 0; i3<split3.length; i3++) {
					finalString += split3[i3] + (i3 == split3.length-1  ? "" : "[^/]?");
				}
				finalString += (split2[i2].endsWith("?") ? "[^/]?" : "");
				finalString += (i2 == split2.length-1 ? "" : "[^/]*");
			}
			finalString += (split[i].endsWith("*") ? "[^/]*" : "");
			finalString += (i == split.length-1 ? "" : ".*");
		}
		finalString += (str.endsWith("**") ? ".*" : "");
		
		return finalString;
		//return str.replace("*", "[^/]*").replace("?", "[^/]?");
	}

	public static void dispose() {
		for (Texture a : textures.values()) {
			a.dispose();
		}
		textures.clear();
		textures = null;
	}
}