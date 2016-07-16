package org.amityregion5.onslaught.common.plugin;

import java.util.Optional;

import org.amityregion5.onslaught.Onslaught;
import org.amityregion5.onslaught.client.asset.SoundRegistry;
import org.amityregion5.onslaught.client.asset.TextureRegistry;
import org.amityregion5.onslaught.client.settings.InputData;
import org.amityregion5.onslaught.common.buff.Buff;
import org.amityregion5.onslaught.common.entity.EntityLantern;
import org.amityregion5.onslaught.common.entity.EntityZombie;
import org.amityregion5.onslaught.common.game.Game;
import org.amityregion5.onslaught.common.game.GameRegistry;
import org.amityregion5.onslaught.common.game.difficulty.BasicDifficulty;
import org.amityregion5.onslaught.common.game.model.IEntityModel;
import org.amityregion5.onslaught.common.game.model.entity.LanternModel;
import org.amityregion5.onslaught.common.game.model.entity.RocketModel;
import org.amityregion5.onslaught.common.game.model.entity.ZombieModel;
import org.amityregion5.onslaught.common.json.BuffSerializor;
import org.amityregion5.onslaught.common.json.EntityModelSerializor;
import org.amityregion5.onslaught.common.json.WeaponStackSerializor;
import org.amityregion5.onslaught.common.util.RandUtil;
import org.amityregion5.onslaught.common.weapon.WeaponStack;
import org.amityregion5.onslaught.common.weapon.data.GrenadeData;
import org.amityregion5.onslaught.common.weapon.data.PlaceableWeaponData;
import org.amityregion5.onslaught.common.weapon.data.RocketData;
import org.amityregion5.onslaught.common.weapon.data.ShotgunWeaponData;
import org.amityregion5.onslaught.common.weapon.data.SoundData;
import org.amityregion5.onslaught.common.weapon.data.WeaponData;
import org.amityregion5.onslaught.common.weapon.types.BasicGun;
import org.amityregion5.onslaught.common.weapon.types.Grenade;
import org.amityregion5.onslaught.common.weapon.types.Placeable;
import org.amityregion5.onslaught.common.weapon.types.Rocket;
import org.amityregion5.onslaught.common.weapon.types.Shotgun;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;

import box2dLight.PointLight;

/**
 * The plugin for the core game
 * @author sergeys
 *
 */
public class CorePlugin implements IPlugin {

	//The container
	private PluginContainer container;

	@Override
	public void init(PluginContainer container) {
		//Set container
		this.container = container;
	}

	@Override
	public void preLoad() {
		//Add all weapon classes to the container
		container.addWeaponClass(BasicGun.class);
		container.addWeaponClass(Shotgun.class);
		container.addWeaponClass(Placeable.class);
		container.addWeaponClass(Grenade.class);
		container.addWeaponClass(Rocket.class);
		
		Onslaught.instance.gsonBuilder.registerTypeAdapter(Buff.class, new BuffSerializor());
		
		Onslaught.instance.gsonBuilder.registerTypeHierarchyAdapter(IEntityModel.class, new EntityModelSerializor());
		
		Onslaught.instance.gsonBuilder.registerTypeAdapter(WeaponStack.class, new WeaponStackSerializor());
		
		Onslaught.instance.gsonBuilder.registerTypeAdapter(Game.class, new Game.GameSerializor());
		
		Onslaught.instance.gsonBuilder.registerTypeAdapter(SoundData.class, new SoundData.Deserializor());
		
		Onslaught.instance.gsonBuilder.registerTypeAdapter(WeaponData.class, new WeaponData.Deserializor());
		Onslaught.instance.gsonBuilder.registerTypeAdapter(ShotgunWeaponData.class, new ShotgunWeaponData.Deserializor());
		
		Onslaught.instance.gsonBuilder.registerTypeAdapter(GrenadeData.class, new GrenadeData.Deserializor());
		Onslaught.instance.gsonBuilder.registerTypeAdapter(RocketData.class, new RocketData.Deserializor());
		
		Onslaught.instance.gsonBuilder.registerTypeAdapter(PlaceableWeaponData.class, new PlaceableWeaponData.Deserializor());
	}

