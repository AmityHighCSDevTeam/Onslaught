package org.amityregion5.ZombieGame.common.game.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.asset.SoundPlayingData;
import org.amityregion5.ZombieGame.client.game.HealthBarDrawingLayer;
import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.client.game.PlayerExtrasDrawingLayer;
import org.amityregion5.ZombieGame.client.game.SpriteDrawingLayer;
import org.amityregion5.ZombieGame.client.screen.InGameScreen;
import org.amityregion5.ZombieGame.common.entity.EntityPlayer;
import org.amityregion5.ZombieGame.common.func.Consumer3;
import org.amityregion5.ZombieGame.common.game.DamageTypes;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.buffs.Buff;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.amityregion5.ZombieGame.common.game.model.particle.BloodParticle;
import org.amityregion5.ZombieGame.common.helper.BodyHelper;
import org.amityregion5.ZombieGame.common.weapon.WeaponStack;
import org.amityregion5.ZombieGame.common.weapon.types.IWeapon;
import org.amityregion5.ZombieGame.common.weapon.types.NullWeapon;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.badlogic.gdx.math.Vector2;

import box2dLight.Light;


public class PlayerModel implements IEntityModel<EntityPlayer> {
	private EntityPlayer				entity; //The entity
	private double						money				= 1000; //The money
	private double						screenJitter		= 0; //The screen jitter
	private Vector2						mousePos; //The mouse position
	private List<WeaponStack>			weapons; //The weapons
	private WeaponStack[]				hotbar; //The hotbar
	private int							currentWeapon		= 0; //The current weapon
	private InGameScreen				screen; //The screen
	private Game						g; //The game
	private Light						light, circleLight; //The lights
	private SpriteDrawingLayer			sprite; //The sprite
	private PlayerExtrasDrawingLayer	extras; //The held gun drawing layer
	private float						health, maxHealth, speed, baseSpeed, baseHealth; //Health and speed
	private boolean						shootJustPressed	= false; //Was shoot button down last tick
	private List<SoundPlayingData>		soundsToPlay; //The sounds to play
	private Buff						totalBuffs; //The total sum of all buffs on it
	private List<Buff>					buffs, temporaryBuffs; //The buffs and temporary buffs
	private double score = 0;

	public PlayerModel() {}

	public PlayerModel(EntityPlayer entity, Game g, InGameScreen screen, double startMoney, String txtr) {
		this.entity = entity; //Set values
		weapons = new ArrayList<WeaponStack>();
		this.g = g;
		money = startMoney;
		this.screen = screen;
		baseHealth = 100; //Health = 100
		health = baseHealth;
		maxHealth = baseHealth;
		hotbar = new WeaponStack[3]; //Create arrays

		totalBuffs = new Buff();
		buffs = new ArrayList<Buff>(); //Create arraylists
		temporaryBuffs = new ArrayList<Buff>();

		sprite = new SpriteDrawingLayer(txtr);// "*/Players/**.png"
		extras = new PlayerExtrasDrawingLayer(this);

		soundsToPlay = new ArrayList<SoundPlayingData>();
	}

