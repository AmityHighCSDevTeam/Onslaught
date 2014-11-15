package org.amityregion5.ZombieGame.common.io;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.common.weapon.SemiAuto;
import org.amityregion5.ZombieGame.common.weapon.WeaponWrapper;

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
		
		
		//Code for regenerating the M9 Weapon
		/*
		{
			SemiAuto m9 = new SemiAuto();
			m9.setBulletDamage(1);
			m9.setBulletSpeed(10);
			m9.setAmmoPrice(1);
			m9.setName("M9");
			m9.setWeaponPrice(10);
			m9.setDescription("A basic pistol.");
			m9.setPreFireDelay(0);
			m9.setPostFireDelay(10);
			
			com.badlogic.gdx.Gdx.files.local("ZombieGameData/GameData/Core/Weapons/M9/M9.json").writeString(json.prettyPrint(new JsonWeaponHolder(m9)), false);
		}
		*/
		
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
							ZombieGame.instance.weaponRegistry.registerWeapon(json.fromJson(WeaponWrapper.class, weapon));
						}
					}
				}
			}
		}
	}
}
