package io.github.AmityHighCSDevTeam.ZombieGame;

import io.github.AmityHighCSDevTeam.ZombieGame.client.screen.LoadingScreen;
import io.github.AmityHighCSDevTeam.ZombieGame.client.screen.MainMenu;
import io.github.AmityHighCSDevTeam.ZombieGame.common.weapon.IWeapon;
import io.github.AmityHighCSDevTeam.ZombieGame.common.weapon.SemiAuto;
import io.github.AmityHighCSDevTeam.ZombieGame.common.weapon.JsonWeaponHolder;
import io.github.AmityHighCSDevTeam.ZombieGame.common.weapon.WeaponRegistry;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Json;

public class ZombieGame extends Game {

	public static BitmapFont buttonFont;
	public boolean isServer;
	public WeaponRegistry weaponRegistry;

	public ZombieGame(boolean isServer) {
		this.isServer = isServer;
	}
	
	@Override
	public void create () {
		setScreen(new LoadingScreen());
		
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				FileHandle gameData = Gdx.files.local("ZombieGameData/GameData");
				
				FileHandle[] plugins = gameData.list();
				
				Json json = new Json();
				json.addClassTag("SemiAutoWeaponType", SemiAuto.class);
				
				weaponRegistry = new WeaponRegistry();
				
				for (FileHandle p : plugins) {
					if (p.isDirectory()) {
						
						FileHandle weaponDir = p.child("Weapons");
						
						if (weaponDir.exists()) {
							
							FileHandle[] weapons = weaponDir.list();
							
							for (FileHandle weaponFolder : weapons) {
								
								if (weaponFolder.isDirectory() && weaponFolder.child(weaponFolder.nameWithoutExtension() + ".json").exists()) {
									
									FileHandle weapon = weaponFolder.child(weaponFolder.nameWithoutExtension() + ".json");
									
									weaponRegistry.registerWeapon(json.fromJson(JsonWeaponHolder.class, weapon));
								}
							}
						}
					}
				}
				/*
				FileHandle m9f = Gdx.files.local("ZombieGameData/GameData/Core/Weapons/M9/M9.json");
				
				IWeapon m9 = new SemiAuto();
				m9.setAmmoPrice(1);
				m9.setName("M9");
				m9.setWeaponPrice(10);
				m9.setDescription("A basic pistol.");
				m9.setBulletDamage(1);
				m9.setBulletSpeed(10);
				m9f.writeString(json.prettyPrint(new JsonWeaponHolder(m9)), false);
				*/
				if (!isServer) {
					FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/Calibri.ttf"));

					FreeTypeFontParameter parameter = new FreeTypeFontParameter();
					parameter.size = 24;

					buttonFont = generator.generateFont(parameter);

					generator.dispose();

					buttonFont.setColor(0, 0, 0, 1);
				}
				
				setScreen(new MainMenu());
			}
		});
	}
	@Override
	public void render () {
		super.render();
	}
	@Override
	public void dispose() {
		super.dispose();
		buttonFont.dispose();
	}
	@Override
	public void pause() {
		super.pause();
	}	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}	
	@Override
	public void resume() {
		super.resume();
	}
}
