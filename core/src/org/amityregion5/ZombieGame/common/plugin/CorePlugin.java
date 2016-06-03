package org.amityregion5.ZombieGame.common.plugin;

import java.util.Optional;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.asset.SoundRegistry;
import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.client.settings.InputData;
import org.amityregion5.ZombieGame.common.entity.EntityLantern;
import org.amityregion5.ZombieGame.common.entity.EntityZombie;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.GameRegistry;
import org.amityregion5.ZombieGame.common.game.difficulty.BasicDifficulty;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.amityregion5.ZombieGame.common.game.model.entity.LanternModel;
import org.amityregion5.ZombieGame.common.game.model.entity.RocketModel;
import org.amityregion5.ZombieGame.common.game.model.entity.ZombieModel;
import org.amityregion5.ZombieGame.common.weapon.types.BasicGun;
import org.amityregion5.ZombieGame.common.weapon.types.Grenade;
import org.amityregion5.ZombieGame.common.weapon.types.Placeable;
import org.amityregion5.ZombieGame.common.weapon.types.Rocket;
import org.amityregion5.ZombieGame.common.weapon.types.Shotgun;

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
	}

	@Override
	public void load() {
		//Register the lanterns
		Placeable.registeredObjects.put("Lantern_0", (g, vector) -> {
			LanternModel lantern = new LanternModel(new EntityLantern(), g, Color.WHITE.cpy().mul(1, 1, 1, 130f / 255), "Core/Entity/Lantern/Lantern.png", "Lantern_0");
			lantern.setLight(new PointLight(g.getLighting(), 200, lantern.getColor(), 3, vector.x, vector.y));
			lantern.getEntity().setFriction(0.99f);
			lantern.getEntity().setMass(10);
			return lantern;
		});
		Placeable.registeredObjects.put("Lantern_1", (g, vector) -> {
			LanternModel lantern = new LanternModel(new EntityLantern(), g, Color.WHITE.cpy().mul(1, 1, 1, 130f / 255), "Core/Entity/Lantern/Lantern.png", "Lantern_1");
			lantern.setLight(new PointLight(g.getLighting(), 200, lantern.getColor(), 6, vector.x, vector.y));
			lantern.getEntity().setFriction(0.99f);
			lantern.getEntity().setMass(10);
			return lantern;
		});

		//Add the spawnable for the main zombie
		GameRegistry.addSpawnable(0, 1, (g) -> {
			float maxModifier = 0.8f;
			
			float gaussRand = -1;
			
			while (gaussRand < 0 || gaussRand > 1) {
				gaussRand = (float) (g.getRandom().nextGaussian()/2.8 + 0.5);
			}
			
			gaussRand = gaussRand * 0.9f + 0.1f;

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
		if (ZombieGame.instance.isCheatModeAllowed) {
			GameRegistry.difficulties.add(new BasicDifficulty("DEBUG", "Debug", 0f, 0.5f, 0.25f, 2f, 2f, 0.01f, 1000, 50));
		}
		//																		overall, wave, health, money, dmg, hlth, start, hostiles
		GameRegistry.difficulties.add(new BasicDifficulty("EASY", 	"Easy", 	0.5f, 	0.5f, 	0.25f, 	2f, 	2f, 	0.01, 	1000, 	50));
		GameRegistry.difficulties.add(new BasicDifficulty("MEDIUM", "Medium",	1f, 	1f, 	0.5f, 	1.5f, 	1.5f, 	75e-4,	750, 	100));
		GameRegistry.difficulties.add(new BasicDifficulty("HARD", 	"Hard", 	1.5f, 	1.5f, 	0.75f, 	1.0f, 	1.0f, 	0.005, 	500, 	150));
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
		ZombieGame.instance.settings.registerInput("Shoot", new InputData(false, Buttons.LEFT));
		ZombieGame.instance.settings.registerInput("Move_Up", new InputData(true, Keys.W));
		ZombieGame.instance.settings.registerInput("Move_Down", new InputData(true, Keys.S));
		ZombieGame.instance.settings.registerInput("Move_Right", new InputData(true, Keys.D));
		ZombieGame.instance.settings.registerInput("Move_Left", new InputData(true, Keys.A));
		ZombieGame.instance.settings.registerInput("Toggle_Flashlight", new InputData(true, Keys.F));
		ZombieGame.instance.settings.registerInput("Buy_Ammo", new InputData(true, Keys.B));
		ZombieGame.instance.settings.registerInput("Reload", new InputData(true, Keys.R));
		ZombieGame.instance.settings.registerInput("Hotbar_1", new InputData(true, Keys.NUM_1));
		ZombieGame.instance.settings.registerInput("Hotbar_2", new InputData(true, Keys.NUM_2));
		ZombieGame.instance.settings.registerInput("Hotbar_3", new InputData(true, Keys.NUM_3));
		ZombieGame.instance.settings.registerInput("Shop_Window", new InputData(true, Keys.P));
		ZombieGame.instance.settings.registerInput("Inventory_Window", new InputData(true, Keys.I));
		ZombieGame.instance.settings.registerInput("Close_Window", new InputData(true, Keys.ESCAPE));
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
