package org.amityregion5.ZombieGame.common.game;

public class BasicDifficulty implements Difficulty {
	
	private String id, name;
	private float overallMult, waveMod, healthMod, moneyMod, damageMod;
	private double healthChance, startMoney;
	private int maxHostiles;
	
	/**
	 * @param id
	 * @param name
	 * @param overallMult
	 * @param waveMod
	 * @param healthMod
	 * @param moneyMod
	 * @param damageMod
	 * @param healthChance
	 * @param startMoney
	 * @param maxHostiles
	 */
	public BasicDifficulty(String id, String name, float overallMult, float waveMod, float healthMod, float moneyMod,
			float damageMod, double healthChance, double startMoney, int maxHostiles) {
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
}
