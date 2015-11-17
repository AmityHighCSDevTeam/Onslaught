package org.amityregion5.ZombieGame.common.game;

/**
 *
 * @author sergeys
 *
 */

public interface Difficulty {
	
	String getUniqueID();
	String getHumanName();
	
	float getOverallMultiplier();
	float getZombieWaveModifier();
	float getZombieHealthModifier();
	float getMoneyMultiplier();
	float getDamageMultiplier();
	
	double getHealthPackChance();
	double getStartingMoney();
	
	int getMaxHostiles();
}