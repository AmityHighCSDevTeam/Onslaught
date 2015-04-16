package org.amityregion5.ZombieGame.client.game;

import java.util.HashMap;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

public class TextureRegistry {
	
	public static HashMap<String, Array<Texture>> textures = new HashMap<String, Array<Texture>>();
	//public static Array<Texture> zombieTextures = new Array<Texture>();

	public static void register(String type, FileHandle file) {
		if (!textures.containsKey(type)) {
			textures.put(type, new Array<Texture>());
		}
		textures.get(type).add(new Texture(file));
	}

	public static void dispose() {
		for (Array<Texture> a : textures.values()) {
			for (Texture t : a) {
				t.dispose();
			}
			a.clear();
		}
		textures.clear();
		textures = null;
	}
}
