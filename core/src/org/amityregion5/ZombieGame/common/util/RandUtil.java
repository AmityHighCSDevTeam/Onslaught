package org.amityregion5.ZombieGame.common.util;

import java.util.Random;

public class RandUtil {
	public static double boundedGaussian(Random rand, double min, double max, double sdv, double center) {
		double val;
		do {
			val = rand.nextGaussian()*sdv + center;
		} while (val < min || val > max);
		return val;
	}
}