	@Override
	public void load() {
		//Register the lanterns
		Placeable.registeredObjects.put("BasicLantern", (game, position, data)->{
			LanternModel lantern = new LanternModel(new EntityLantern(), game, Color.WHITE.cpy().mul(1, 1, 1, 130f / 255), data.get("fieldTxtr").getAsString(), "BasicLantern", data, data.get("life").getAsFloat());
			lantern.setLight(new PointLight(game.getLighting(), 200, lantern.getColor(), data.get("lightLevel").getAsFloat(), position.x, position.y));
			lantern.getEntity().setFriction(0.99f);
			lantern.getEntity().setMass(10);
			return lantern;
		});

		//Add the spawnable for the main zombie
		GameRegistry.addSpawnable(0, 1, (g) -> {
			float maxModifier = 0.8f;
			
			float gaussRand = (float)RandUtil.boundedGaussian(g.getRandom(), -0.05, 0.85, 0.36, 0.4);

			float speedModifier = gaussRand * maxModifier + 1f - maxModifier / 1.5f;
			float sizeModifier = ((maxModifier + 1f) - speedModifier);

			EntityZombie zom = new EntityZombie(0.15f * sizeModifier);
			zom.setMass(100 * sizeModifier * sizeModifier);
			// zom.setSpeed(1f);
			zom.setFriction(0.99f);

			ZombieModel model = new ZombieModel(zom, g, sizeModifier);

			model.setSpeed(0.03f * speedModifier);
			model.setAllHealth((float) (Math.pow(1.1, Math.sqrt(g.getMobsSpawned())) + 4) * (g.getDifficulty().getZombieHealthModifier() + 1) * sizeModifier
					* sizeModifier);
			model.setPrizeMoney((5 + model.getHealth() / 2) * (g.getDifficulty().getMoneyMultiplier()) * sizeModifier * sizeModifier);
			model.setDamage((5 + model.getHealth() / 2) * (g.getDifficulty().getDamageMultiplier()) * sizeModifier * sizeModifier);
			model.setRange(zom.getShape().getRadius() * 1.1f);

			return model;
		});

		//Add difficulty levels to the game
		if (Onslaught.instance.isCheatModeAllowed) {
			GameRegistry.difficulties.add(new BasicDifficulty("DEBUG", "Debug", 0f, 0.5f, 0.25f, 2f, 2f, 0.01f, 1000, 50));
		}
		//																	   overall, wave,  health,  money,  dmg,    pack,  start,  hostiles
		GameRegistry.difficulties.add(new BasicDifficulty("NOVICE", "Novice", 	0.5f, 	0.5f, 	0.25f, 	2f, 	2f, 	0.01, 	1000, 	50));
		GameRegistry.difficulties.add(new BasicDifficulty("EASY", 	"Easy",		1f, 	1f, 	0.5f, 	1.5f, 	1.5f, 	75e-4,	750, 	100));
		GameRegistry.difficulties.add(new BasicDifficulty("MEDIUM", "Medium", 	1.5f, 	1.5f, 	0.75f, 	1.0f, 	1.0f, 	0.005, 	500, 	150));
		GameRegistry.difficulties.add(new BasicDifficulty("HARD", 	"Hard",		1.75f, 	1.75f, 	0.875f, 0.8f, 	1.5f, 	375e-5,	375, 	175));
		GameRegistry.difficulties.add(new BasicDifficulty("INSANE", "Insane", 	2f, 	2f, 	1f, 	0.6f, 	0.5f, 	25e-4,	250, 	200));
	}

	@Override
	public void postLoad() {
		//Load explosion texture
		TextureRegistry.tryRegisterAs("Core/explosion.png", "explosion");
		//Load background tile texture
		TextureRegistry.tryRegisterAs("Core/backgroundTile2.png", "backgroundTile");
		//Load health pack texture
		TextureRegistry.tryRegisterAs("Core/HealthBox.png", "healthPack");
		//Load upgrade arrow texture
		TextureRegistry.tryRegisterAs("Core/UpgradeArrow.png", "upgradeArrow");

		//Load all blood textures
		TextureRegistry.tryRegisterAs("Core/Blood/blood1.png", "blood/core/blood1");

		//Load all zombie growls
		SoundRegistry.tryRegister("Core/Audio/Zombie/ZombieGrowl1.wav");
		SoundRegistry.tryRegister("Core/Audio/Zombie/ZombieGrowl2.wav");
		SoundRegistry.tryRegister("Core/Audio/Zombie/ZombieGrowl3.wav");
		SoundRegistry.tryRegister("Core/Audio/Zombie/ZombieGrowl4.wav");
		SoundRegistry.tryRegister("Core/Audio/Zombie/ZombieGrowl5.wav");
		SoundRegistry.tryRegister("Core/Audio/Zombie/ZombieGrowl6.wav");

		//Load explode sound
		SoundRegistry.tryRegister("Core/Audio/explode.wav");

		//Register inputs
		Onslaught.instance.settings.registerInput("Shoot", new InputData(false, Buttons.LEFT));
		Onslaught.instance.settings.registerInput("Move_Up", new InputData(true, Keys.W));
		Onslaught.instance.settings.registerInput("Move_Down", new InputData(true, Keys.S));
		Onslaught.instance.settings.registerInput("Move_Right", new InputData(true, Keys.D));
		Onslaught.instance.settings.registerInput("Move_Left", new InputData(true, Keys.A));
		Onslaught.instance.settings.registerInput("Toggle_Flashlight", new InputData(true, Keys.F));
		Onslaught.instance.settings.registerInput("Buy_Ammo", new InputData(true, Keys.B));
		Onslaught.instance.settings.registerInput("Reload", new InputData(true, Keys.R));
		Onslaught.instance.settings.registerInput("Hotbar_1", new InputData(true, Keys.NUM_1));
		Onslaught.instance.settings.registerInput("Hotbar_2", new InputData(true, Keys.NUM_2));
		Onslaught.instance.settings.registerInput("Hotbar_3", new InputData(true, Keys.NUM_3));
		Onslaught.instance.settings.registerInput("Shop_Window", new InputData(true, Keys.P));
		Onslaught.instance.settings.registerInput("Inventory_Window", new InputData(true, Keys.I));
		Onslaught.instance.settings.registerInput("Close_Window", new InputData(true, Keys.ESCAPE));
	}

	@Override
	public void onGameStart(Game game) {
		//Add a listener for rocket collisions
		game.getContactListener().addBeginContactListener((c) -> {
			//When a rocket collides with anything trigger its onHit method
			if (c.getFixtureA().getBody() != null) {
				Optional<IEntityModel<?>> model = game.getEntityFromBody(c.getFixtureA().getBody());

				model.ifPresent((en) -> {
					if (en instanceof RocketModel) {
						((RocketModel) en).onHit();
					}
				});
			}
			if (c.getFixtureB().getBody() != null) {
				Optional<IEntityModel<?>> model = game.getEntityFromBody(c.getFixtureB().getBody());

				model.ifPresent((en) -> {
					if (en instanceof RocketModel) {
						((RocketModel) en).onHit();
					}
				});
			}
		});
	}

	@Override
	public void dispose() {}
}
