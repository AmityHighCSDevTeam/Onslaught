package org.amityregion5.onslaught.common.game.model.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import org.amityregion5.onslaught.Onslaught;
import org.amityregion5.onslaught.client.asset.SoundPlayingData;
import org.amityregion5.onslaught.client.game.HealthBarDrawingLayer;
import org.amityregion5.onslaught.client.game.IDrawingLayer;
import org.amityregion5.onslaught.client.game.PlayerExtrasDrawingLayer;
import org.amityregion5.onslaught.client.game.SpriteDrawingLayer;
import org.amityregion5.onslaught.client.screen.InGameScreen;
import org.amityregion5.onslaught.common.buff.Buff;
import org.amityregion5.onslaught.common.entity.EntityPlayer;
import org.amityregion5.onslaught.common.game.DamageTypes;
import org.amityregion5.onslaught.common.game.Game;
import org.amityregion5.onslaught.common.game.model.IEntityModel;
import org.amityregion5.onslaught.common.game.model.particle.BloodParticle;
import org.amityregion5.onslaught.common.helper.BodyHelper;
import org.amityregion5.onslaught.common.util.MapUtil;
import org.amityregion5.onslaught.common.weapon.WeaponStack;
import org.amityregion5.onslaught.common.weapon.types.NullWeapon;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import box2dLight.Light;


public class PlayerModel implements IEntityModel<EntityPlayer> {
	private List<WeaponStack>			weapons; //The weapons
	private float						health; //Health and speed
	private HashMap<String, JsonElement>		data = new HashMap<String, JsonElement>();
	private Buff						totalBuffs; //The total sum of all buffs on it
	private List<Buff>					buffs; //The buffs and temporary buffs
	
	private transient List<Buff> temporaryBuffs;
	private transient WeaponStack[]				hotbar; //The hotbar
	private transient EntityPlayer				entity; //The entity
	private transient float maxHealth, speed;
	private transient float baseSpeed, baseHealth = 100;
	private transient double						screenJitter		= 0; //The screen jitter
	private transient Vector2						mousePos; //The mouse position
	private transient int							currentWeapon		= 0; //The current weapon
	private transient InGameScreen				screen; //The screen
	private transient Game						g; //The game
	private transient Light						light, circleLight; //The lights
	private transient SpriteDrawingLayer			sprite; //The sprite
	private transient PlayerExtrasDrawingLayer	extras; //The held gun drawing layer
	private transient boolean						shootJustPressed	= false; //Was shoot button down last tick
	private transient List<SoundPlayingData>		soundsToPlay; //The sounds to play

	public PlayerModel() {}

