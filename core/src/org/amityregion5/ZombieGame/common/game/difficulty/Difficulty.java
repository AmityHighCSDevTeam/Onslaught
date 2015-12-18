package org.amityregion5.ZombieGame.common.game.difficulty;

/**
 * An interface for implementing difficulty levels
 * 
 * @author sergeys
 */
public interface Difficulty {

	/**
	 * An ID used for saving and loading difficulties (should be unique)
	 * 
	 * @return the ID
	 */
	String getUniqueID();

	/**
	 * Get a Human Readable name for this difficulty
	 * 
	 * @return the Name
	 */
	String getHumanName();

	/**
	 * Get the overall multiplier for this difficulty
	 * Used for determining the ordering of the difficulties
	 * 
	 * @return the overall multiplier
	 */
	float getOverallMultiplier();

	/**
	 * Get the zombie wave modifier
	 * 
	 * @return a modifier for the rate at which zombies spawn
	 */
	float getZombieWaveModifier();

	/**
	 * Get the zombie health modifier
	 * 
	 * @return a modifier for the health that zombies have
	 */
	float getZombieHealthModifier();

	/**
	 * Get the money multiplier
	 * 
	 * @return a multiplier that effects the money zombies drop
	 */
	float getMoneyMultiplier();

	/**
	 * Get the damage multiplier
	 * 
	 * @return a multiplier that effects zombie damage
	 */
	float getDamageMultiplier();

	/**
	 * Get the health pack drop chance
	 * 
	 * @return the chance for a health pack to drop
	 */
	double getHealthPackChance();

	/**
	 * Get the amount of money that the player should start with
	 * 
	 * @return the amount of money
	 */
	double getStartingMoney();

	/**
	 * Get the maximum amount of hostiles that will be allowed to spawn
	 * 
	 * @return the maximum number of hostiles
	 */
	int getMaxHostiles();

	/**
	 * Does this difficulty allow zombies to spawn
	 * 
	 * @return should zombies spawn
	 */
	boolean doSpawnZombies();
}