	@Override
	public void tick(float delta) {
		//If shoot is down and a window is not open
		if (ZombieGame.instance.settings.getInput("Shoot").isDown() && screen != null && screen.getCurrentWindow() == null) {
			//If we have weapons
			if (weapons.size() > 0) {
				//Shoot the weapon
				hotbar[currentWeapon].onUse(mousePos, g, this, 15, shootJustPressed);
				if (ZombieGame.instance.settings.isAutoBuy() && getCurrentWeapon().getTotalAmmo() == 0 && getCurrentWeapon().getAmmo() == 0) {
					hotbar[currentWeapon].purchaseAmmo(this);
					if (getCurrentWeapon().getTotalAmmo() > 0) {
						hotbar[currentWeapon].reload(g, this);
					}
				}
			}
			//Shoot was just pressed
			shootJustPressed = false;
		} else {
			//Shoot will be just pressed when it shoots
			shootJustPressed = true;
		}

		//Movement controls
		{
			if (ZombieGame.instance.settings.getInput("Move_Up").isDown()) {
				entity.getBody().applyForceToCenter(new Vector2(0, getSpeed()), true);
			}
			if (ZombieGame.instance.settings.getInput("Move_Down").isDown()) {
				entity.getBody().applyForceToCenter(new Vector2(0, -getSpeed()), true);
			}
			if (ZombieGame.instance.settings.getInput("Move_Right").isDown()) {
				entity.getBody().applyForceToCenter(new Vector2(getSpeed(), 0), true);
			}
			if (ZombieGame.instance.settings.getInput("Move_Left").isDown()) {
				entity.getBody().applyForceToCenter(new Vector2(-getSpeed(), 0), true);
			}
		}

		//Flashlight toggle
		if (ZombieGame.instance.settings.getInput("Toggle_Flashlight").isJustDown()) {
			getLight().setActive(!getLight().isActive());
		}
		//Buy ammo
		if (ZombieGame.instance.settings.getInput("Buy_Ammo").isJustDown()) {
			hotbar[currentWeapon].purchaseAmmo(this);
		}
		//Reload
		if (ZombieGame.instance.settings.getInput("Reload").isJustDown()) {
			hotbar[currentWeapon].reload(g, this);
		}

		//Point at mouse
		BodyHelper.setPointing(entity.getBody(), mousePos, delta, 10);
		//Set light direction
		getLight().setDirection((float) Math.toDegrees(entity.getBody().getAngle()));

		//Switch hotbar slot
		{
			if (ZombieGame.instance.settings.getInput("Hotbar_1").isJustDown()) {
				removeTemporaryWeaponBuff();
				currentWeapon = 0;
				addTemporaryWeaponBuff();
			}
			if (ZombieGame.instance.settings.getInput("Hotbar_2").isJustDown()) {
				removeTemporaryWeaponBuff();
				currentWeapon = 1;
				addTemporaryWeaponBuff();
			}
			if (ZombieGame.instance.settings.getInput("Hotbar_3").isJustDown()) {
				removeTemporaryWeaponBuff();
				currentWeapon = 2;
				addTemporaryWeaponBuff();
			}
		}

		//Attach lights to the body
		getLight().attachToBody(entity.getBody());
		getCircleLight().attachToBody(entity.getBody());

		//Tick the current weapon
		if (currentWeapon < getHotbar().length && currentWeapon >= 0) {
			getHotbar()[currentWeapon].tick((float) ((delta + getTotalBuffs().getAdd("weaponTime")) * getTotalBuffs().getMult("weaponTime")));
		}
		
		//Set sprite position
		sprite.getSprite().setOriginCenter();
		//Decrease screen jitter
		screenJitter *= 0.9;
	}

	/**
	 * Set the mouse position
	 * 
	 * @param mousePos the position
	 */
	public void setMousePos(Vector2 mousePos) {
		this.mousePos = mousePos;
	}

	/**
	 * Set the current weapon
	 * 
	 * @return the current weapon
	 */
	public WeaponStack getCurrentWeapon() {
		return hotbar[currentWeapon];
	}

	/**
	 * Get the money
	 * 
	 * @return the money
	 */
	public double getMoney() {
		return money;
	}

	/**
	 * Set the money
	 * 
	 * @param money the new money
	 */
	public void setMoney(double money) {
		this.money = money;
	}

	/**
	 * Get all weapons owned
	 * 
	 * @return a list of all weapons
	 */
	public List<WeaponStack> getWeapons() {
		return weapons;
	}

	/**
	 * @return the entity
	 */
	@Override
	public EntityPlayer getEntity() {
		return entity;
	}

	@Override
	public void dispose() {
		light.remove(); //Remove lights
		circleLight.remove();
		entity.dispose(); //Dispose entity
	}

	@Override
	public float damage(float damage, IEntityModel<?> source, String damageType) {
		//Subtract armor, Multiply by armor modifier
		damage = (float) ((damage - getTotalBuffs().getAdd("allArmor")) * getTotalBuffs().getMult("allArmor"));

		//If it is a zombie damage
		if (damageType == DamageTypes.ZOMBIE) {
			//Subtract zombie armor
			damage = (float) ((damage - getTotalBuffs().getAdd("zombieArmor")) * getTotalBuffs().getMult("zombieArmor"));
		} else if (damageType == DamageTypes.EXPLOSION) {
			//If explosion damage
			//Subtract explosion armor
			damage = (float) ((damage - getTotalBuffs().getAdd("explosionArmor")) * getTotalBuffs().getMult("explosionArmor"));
		}

		//Get damage taken
		float damageTaken = Math.min(damage, health);
		
		//Spawn a blood particle for each 5 damage taken
		for (int i = 0; i < damageTaken; i += 5) {
			g.addParticleToWorld(new BloodParticle(entity.getBody().getWorldCenter().x - 0.18f + g.getRandom().nextFloat() * 0.18f * 2,
					entity.getBody().getWorldCenter().y - 0.18f + g.getRandom().nextFloat() * 0.18f * 2, g));
		}
		
		//Subtract health
		health -= damageTaken;
		
		//If no more health
		if (health <= 0) {
			//Kill player
			g.doPlayerDie(this);
		}
		return damageTaken;
	}

	/**
	 * Get the main light
	 * @return the main light
	 */
	public Light getLight() {
		return light;
	}

