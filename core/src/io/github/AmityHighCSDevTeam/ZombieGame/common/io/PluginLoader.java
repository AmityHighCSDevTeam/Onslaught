package io.github.AmityHighCSDevTeam.ZombieGame.common.io;

import io.github.AmityHighCSDevTeam.ZombieGame.ZombieGame;
import io.github.AmityHighCSDevTeam.ZombieGame.common.weapon.JsonWeaponHolder;
import io.github.AmityHighCSDevTeam.ZombieGame.common.weapon.SemiAuto;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class PluginLoader {
	public void loadPlugin(FileHandle[] plugins)
	{	
		//Create JSON reader/writer
		Json json = new Json();
		json.addClassTag("SemiAutoWeaponType", SemiAuto.class);
		
		//Loop through the plugin list
		for (FileHandle p : plugins) {
			if (p.isDirectory()) {//If it is a directory
				//Get the subdirectory called Weapons
				FileHandle weaponDir = p.child("Weapons");
				if (weaponDir.exists()) {//If it exsists	
					FileHandle[] weapons = weaponDir.list();//Get the list of all weapons						
					for (FileHandle weaponFolder : weapons) {//Loop through the weapons
						//If the weapon is a folder and has a file called <FolderName>.json
						if (weaponFolder.isDirectory() && weaponFolder.child(weaponFolder.nameWithoutExtension() + ".json").exists()) {
							FileHandle weapon = weaponFolder.child(weaponFolder.nameWithoutExtension() + ".json");
							//Read the Json and register it
							ZombieGame.instance.weaponRegistry.registerWeapon(json.fromJson(JsonWeaponHolder.class, weapon));
						}
					}
				}
			}
		}
	}
}
