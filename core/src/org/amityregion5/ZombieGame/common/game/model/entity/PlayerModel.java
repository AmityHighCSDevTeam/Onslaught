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
	private EntityPlayer 		entity;
	private double				money			= 1000;
	private double				screenJitter 	= 0;
	private Vector2				mousePos;
	private List<WeaponStack>	weapons;
	private WeaponStack[] 		hotbar;
	private int					currentWeapon	= 0;
	private InGameScreen 		screen;
	private Game				g;
	private Light				light, circleLight;
	private SpriteDrawingLayer	sprite;
	private PlayerExtrasDrawingLayer extras;
	private float health, maxHealth, speed, baseSpeed, baseHealth;
	private boolean shootJustPressed = false;
	//private boolean meleeJustPressed = false;
	private List<SoundPlayingData> 	soundsToPlay;
	private Buff totalBuffs;
	private List<Buff> buffs, temporaryBuffs;
	
	public PlayerModel() {
	}

	public PlayerModel(EntityPlayer entity, Game g, InGameScreen screen, double startMoney, String txtr) {
		this.entity = entity;
		weapons = new ArrayList<WeaponStack>();
		this.g = g;
		this.money = startMoney;
		this.screen = screen;
		baseHealth = 100;
		health = baseHealth;
		maxHealth = baseHealth;
		hotbar = new WeaponStack[3];

		totalBuffs = new Buff();
		buffs = new ArrayList<Buff>();
		temporaryBuffs = new ArrayList<Buff>();

		sprite = new SpriteDrawingLayer(txtr);//"*/Players/**.png"
		extras = new PlayerExtrasDrawingLayer(this);

		soundsToPlay = new ArrayList<SoundPlayingData>();
	}

	public void tick(float delta) {
		if (ZombieGame.instance.settings.getInput("Shoot").isDown() && screen != null && screen.getCurrentWindow() == null) {
			if (weapons.size() > 0) {
				hotbar[currentWeapon].onUse(mousePos, g, this, 15, shootJustPressed);					
			}
			shootJustPressed = false;
		} else {
			shootJustPressed = true;
		}/*
		if (ZombieGame.instance.settings.getInput("Melee").isDown() && screen != null && screen.getCurrentWindow() == null) {
			if (meleeJustPressed) {
				double dir = MathHelper.clampAngleAroundCenter(this
						.getEntity().getBody().getAngle(), MathHelper
						.getDirBetweenPoints(
								this.getEntity().getBody().getPosition(), mousePos), Math
						.toRadians(10));

				dir = MathHelper.fixAngle(dir);

				Vector2 firingPos = this.getEntity().getBody().getWorldCenter();
				Vector2 firingPosVisual = MathHelper.getEndOfLine(this.getEntity().getBody()
						.getWorldCenter(),
						this.getEntity().getShape().getRadius() - 0.01, dir);

				Vector2 bullVector = VectorFactory.createVector(0.25f,
						(float) dir);

				BasicBullet bull = new BasicBullet(g, firingPosVisual, 0, 
						(float)((3+getTotalBuffs().getAdd("meleeDamage"))*getTotalBuffs().getMult("meleeDamage")),
						bullVector, this, 
						Color.WHITE, 1, 0.2f);
				bull.setDir((float) dir);

				g.getActiveBullets().add(bull);

				bullVector = VectorFactory.createVector(200f,
						(float) dir);
				g.getWorld().rayCast(bull, firingPos, bullVector);
				bull.finishRaycast();
			}
			meleeJustPressed = false;
		} else {
			meleeJustPressed = true;
		}*/

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
		if (ZombieGame.instance.settings.getInput("Toggle_Flashlight").isJustDown()) {
			getLight().setActive(!getLight().isActive());
			//getCircleLight().setActive(getLight().isActive());
		}
		if (ZombieGame.instance.settings.getInput("Buy_Ammo").isJustDown()) {
			hotbar[currentWeapon].purchaseAmmo(this);
		}
		if (ZombieGame.instance.settings.getInput("Reload").isJustDown()) {
			hotbar[currentWeapon].reload(g, this);
		}

		BodyHelper.setPointing(entity.getBody(), mousePos, delta, 10);
		getLight().setDirection((float) Math.toDegrees(entity.getBody().getAngle()));

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

		getLight().attachToBody(entity.getBody());
		getCircleLight().attachToBody(entity.getBody());
		//getLight().setPosition(entity.getBody().getWorldCenter());
		//getCircleLight().setPosition(entity.getBody().getWorldCenter());

		if (currentWeapon < getHotbar().length && currentWeapon >= 0) {
			getHotbar()[currentWeapon].tick((float)((delta+getTotalBuffs().getAdd("weaponTime"))*getTotalBuffs().getMult("weaponTime")));
		}
		sprite.getSprite().setOriginCenter();
		screenJitter *= 0.9;
	}


	public void setMousePos(Vector2 mousePos) {
		this.mousePos = mousePos;
	}

	public WeaponStack getCurrentWeapon() {
		return hotbar[currentWeapon];
		/*
		if (currentWeapon < weapons.size() && currentWeapon >= 0) {
			return weapons.get(currentWeapon);
		}
		return new WeaponStack(new NullWeapon());*/
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}


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
		light.remove();
		circleLight.remove();
		entity.dispose();
	}


	@Override
	public float damage(float damage, IEntityModel<?> source, String damageType) {
		damage  = (float) ((damage - getTotalBuffs().getAdd("allArmor")) * getTotalBuffs().getMult("allArmor"));

		if (damageType == DamageTypes.ZOMBIE) {
			damage  = (float) ((damage - getTotalBuffs().getAdd("zombieArmor")) * getTotalBuffs().getMult("zombieArmor"));
		} else if (damageType == DamageTypes.EXPLOSION) {
			damage  = (float) ((damage - getTotalBuffs().getAdd("explosionArmor")) * getTotalBuffs().getMult("explosionArmor"));
		}

		float damageTaken = Math.min(damage, health);
		for (int i = 0; i<damageTaken; i+=5) {
			g.addParticleToWorld(new BloodParticle(
					entity.getBody().getWorldCenter().x - 0.18f + g.getRandom().nextFloat()*0.18f*2,
					entity.getBody().getWorldCenter().y - 0.18f + g.getRandom().nextFloat()*0.18f*2,
					g));
		}
		health -= damageTaken;
		if (health <= 0) {
			g.doPlayerDie(this);
		}
		return damageTaken; 
	}

	public Light getLight() {
		return light;
	}

	public void setLight(Light light) {
		this.light = light;
	}

	public void setCircleLight(Light circleLight) {
		this.circleLight = circleLight;
	}

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

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		baseSpeed = speed;
		this.speed = speed;
	}

	@Override
	public boolean isHostile() {
		return false;
	}

	public WeaponStack[] getHotbar() {
		for (int i = 0; i<hotbar.length; i++) {
			if (hotbar[i] == null) {
				hotbar[i] = new WeaponStack(new NullWeapon());
			}
		}
		return hotbar;
	}

	public int getCurrWeapIndex() {
		return currentWeapon;
	}

	public double getScreenVibrate() {
		return screenJitter;
	}

	public void setScreenVibrate(double screenJitter) {
		this.screenJitter = screenJitter;
	}

	public void playSound(SoundPlayingData sound) {
		soundsToPlay.add(sound);
	}

	public List<SoundPlayingData> getSoundsToPlay() {
		return soundsToPlay;
	}

	public void setHealth(float health) {
		this.health = health;
	}

	public void applyBuff(Buff buff) {
		totalBuffs = totalBuffs.add(buff);
		buffs.add(buff);
		recalculateBuffEffects();
	}
	
	private void recalculateBuffEffects() {
		{//Health
			double newMaxHealth = totalBuffs.getMult("health")*(baseHealth+totalBuffs.getAdd("health"));
			health = (float) (health/maxHealth*newMaxHealth);
			maxHealth = (float) newMaxHealth;
		}
		{//Speed
			double newSpeed = totalBuffs.getMult("speed")*(baseSpeed+totalBuffs.getAdd("speed"));
			speed = (float) newSpeed;
		}
	}

	public Buff getTotalBuffs() {
		return totalBuffs;
	}

	public boolean hasBuff(Buff buff) {
		return buffs.contains(buff);
	}

	public void addTemporaryWeaponBuff() {
		if (hotbar[currentWeapon] != null && hotbar[currentWeapon].getWeaponDataBase() != null
				&& hotbar[currentWeapon].getWeaponDataBase().getBuff() != null) {
			totalBuffs = totalBuffs.add(hotbar[currentWeapon].getWeaponDataBase().getBuff());
			temporaryBuffs.add(hotbar[currentWeapon].getWeaponDataBase().getBuff());
			recalculateBuffEffects();
		}
	}

	public void removeTemporaryWeaponBuff() {
		if (hotbar[currentWeapon] != null && hotbar[currentWeapon].getWeaponDataBase() != null
				&& hotbar[currentWeapon].getWeaponDataBase().getBuff() != null) {
			temporaryBuffs.remove(
					hotbar[currentWeapon]
							.getWeaponDataBase()
							.getBuff());
			totalBuffs = Stream.concat(buffs.parallelStream(), temporaryBuffs.parallelStream())
					.reduce(new Buff(), Buff::sum, Buff::sum);
			recalculateBuffEffects();
		}
	}
	
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
		
		JSONArray weaps = new JSONArray();
		JSONObject hotba = new JSONObject();
		
		for (int i=0; i<weapons.size(); i++) {
			WeaponStack weap = weapons.get(i);
			
			JSONObject w = new JSONObject();
			
			w.put("id", weap.getID());
			w.put("ammo",weap.getAmmo());
			w.put("totalAmmo",weap.getTotalAmmo());
			w.put("cooldown",weap.getCooldown());
			w.put("level",weap.getLevel());
			
			weaps.add(w);
			
			for (int i2=0; i2<hotbar.length; i2++) {
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
	public PlayerModel fromJSON(JSONObject obj, Game g) {
		float x = ((Number)obj.get("x")).floatValue(); //√
		float y = ((Number)obj.get("y")).floatValue(); //√
		float r = ((Number)obj.get("r")).floatValue(); //√
		double money = ((Number)obj.get("money")).doubleValue(); //√
		double screenJitter = ((Number)obj.get("screenJitter")).doubleValue();
		String txtr = (String) obj.get("txtr"); //√
		float m = ((Number)obj.get("m")).floatValue(); //√
		float f = ((Number)obj.get("f")).floatValue(); //√
		float heal = ((Number)obj.get("health")).floatValue();
		JSONArray weaps = (JSONArray) obj.get("weapons");
		JSONArray buffs = (JSONArray) obj.get("buffs");
		JSONObject hotbar = (JSONObject) obj.get("hotbar");
		
		PlayerModel model = new  PlayerModel(new EntityPlayer(), g, null, money, txtr);
		model.getEntity().setFriction(f);
		model.getEntity().setMass(m);
		model.setScreenVibrate(screenJitter);
		for (Object o : buffs) {
			model.applyBuff(Buff.getFromJSON((JSONObject)o));
		}
		model.setHealth(heal);
		for (int i = 0; i<weaps.size(); i++) {
			JSONObject w = (JSONObject)weaps.get(i);
			
			IWeapon iWea = ZombieGame.instance.weaponRegistry.getWeaponFromID((String)w.get("id"));
			
			WeaponStack weap = new WeaponStack(iWea);
			
			weap.setAmmo(((Number)w.get("ammo")).intValue());
			weap.setTotalAmmo(((Number)w.get("totalAmmo")).intValue());
			weap.setCooldown(((Number)w.get("cooldown")).doubleValue());
			weap.setLevel(((Number)w.get("level")).intValue());
			
			model.weapons.add(weap);
			
			if (hotbar.containsKey(i+"")) {
				int i2 = ((Number)hotbar.get(i+"")).intValue();
				model.hotbar[i2] = weap;
			}
		}
		g.addEntityToWorld(model, x, y);
		model.getEntity().getBody().getTransform().setPosition(new Vector2(x,y));
		model.getEntity().getBody().getTransform().setRotation(r);
		
		return model;
	}
	
	public InGameScreen getScreen() {
		return screen;
	}
}