	/**
	 * Set the main light
	 * 
	 * @param light the new main light
	 */
	public void setLight(Light light) {
		this.light = light;
	}

	/**
	 * Set the circle light
	 * 
	 * @param circleLight the new circle light
	 */
	public void setCircleLight(Light circleLight) {
		this.circleLight = circleLight;
	}

	/**
	 * Get the circle light
	 * 
	 * @return the circle light
	 */
	public Light getCircleLight() {
		return circleLight;
	}

	@Override
	public IDrawingLayer[] getDrawingLayers() {
		return new IDrawingLayer[] {sprite, extras, HealthBarDrawingLayer.instance};
	}

	@Override
	public float getHealth() {
		return health;
	}

	@Override
	public float getMaxHealth() {
		return maxHealth;
	}

	/**
	 * Get the speed
	 * 
	 * @return the speed
	 */
	public float getSpeed() {
		return speed;
	}

	/**
	 * Set the speed
	 * 
	 * @param speed the speed
	 */
	public void setSpeed(float speed) {
		baseSpeed = speed;
		this.speed = speed;
	}

	@Override
	public boolean isHostile() {
		return false;
	}

	/**
	 * Get the hotbar
	 * 
	 * @return the hotbar
	 */
	public WeaponStack[] getHotbar() {
		//If hotbar contains any nulls replace them with NullWeapons
		for (int i = 0; i < hotbar.length; i++) {
			if (hotbar[i] == null) {
				hotbar[i] = new WeaponStack(new NullWeapon());
			}
		}
		return hotbar;
	}

	/**
	 * Get the current weapon index
	 * 
	 * @return the hotbar slot that is selected
	 */
	public int getCurrWeapIndex() {
		return currentWeapon;
	}

	/**
	 * Get the amount of screen vibrate
	 * 
	 * @return the amount of screen vibrate
	 */
	public double getScreenVibrate() {
		return screenJitter;
	}

	/**
	 * Set the amount of screen vibrate
	 * 
	 * @param screenJitter the amount of screen vibrate
	 */
	public void setScreenVibrate(double screenJitter) {
		this.screenJitter = screenJitter;
	}

	/**
	 * Play a sound
	 * 
	 * @param sound the sound to play
	 */
	public void playSound(SoundPlayingData sound) {
		soundsToPlay.add(sound);
	}

	/**
	 * Get the sounds that will be played by the screen
	 * 
	 * @return the list of sounds
	 */
	public List<SoundPlayingData> getSoundsToPlay() {
		return soundsToPlay;
	}

	/**
	 * Set the health
	 * 
	 * @param health the health
	 */
	public void setHealth(float health) {
		this.health = health;
	}

	/**
	 * Apply a permanent buff
	 * Will automatically calculate effects
	 * 
	 * @param buff the buff to add
	 */
	public void applyBuff(Buff buff) {
		totalBuffs = totalBuffs.add(buff);
		buffs.add(buff);
		recalculateBuffEffects();
	}

	/**
	 * Calculate buff effects
	 */
	public void recalculateBuffEffects() {
		{// Health
			double newMaxHealth = totalBuffs.getMult("health") * (baseHealth + totalBuffs.getAdd("health"));
			health = (float) (health / maxHealth * newMaxHealth);
			maxHealth = (float) newMaxHealth;
		}
		{// Speed
			double newSpeed = totalBuffs.getMult("speed") * (baseSpeed + totalBuffs.getAdd("speed"));
			speed = (float) newSpeed;
		}
	}

	/**
	 * Get sum of all the buffs
	 * 
	 * @return the total buff
	 */
	public Buff getTotalBuffs() {
		return totalBuffs;
	}

	/**
	 * Does this player have this buff
	 * 
	 * @param buff the buff
	 * @return does it have the buff
	 */
	public boolean hasBuff(Buff buff) {
		return buffs.contains(buff);
	}

	/**
	 * Add the temporary weapon buff
	 */
	public void addTemporaryWeaponBuff() {
		if (hotbar[currentWeapon] != null && hotbar[currentWeapon].getWeaponDataBase() != null && hotbar[currentWeapon].getWeaponDataBase().getBuff() != null) {
			totalBuffs = totalBuffs.add(hotbar[currentWeapon].getWeaponDataBase().getBuff());
			temporaryBuffs.add(hotbar[currentWeapon].getWeaponDataBase().getBuff());
			recalculateBuffEffects();
		}
	}

	/**
	 * Remove the temporary weapon buff
	 */
	public void removeTemporaryWeaponBuff() {
		if (hotbar[currentWeapon] != null && hotbar[currentWeapon].getWeaponDataBase() != null && hotbar[currentWeapon].getWeaponDataBase().getBuff() != null) {
			temporaryBuffs.remove(hotbar[currentWeapon].getWeaponDataBase().getBuff());
			totalBuffs = Stream.concat(buffs.parallelStream(), temporaryBuffs.parallelStream()).reduce(new Buff(), Buff::sum, Buff::sum);
			recalculateBuffEffects();
		}
	}

