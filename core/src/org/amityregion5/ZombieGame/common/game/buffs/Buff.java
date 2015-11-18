package org.amityregion5.ZombieGame.common.game.buffs;

import java.util.HashMap;

import org.json.simple.JSONObject;

public class Buff {
	/*
	 * Done:
	 * health, speed, bulletDamage, explodeDamage, allArmor, zombieArmor, explosionArmor, weaponTime
	 */
	private HashMap<String, Double>	multiplicative;
	private HashMap<String, Double>	additive;

	public static Buff sum(Buff b1, Buff b2) {
		Buff newBuff = new Buff();

		HashMap<String, Double> mult = new HashMap<String, Double>();
		HashMap<String, Double> add = new HashMap<String, Double>();

		for (String b1s : b1.getMultiplicative().keySet()) {
			if (mult.containsKey(b1s)) {
				mult.put(b1s, mult.get(b1s) * b1.getMultiplicative().get(b1s));
			} else {
				mult.put(b1s, b1.getMultiplicative().get(b1s));
			}
		}

		for (String b2s : b2.getMultiplicative().keySet()) {
			if (mult.containsKey(b2s)) {
				mult.put(b2s, mult.get(b2s) * b2.getMultiplicative().get(b2s));
			} else {
				mult.put(b2s, b2.getMultiplicative().get(b2s));
			}
		}

		for (String b1a : b1.getAdditive().keySet()) {
			if (add.containsKey(b1a)) {
				add.put(b1a, add.get(b1a) + b1.getAdditive().get(b1a));
			} else {
				add.put(b1a, b1.getAdditive().get(b1a));
			}
		}

		for (String b2a : b2.getAdditive().keySet()) {
			if (add.containsKey(b2a)) {
				add.put(b2a, add.get(b2a) + b2.getAdditive().get(b2a));
			} else {
				add.put(b2a, b2.getAdditive().get(b2a));
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

	public static Buff getFromJSON(JSONObject json) {
		Buff buff = new Buff();

		if (json.containsKey("add")) {
			for (Object o : ((JSONObject) json.get("add")).keySet()) {
				String key = (String) o;
				Double val = ((Number) ((JSONObject) json.get("add")).get(o)).doubleValue();
				buff.addAdd(key, val);
			}
		}
		if (json.containsKey("mult")) {
			for (Object o : ((JSONObject) json.get("mult")).keySet()) {
				String key = (String) o;
				Double val = ((Number) ((JSONObject) json.get("mult")).get(o)).doubleValue();
				buff.addMult(key, val);
			}
		}

		return buff;
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject jo = new JSONObject();

		JSONObject add = new JSONObject();
		JSONObject mult = new JSONObject();

		for (String a : additive.keySet()) {
			add.put(a, additive.get(a));
		}

		for (String m : multiplicative.keySet()) {
			add.put(m, multiplicative.get(m));
		}

		jo.put("add", add);
		jo.put("mult", mult);

		return jo;
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
		multiplicative.put(key, multiplicative.getOrDefault(key, 1d) * val);
	}

	public void addAdd(String key, Double val) {
		additive.put(key, additive.getOrDefault(key, 0d) * val);
	}

	public Double getMult(String key) {
		return multiplicative.getOrDefault(key, 1d);
	}

	public Double getAdd(String key) {
		return additive.getOrDefault(key, 0d);
	}
}