	public PlayerModel(EntityPlayer entity, Game g, InGameScreen screen, double startMoney, String txtr) {
		this.entity = entity; //Set values
		weapons = new ArrayList<WeaponStack>();
		this.g = g;
		setMoney(startMoney);
		setHealth(baseHealth);
		maxHealth = baseHealth;
		this.screen = screen;
		hotbar = new WeaponStack[3]; //Create arrays
		
		setScore(0);

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
		if (Onslaught.instance.settings.getInput("Shoot").isDown() && screen != null && screen.getCurrentWindow() == null) {
			//If we have weapons
			if (weapons.size() > 0) {
				//Shoot the weapon
				hotbar[currentWeapon].onUse(mousePos, g, this, 15, shootJustPressed);
				if (Onslaught.instance.settings.isAutoBuy() && getCurrentWeapon().getTotalAmmo() == 0 && getCurrentWeapon().getAmmo() == 0) {
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
			if (Onslaught.instance.settings.getInput("Move_Up").isDown()) {
				entity.getBody().applyForceToCenter(new Vector2(0, getSpeed()), true);
			}
			if (Onslaught.instance.settings.getInput("Move_Down").isDown()) {
				entity.getBody().applyForceToCenter(new Vector2(0, -getSpeed()), true);
			}
			if (Onslaught.instance.settings.getInput("Move_Right").isDown()) {
				entity.getBody().applyForceToCenter(new Vector2(getSpeed(), 0), true);
			}
			if (Onslaught.instance.settings.getInput("Move_Left").isDown()) {
				entity.getBody().applyForceToCenter(new Vector2(-getSpeed(), 0), true);
			}
		}

		//Flashlight toggle
		if (Onslaught.instance.settings.getInput("Toggle_Flashlight").isJustDown()) {
			getLight().setActive(!getLight().isActive());
		}
		//Buy ammo
		if (Onslaught.instance.settings.getInput("Buy_Ammo").isJustDown()) {
			hotbar[currentWeapon].purchaseAmmo(this);
		}
		//Reload
		if (Onslaught.instance.settings.getInput("Reload").isJustDown()) {
			hotbar[currentWeapon].reload(g, this);
		}

		//Point at mouse
		BodyHelper.setPointing(entity.getBody(), mousePos, delta, 10);
		//Set light direction
		getLight().setDirection((float) Math.toDegrees(entity.getBody().getAngle()));

		//Switch hotbar slot
		{
			if (Onslaught.instance.settings.getInput("Hotbar_1").isJustDown()) {
				removeTemporaryWeaponBuff();
				currentWeapon = 0;
				addTemporaryWeaponBuff();
			}
			if (Onslaught.instance.settings.getInput("Hotbar_2").isJustDown()) {
				removeTemporaryWeaponBuff();
				currentWeapon = 1;
				addTemporaryWeaponBuff();
			}
			if (Onslaught.instance.settings.getInput("Hotbar_3").isJustDown()) {
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
	
	public void setHotbarSlot(int i) {
		removeTemporaryWeaponBuff();
		currentWeapon = i%hotbar.length;
		if (currentWeapon < 0) {
			currentWeapon += hotbar.length;
		}
		addTemporaryWeaponBuff();
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
		return data.get("money").getAsDouble();
	}

	/**
	 * Set the money
	 * 
	 * @param money the new money
	 */
	public void setMoney(double money) {
		data.put("money", new JsonPrimitive(money));
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
			BloodParticle.addBloodToWorld(entity.getBody().getWorldCenter().x - 0.18f + g.getRandom().nextFloat() * 0.18f * 2,
					entity.getBody().getWorldCenter().y - 0.18f + g.getRandom().nextFloat() * 0.18f * 2, g);
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
	
	@Override
	public void read(JsonObject obj) {
		data = MapUtil.convertToHashMap(obj.entrySet());
	}
	
	@Override
	public void doPostDeserialize(Game game) {
		entity = new EntityPlayer();
		weapons = Onslaught.instance.gson.fromJson(data.get("weapons"), new TypeToken<ArrayList<WeaponStack>>(){}.getType());
		g = game;
		maxHealth = baseHealth;
		totalBuffs = new Buff();
		List<Buff> tempBuffs = Onslaught.instance.gson.fromJson(data.get("buffs"), new TypeToken<ArrayList<Buff>>(){}.getType());
		tempBuffs.forEach((b)->applyBuff(b));
		
		setHealth(getMaxHealth() * data.get("healthFraction").getAsFloat());
		
		hotbar = new WeaponStack[3]; //Create arrays
		
		int[] hotbarIndicies = Onslaught.instance.gson.fromJson(data.get("hotbarIndicies"), int[].class);
		for (int i=0;i<hotbar.length; i++) {
			if (hotbarIndicies[i] != -1) {
				hotbar[i] = weapons.get(hotbarIndicies[i]);
			}
		}

		temporaryBuffs = new ArrayList<Buff>();

		sprite = new SpriteDrawingLayer(data.get("txtr").getAsString());// "*/Players/**.png"
		extras = new PlayerExtrasDrawingLayer(this);

		soundsToPlay = new ArrayList<SoundPlayingData>();
		
		game.addEntityToWorld(this, data.get("x").getAsFloat(), data.get("y").getAsFloat());
		entity.getBody().getTransform().setPosition(new Vector2(data.get("x").getAsFloat(), data.get("y").getAsFloat()));
		entity.getBody().getTransform().setRotation(data.get("r").getAsFloat());
		entity.getBody().setLinearVelocity(data.get("vx").getAsFloat(), data.get("vy").getAsFloat());
		
		data.remove("x");
		data.remove("y");
		data.remove("r");
		data.remove("vx");
		data.remove("vy");
		data.remove("healthFraction");
		data.remove("weapons");
		data.remove("txtr");
		data.remove("hotbarIndicies");
		data.remove("buffs");
	}
	
	@Override
	public void write(JsonObject obj) {
		MapUtil.addMapToJson(obj, data);
		obj.addProperty("x", entity.getBody().getWorldCenter().x);
		obj.addProperty("y", entity.getBody().getWorldCenter().y);
		obj.addProperty("r", entity.getBody().getTransform().getRotation());
		obj.addProperty("vx", entity.getBody().getLinearVelocity().x);
		obj.addProperty("vy", entity.getBody().getLinearVelocity().y);
		obj.addProperty("healthFraction", health/maxHealth);
		obj.add("weapons", Onslaught.instance.gson.toJsonTree(weapons));
		
		obj.addProperty("txtr", sprite.getTxtrName());
		
		int[] indicies = new int[hotbar.length];
		for (int i = 0; i<hotbar.length; i++) {
			if (!(hotbar[i] == null || hotbar[i].getWeapon() instanceof NullWeapon)) {
				indicies[i] = weapons.indexOf(hotbar[i]);
			} else {
				indicies[i] = -1;
			}
		}
		obj.add("hotbarIndicies", Onslaught.instance.gson.toJsonTree(indicies));
		
		obj.add("buffs", Onslaught.instance.gson.toJsonTree(buffs));
	}

	/**
	 * Get the player's screen
	 * 
	 * @return the screen
	 */
	public InGameScreen getScreen() {
		return screen;
	}
	
	private void setScore(double score) {
		data.put("score", new JsonPrimitive(score));
	}
	
	/**
	 * Increase the score by a specific number
	 * @param amount
	 */
	public void addScore(double amount) {
		setScore(getScore() + amount);
	}
	public double getScore() {
		return data.get("score").getAsDouble();
	}
	
	public HashMap<String, JsonElement> getData() {
		return data;
	}
}