	/**
	 * Set the screen
	 * 
	 * @param screen the screen
	 */
	public void setScreen(InGameScreen screen) {
		this.screen = screen;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject convertToJSONObject() {
		JSONObject obj = new JSONObject();

		obj.put("x", entity.getBody().getWorldCenter().x);
		obj.put("y", entity.getBody().getWorldCenter().y);
		obj.put("r", entity.getBody().getTransform().getRotation());
		obj.put("money", money);
		obj.put("screenJitter", screenJitter);
		obj.put("txtr", sprite.getTxtrName());
		obj.put("m", entity.getMassData().mass);
		obj.put("f", entity.getFriction());
		obj.put("health", health);
		obj.put("score", score);

		JSONArray weaps = new JSONArray();
		JSONObject hotba = new JSONObject();

		for (int i = 0; i < weapons.size(); i++) {
			WeaponStack weap = weapons.get(i);

			JSONObject w = new JSONObject();

			w.put("id", weap.getID());
			w.put("ammo", weap.getAmmo());
			w.put("totalAmmo", weap.getTotalAmmo());
			w.put("cooldown", weap.getPostFire());
			w.put("level", weap.getLevel());

			weaps.add(w);

			for (int i2 = 0; i2 < hotbar.length; i2++) {
				if (hotbar[i2] == weap) {
					hotba.put(i, i2);
				}
			}
		}

		obj.put("weapons", weaps);
		obj.put("hotbar", hotba);

		JSONArray bufs = new JSONArray();

		for (Buff b : buffs) {
			bufs.add(b.toJSON());
		}

		obj.put("buffs", bufs);

		return obj;
	}

	@Override
	public PlayerModel fromJSON(JSONObject obj, Game g, Consumer3<String, String, Boolean> addErrorConsumer) {
		float x = ((Number) obj.get("x")).floatValue(); // √
		float y = ((Number) obj.get("y")).floatValue(); // √
		float r = ((Number) obj.get("r")).floatValue(); // √
		double money = ((Number) obj.get("money")).doubleValue(); // √
		double score = ((Number) obj.get("score")).doubleValue(); // √
		double screenJitter = ((Number) obj.get("screenJitter")).doubleValue();
		String txtr = (String) obj.get("txtr"); // √
		float m = ((Number) obj.get("m")).floatValue(); // √
		float f = ((Number) obj.get("f")).floatValue(); // √
		float heal = ((Number) obj.get("health")).floatValue();
		JSONArray weaps = (JSONArray) obj.get("weapons");
		JSONArray buffs = (JSONArray) obj.get("buffs");
		JSONObject hotbar = (JSONObject) obj.get("hotbar");

		PlayerModel model = new PlayerModel(new EntityPlayer(), g, null, money, txtr);
		model.getEntity().setFriction(f);
		model.getEntity().setMass(m);
		model.setScreenVibrate(screenJitter);
		model.addScore(score);
		for (Object o : buffs) {
			model.applyBuff(Buff.getFromJSON((JSONObject) o));
		}
		model.setHealth(heal);
		for (int i = 0; i < weaps.size(); i++) {
			JSONObject w = (JSONObject) weaps.get(i);

			IWeapon iWea = ZombieGame.instance.weaponRegistry.getWeaponFromID((String) w.get("id"));
			
			if (iWea == null) {
				addErrorConsumer.run("Weapons not found:", (String) w.get("id"), true);
				continue;
			}

			WeaponStack weap = new WeaponStack(iWea);

			weap.setAmmo(((Number) w.get("ammo")).intValue());
			weap.setTotalAmmo(((Number) w.get("totalAmmo")).intValue());
			weap.setPostFire(((Number) w.get("cooldown")).doubleValue());
			weap.setLevel(((Number) w.get("level")).intValue());

			model.weapons.add(weap);

			if (hotbar.containsKey(i + "")) {
				int i2 = ((Number) hotbar.get(i + "")).intValue();
				model.hotbar[i2] = weap;
			}
		}
		g.addEntityToWorld(model, x, y);
		model.getEntity().getBody().getTransform().setPosition(new Vector2(x, y));
		model.getEntity().getBody().getTransform().setRotation(r);

		return model;
	}

	/**
	 * Get the player's screen
	 * 
	 * @return the screen
	 */
	public InGameScreen getScreen() {
		return screen;
	}
	
	/**
	 * Increase the score by a specific number
	 * @param amount
	 */
	public void addScore(double amount) {
		score += amount;
	}
	public double getScore() {
		return score;
	}
}