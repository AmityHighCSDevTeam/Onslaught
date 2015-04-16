package org.amityregion5.ZombieGame.common.io;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.game.TextureRegistry;
import org.amityregion5.ZombieGame.common.weapon.SemiAuto;
import org.amityregion5.ZombieGame.common.weapon.WeaponWrapper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

/**
 * 
 * @author sergeys
 *
 */
public class PluginLoader {
	/**
	 * 
	 * @param plugins the list of files that can possibly be plugins
	 */
	public void loadPlugin(FileHandle[] plugins)
	{	
		//Create JSON reader/writer
		Json json = new Json();
		json.setOutputType(OutputType.json);
		json.addClassTag("SemiAutoWeaponType", SemiAuto.class);

		//Loop through the plugin list
		for (FileHandle p : plugins) {
			if (p.isDirectory()) {//If it is a directory
				//Get the subdirectory called Weapons
				loadWeaponsForDir(json, p);
				if (!ZombieGame.instance.isServer) {
					Gdx.app.postRunnable(()->{loadTextures(json, p);});
				}
			}
		}
	}

	private void loadWeaponsForDir(Json json, FileHandle handle) {
		FileHandle weaponDir = handle.child("Weapons");
		if (weaponDir.exists()) {//If it exsists	
			FileHandle[] weapons = weaponDir.list();//Get the list of all weapons						
			for (FileHandle weaponFolder : weapons) {//Loop through the weapons
				//If the weapon is a folder and has a file called <FolderName>.json
				if (weaponFolder.isDirectory() && weaponFolder.child(weaponFolder.nameWithoutExtension() + ".json").exists()) {
					FileHandle weapon = weaponFolder.child(weaponFolder.nameWithoutExtension() + ".json");
					//Read the Json and register it
					ZombieGame.instance.weaponRegistry.registerWeapon(json.fromJson(WeaponWrapper.class, weapon));
				}
			}
		}
	}

	private void loadTextures(Json json, FileHandle handle) {
		{
			FileHandle zombieDir = handle.child("Zombies");
			if (zombieDir.exists()) {
				FileHandle[] zombies = zombieDir.list();
				for (FileHandle zombie : zombies) {
					if (!zombie.isDirectory() && zombie.extension().equals("png")) {
						TextureRegistry.register("Zombie", zombie);
					}
				}
			}
		}
		{
			FileHandle playerDir = handle.child("Players");
			if (playerDir.exists()) {
				FileHandle[] players = playerDir.list();
				for (FileHandle player : players) {
					if (!player.isDirectory() && player.exists() && !player.file().isHidden() && player.extension().equals("png")) {
						TextureRegistry.register("Player", player);
					}
				}
			}
		}
	}
}
