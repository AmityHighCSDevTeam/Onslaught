package org.amityregion5.ZombieGame.common.game;

import java.util.Arrays;

/**
 *
 * @author sergeys
 *
 */
public enum Difficulty {
	EASY("Easy", 0.5f), MEDIUM("Medium", 1f), HARD("Hard", 1.5f), INSANE(
			"Insane", 2f);

	// Used for inverting the difficulty
	public static final float	diffInvertNum	= 2.5f;

	// Name on buttons
	private String				locName;
	// Difficulty number
	private float				difficultyMultiplier;

	/**
	 * @param locName
	 * @param difficultyMultiplier
	 */
	private Difficulty(String locName, float difficultyMultiplier) {
		this.locName = locName;
		this.difficultyMultiplier = difficultyMultiplier;
	}

	/**
	 *
	 * @return the difficulty multiplier
	 */
	public float getDifficultyMultiplier() {
		return difficultyMultiplier;
	}

	/**
	 *
	 * @return the name
	 */
	public String getLocName() {
		return locName;
	}

	/**
	 *
	 * @return a version of Difficulty.values() that has been sorted by
	 *         difficulty
	 */
	public static Difficulty[] getSortedArray() {
		Difficulty[] result = values();

		// Java 1.8 feature
		// Sorts using multiple threads
		Arrays.parallelSort(
				result,
				(o1, o2) -> (int) (100 * o1.difficultyMultiplier - 100 * o2.difficultyMultiplier));

		return result;
	}
	
	public float getHealthPackChance() {
		return (diffInvertNum - difficultyMultiplier)/200;
	}
}
