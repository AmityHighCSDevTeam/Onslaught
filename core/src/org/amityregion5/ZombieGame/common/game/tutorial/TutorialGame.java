package org.amityregion5.ZombieGame.common.game.tutorial;

import java.util.Iterator;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.window.InventoryWindow;
import org.amityregion5.ZombieGame.client.window.PauseWindow;
import org.amityregion5.ZombieGame.client.window.ShopWindow;
import org.amityregion5.ZombieGame.common.Constants;
import org.amityregion5.ZombieGame.common.entity.EntityZombie;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.difficulty.BasicDifficulty;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.amityregion5.ZombieGame.common.game.model.IParticle;
import org.amityregion5.ZombieGame.common.game.model.entity.ZombieModel;
import org.amityregion5.ZombieGame.common.game.model.particle.TextParticle;
import org.amityregion5.ZombieGame.common.shop.GunPurchaseable;
import org.amityregion5.ZombieGame.common.shop.IPurchaseable;
import org.amityregion5.ZombieGame.common.weapon.WeaponStack;
import org.amityregion5.ZombieGame.common.weapon.data.WeaponData;

import com.badlogic.gdx.graphics.Color;

/**
 * The tutorial game
 * @author sergeys
 *
 */
public class TutorialGame extends Game {

	private int				tutorialPart	= -1; //Current part of tutorial
	private TextParticle	particle		= null; //Current text particle
	private float			yOffset			= 1; //Particle y offset

	public TutorialGame() {
		super(new BasicDifficulty("TUTORIAL", "Tutorial", 0, 0, 0, 0, 0, 0, 0, 0), true, false);
	}

