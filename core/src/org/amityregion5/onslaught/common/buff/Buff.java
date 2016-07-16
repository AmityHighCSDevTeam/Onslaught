package org.amityregion5.onslaught.common.buff;

import java.util.HashMap;

/**
 * A class representing any and all buffs
 * 
 * Current Buffs:
 * allArmor
 * zombieArmor
 * explosionArmor
 * health
 * speed
 * zoom
 * 
 * @author sergeys
 *
 */
public class Buff{
	private HashMap<String, Double>	multiplicative; //Buffs that add together through multiplication
	private HashMap<String, Double>	additive; //Buffs that add together through addition

	/**
	 * Add two buffs together
	 * 
	 * @param b1 the first buff
	 * @param b2 the second buff
	 * @return a sum of the two buffs
	 */
	public static Buff sum(Buff b1, Buff b2) {
		Buff newBuff = new Buff(); //Create the new buff

		//Make the hashmaps for the new buff
		HashMap<String, Double> mult = new HashMap<String, Double>();
		HashMap<String, Double> add = new HashMap<String, Double>();

		//Loop through the first buff's multiplicative buffs and add them to the new one
		for (String b1s : b1.getMultiplicative().keySet()) {
			if (mult.containsKey(b1s)) {
				mult.put(b1s, mult.get(b1s) * b1.getMultiplicative().get(b1s));
			} else {
				mult.put(b1s, b1.getMultiplicative().get(b1s));
			}
		}

		//Loop through the second buff's multiplicative buffs and add them to the new one
		for (String b2s : b2.getMultiplicative().keySet()) {
			if (mult.containsKey(b2s)) {
				mult.put(b2s, mult.get(b2s) * b2.getMultiplicative().get(b2s));
			} else {
				mult.put(b2s, b2.getMultiplicative().get(b2s));
			}
		}

		//Loop through the first buff's additive buffs and add them to the new one
		for (String b1a : b1.getAdditive().keySet()) {
			if (add.containsKey(b1a)) {
				add.put(b1a, add.get(b1a) + b1.getAdditive().get(b1a));
			} else {
				add.put(b1a, b1.getAdditive().get(b1a));
			}
		}

		//Loop through the second buff's additive buffs and add them to the new one
		for (String b2a : b2.getAdditive().keySet()) {
			if (add.containsKey(b2a)) {
				add.put(b2a, add.get(b2a) + b2.getAdditive().get(b2a));
			} else {
				add.put(b2a, b2.getAdditive().get(b2a));
			}
		}

		//Set them as the values for the new buff
		newBuff.setAdditive(add);
		newBuff.setMultiplicative(mult);

		//Return the buff
		return newBuff;
	}

	public Buff() {
		multiplicative = new HashMap<String, Double>();
		additive = new HashMap<String, Double>();
	}

	/**
	 * Add this buff to another one (does not change contents of current buff)
	 * 
	 * @param b the other buff
	 * @return the sum of this buff and the other buff
	 */
	public Buff add(Buff b) {
		return sum(this, b);
	}

	private void setAdditive(HashMap<String, Double> additive) {
		this.additive = additive;
	}

	private void setMultiplicative(HashMap<String, Double> multiplicative) {
		this.multiplicative = multiplicative;
	}

	public HashMap<String, Double> getAdditive() {
		return additive;
	}

	public HashMap<String, Double> getMultiplicative() {
		return multiplicative;
	}

	public void addMult(String key, Double val) {
		multiplicative.put(key, multiplicative.getOrDefault(key, 1d) * val);
	}

	public void addAdd(String key, Double val) {
		additive.put(key, additive.getOrDefault(key, 0d) + val);
	}

	public Double getMult(String key) {
		return multiplicative.getOrDefault(key, 1d);
	}

	public Double getAdd(String key) {
		return additive.getOrDefault(key, 0d);
	}
}
