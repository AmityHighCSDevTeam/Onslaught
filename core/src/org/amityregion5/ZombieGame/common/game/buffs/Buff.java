package org.amityregion5.ZombieGame.common.game.buffs;

import java.util.HashMap;

public class Buff {
	/*
	 * Done:
	 * health, speed, bulletDamage, explodeDamage, allArmor, zombieArmor, explosionArmor, weaponTime
	 * 
	 */
	private HashMap<String, Double> multiplicative;
	private HashMap<String, Double> additive;
	
	public static Buff sum(Buff b1, Buff b2) {
		Buff newBuff = new Buff();
		
		HashMap<String, Double> mult = new HashMap<String, Double>();
		HashMap<String, Double> add = new HashMap<String, Double>();
		
		for (String b1s : b1.getMultiplicative().keySet()) {
			if (mult.containsKey(b1s)) {
				mult.put(b1s, mult.get(b1s)*b1.getMultiplicative().get(b1s));
			} else {
				mult.put(b1s, b1.getMultiplicative().get(b1s));
			}
		}
		
		for (String b2s : b2.getMultiplicative().keySet()) {
			if (mult.containsKey(b2s)) {
				mult.put(b2s, mult.get(b2s)*b2.getMultiplicative().get(b2s));
			} else {
				mult.put(b2s, b2.getMultiplicative().get(b2s));
			}
		}
		
		for (String b1a : b1.getMultiplicative().keySet()) {
			if (add.containsKey(b1a)) {
				add.put(b1a, add.get(b1a)+b1.getMultiplicative().get(b1a));
			} else {
				add.put(b1a, b1.getMultiplicative().get(b1a));
			}
		}
		
		for (String b2a : b2.getMultiplicative().keySet()) {
			if (add.containsKey(b2a)) {
				add.put(b2a, add.get(b2a)+b2.getMultiplicative().get(b2a));
			} else {
				add.put(b2a, b2.getMultiplicative().get(b2a));
			}
		}
		
		newBuff.setAdditive(add);
		newBuff.setMultiplicative(mult);
		
		return newBuff;
	}
	
	public Buff() {
		multiplicative = new HashMap<String, Double>();
		additive = new HashMap<String, Double>();
	}
	
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
		multiplicative.put(key, multiplicative.getOrDefault(key, 1d)*val);
	}
	
	public void addAdd(String key, Double val) {
		additive.put(key, additive.getOrDefault(key, 0d)*val);
	}
	
	public Double getMult(String key) {
		return multiplicative.getOrDefault(key, 1d);
	}
	
	public Double getAdd(String key) {
		return additive.getOrDefault(key, 0d);
	}
}