	@Override
	public void tick(float deltaTime) {
		float frameTime = Math.min(deltaTime, 0.25f);

		//Do tutorial parts
		if (tutorialPart == -1) {
			lighting.setAmbientLight(new Color(.35f, .35f, .35f, 1f));

			getSingleplayerPlayer().setMoney(0);
			String moveDirName = ZombieGame.instance.settings.getInput("Move_Up").getName() + ", " + ZombieGame.instance.settings.getInput("Move_Left").getName() + ", "
					+ ZombieGame.instance.settings.getInput("Move_Down").getName() + ", and " + ZombieGame.instance.settings.getInput("Move_Right").getName();

			addParticleToWorld(particle = new TextParticle(0, 1, this, "Use " + moveDirName + " to move."));
			tutorialPart = 0;
		} else if (tutorialPart == 0 && getSingleplayerPlayer().getEntity().getBody().getLinearVelocity().len2() > 0.01) {
			removeParticle(particle);
			particle = new TextParticle(0, 1, this, "Press " + ZombieGame.instance.settings.getInput("Shop_Window").getName() + " to open the store.");
			addParticleToWorld(particle);
			tutorialPart = 1;
		} else if (tutorialPart == 1 && getSingleplayerPlayer().getScreen().getCurrentWindow() instanceof ShopWindow) {
			removeParticle(particle);

			getSingleplayerPlayer().setMoney(ZombieGame.instance.pluginManager.getActivatedWeapons().parallelStream()
					.filter((w) -> w.getName().equalsIgnoreCase("M9")).findFirst().get().getWeaponData(0).getPrice());

			particle = new TextParticle(0, 1, this, "Buy the M9");
			yOffset = -3;
			addParticleToWorld(particle);
			tutorialPart = 2;
		} else if (tutorialPart == 2 && getSingleplayerPlayer().getWeapons().parallelStream().anyMatch((w) -> w.getWeapon().getName().equalsIgnoreCase("M9"))) {
			removeParticle(particle);

			particle = new TextParticle(0, 1, this, "Press " + ZombieGame.instance.settings.getInput("Close_Window").getName() + " to close the window.");
			addParticleToWorld(particle);
			tutorialPart = 3;
		} else if (tutorialPart == 3 && getSingleplayerPlayer().getScreen().getCurrentWindow() == null) {
			removeParticle(particle);

			WeaponData m9Data = ((WeaponData) ZombieGame.instance.pluginManager.getActivatedWeapons().parallelStream()
					.filter((w) -> w.getName().equalsIgnoreCase("M9")).findFirst().get().getWeaponData(0));

			getSingleplayerPlayer().setMoney(m9Data.getAmmoPrice() * m9Data.getMaxAmmo());

			particle = new TextParticle(0, 1, this, "Press " + ZombieGame.instance.settings.getInput("Buy_Ammo").getName() + " to buy ammo.");
			yOffset = 1;
			addParticleToWorld(particle);
			tutorialPart = 4;
		} else if (tutorialPart == 4 && getSingleplayerPlayer().getWeapons().parallelStream().filter((w) -> w.getWeapon().getName().equalsIgnoreCase("M9"))
				.findFirst().get().getTotalAmmo() > 0) {
			removeParticle(particle);

			particle = new TextParticle(0, 1, this, "Press " + ZombieGame.instance.settings.getInput("Reload").getName() + " to reload.");
			addParticleToWorld(particle);
			tutorialPart = 5;
		} else if (tutorialPart == 5 && getSingleplayerPlayer().getWeapons().parallelStream().filter((w) -> w.getWeapon().getName().equalsIgnoreCase("M9"))
				.findFirst().get().getAmmo() > 0) {
			removeParticle(particle);

			particle = new TextParticle(0, 1, this, "Kill the zombie by pressing " + ZombieGame.instance.settings.getInput("Shoot").getName() + " to shoot.");
			addParticleToWorld(particle);

			EntityZombie zom = new EntityZombie(0.15f);
			zom.setMass(100);
			// zom.setSpeed(1f);
			zom.setFriction(0.99f);

			ZombieModel model = new ZombieModel(zom, this, 1);

			model.setSpeed(0.03f);
			model.setAllHealth(6.7f * 3);
			model.setPrizeMoney(0);
			model.setDamage(0);
			model.setRange(zom.getShape().getRadius() * 1.1f);

			addEntityToWorld(model, getSingleplayerPlayer().getEntity().getBody().getWorldCenter().x + 5,
					getSingleplayerPlayer().getEntity().getBody().getWorldCenter().y);

			tutorialPart = 6;
		} else if (tutorialPart == 6 && hostiles == 0) {
			removeParticle(particle);

			particle = new TextParticle(0, 1, this,
					"Select the second hotbar slot by pressing " + ZombieGame.instance.settings.getInput("Hotbar_2").getName() + ".");
			addParticleToWorld(particle);
			tutorialPart = 7;
		} else if (tutorialPart == 7 && getSingleplayerPlayer().getCurrWeapIndex() == 1) {
			removeParticle(particle);

			particle = new TextParticle(0, 1, this,
					"Open your inventory by pressing " + ZombieGame.instance.settings.getInput("Inventory_Window").getName() + ".");
			addParticleToWorld(particle);
			tutorialPart = 8;
		} else if (tutorialPart == 8 && getSingleplayerPlayer().getScreen().getCurrentWindow() instanceof InventoryWindow) {
			removeParticle(particle);

			getSingleplayerPlayer().getWeapons().add(new WeaponStack(
					ZombieGame.instance.pluginManager.getActivatedWeapons().parallelStream().filter((w) -> w.getName().equals("AK47")).findFirst().get()));

			particle = new TextParticle(0, 1, this, "Put the AK47 in your current slot by clicking on it.");
			yOffset = -3;
			addParticleToWorld(particle);
			tutorialPart = 9;
		} else if (tutorialPart == 9 && getSingleplayerPlayer().getCurrentWeapon().getWeapon().getName().equals("AK47")) {
			removeParticle(particle);

			particle = new TextParticle(0, 1, this, "Press " + ZombieGame.instance.settings.getInput("Close_Window").getName() + " to close the window.");
			addParticleToWorld(particle);
			tutorialPart = 10;
		} else if (tutorialPart == 10 && getSingleplayerPlayer().getScreen().getCurrentWindow() == null) {
			removeParticle(particle);

			particle = new TextParticle(0, 1, this,
					"Press " + ZombieGame.instance.settings.getInput("Toggle_Flashlight").getName() + " to toggle your flashlight.");
			yOffset = 1;
			addParticleToWorld(particle);
			tutorialPart = 11;
		} else if (tutorialPart == 11 && !getSingleplayerPlayer().getLight().isActive()) {
			removeParticle(particle);

			lighting.setAmbientLight(new Color(0, 0, 0, 1f));

			particle = new TextParticle(0, 1, this, "The actual game will be dark like this. Turn your flashlight back on.");
			addParticleToWorld(particle);
			tutorialPart = 12;
		} else if (tutorialPart == 12 && getSingleplayerPlayer().getLight().isActive()) {
			removeParticle(particle);

			particle = new TextParticle(0, 1, this,
					"Press " + ZombieGame.instance.settings.getInput("Close_Window").getName() + " to open the pause/quit window.");
			addParticleToWorld(particle);
			tutorialPart = 13;
		} else if (tutorialPart == 13 && getSingleplayerPlayer().getScreen().getCurrentWindow() instanceof PauseWindow) {
			removeParticle(particle);

			yOffset = 0;
			particle = new TextParticle(0, 0, this, "You have completed the tutorial.");
			addParticleToWorld(particle);
			tutorialPart = 14;
		}
		
		if (tutorialPart == 6 && getSingleplayerPlayer().getMoney() <= 0.1) {
			WeaponData m9Data = ((WeaponData) ZombieGame.instance.pluginManager.getActivatedWeapons().parallelStream()
					.filter((w) -> w.getName().equalsIgnoreCase("M9")).findFirst().get().getWeaponData(0));

			getSingleplayerPlayer().setMoney(m9Data.getAmmoPrice() * m9Data.getMaxAmmo());
		}

		if (particle != null) {
			particle.setX(getSingleplayerPlayer().getEntity().getBody().getWorldCenter().x);
			particle.setY(getSingleplayerPlayer().getEntity().getBody().getWorldCenter().y + yOffset);
		}

		//Normal tick method
		accumulator += frameTime;
		while (accumulator >= Constants.TIME_STEP) {

			if (!isPaused) {
				entities.forEach((e) -> e.tick(Constants.TIME_STEP));
				particles.forEach((p) -> p.tick(Constants.TIME_STEP));
				
				world.step(Constants.TIME_STEP, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
			}

			accumulator -= Constants.TIME_STEP;

			{ // Deletion of Entities
				Iterator<IEntityModel<?>> i = entitiesToDelete.iterator();
				if (!world.isLocked()) {
					while (i.hasNext()) {
						IEntityModel<?> b = i.next();
						world.destroyBody(b.getEntity().getBody());
						entities.remove(b);
						i.remove();
						if (b.isHostile()) {
							hostiles--;
						}
						b.dispose();
					}
				}
			}
			{ // Deletion of Particles
				Iterator<IParticle> i = particlesToDelete.iterator();
				if (!world.isLocked()) {
					while (i.hasNext()) {
						IParticle p = i.next();
						particles.remove(p);
						i.remove();
						p.dispose();
					}
				}
			}
			{ // Addition of Entities
				Iterator<IEntityModel<?>> i = entitiesToAdd.iterator();
				if (!world.isLocked()) {
					while (i.hasNext()) {
						IEntityModel<?> b = i.next();
						entities.add(b);
						i.remove();
					}
				}
			}
			{ // Addition of Particles
				Iterator<IParticle> i = particlesToAdd.iterator();
				if (!world.isLocked()) {
					while (i.hasNext()) {
						IParticle p = i.next();
						particles.add(p);
						i.remove();
					}
				}
			}
		}
	}

	@Override
	public boolean isLightingEnabled() {
		return true;
	}
	
	@Override
	public boolean isPurchaseAllowed(IPurchaseable purchaseable) {
		return (purchaseable instanceof GunPurchaseable) && ((GunPurchaseable)purchaseable).getGun().getID().equals("Core_M9");
	}
	
	@Override
	public boolean canSaveGame() {
		return false;
	}
}
