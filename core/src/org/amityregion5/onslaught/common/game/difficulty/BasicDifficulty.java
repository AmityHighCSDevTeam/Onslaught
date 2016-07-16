package org.amityregion5.onslaught.common.game.difficulty;

/**
 * A basic difficulty implementation
 * 
 * @author sergeys
 */
public class BasicDifficulty implements Difficulty {
	
	public static final Difficulty ERROR_DIFFICULTY = new BasicDifficulty("ERROR", "ERROR DIFFICULTY", -1, -1, -1, -1, -1, -1, -1, -1);

	private String	id, name; //All the variables
	private float	overallMult, waveMod, healthMod, moneyMod, damageMod;
	private double	healthChance, startMoney;
	private int		maxHostiles;

	/**
	 * Create a basic difficulty
	 * 
	 * @param id the difficulty's ID
	 * @param name the difficulty's Name
	 * @param overallMult the difficulty's overall multiplier
	 * @param waveMod the wave modifier
	 * @param healthMod the health modifier
	 * @param moneyMod the money modifier
	 * @param damageMod the damage modifier
	 * @param healthChance the health pack chance
	 * @param startMoney the starting money
	 * @param maxHostiles the maximum number of hostiles
	 */
	public BasicDifficulty(String id, String name, float overallMult, float waveMod, float healthMod, float moneyMod, float damageMod, double healthChance,
			double startMoney, int maxHostiles) {
		this.id = id;
		this.name = name;
		this.overallMult = overallMult;
		this.waveMod = waveMod;
		this.healthMod = healthMod;
		this.moneyMod = moneyMod;
		this.damageMod = damageMod;
		this.healthChance = healthChance;
		this.startMoney = startMoney;
		this.maxHostiles = maxHostiles;
	}

	@Override
	public String getUniqueID() {
		return id;
	}

	@Override
	public String getHumanName() {
		return name;
	}

	@Override
	public float getOverallMultiplier() {
		return overallMult;
	}

	@Override
	public float getZombieWaveModifier() {
		return waveMod;
	}

	@Override
	public float getZombieHealthModifier() {
		return healthMod;
	}

	@Override
	public float getMoneyMultiplier() {
		return moneyMod;
	}

	@Override
	public float getDamageMultiplier() {
		return damageMod;
	}

	@Override
	public double getHealthPackChance() {
		return healthChance;
	}

	@Override
	public double getStartingMoney() {
		return startMoney;
	}

	@Override
	public int getMaxHostiles() {
		return maxHostiles;
	}

	@Override
	public boolean doSpawnZombies() {
		return !id.equals("DEBUG");
	}
}
