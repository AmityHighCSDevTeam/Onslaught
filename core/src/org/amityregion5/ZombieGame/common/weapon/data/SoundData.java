package org.amityregion5.ZombieGame.common.weapon.data;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.asset.SoundRegistry;
import org.json.simple.JSONObject;

public class SoundData {
	private String assetName, trigger;
	private double pitch;
	private double maxVolume;
	
	public static SoundData getSoundData(JSONObject jsonData) {
		if (!jsonData.containsKey("path") && !jsonData.containsKey("trigger")) {
			return null;
		}
		
		String assetName = (String)jsonData.get("path");
		String trigger = (String)jsonData.get("trigger");
		double pitch = 1;
		double maxVolume = 1;
		
		if (jsonData.containsKey("pitch")) {
			pitch = ((Number)jsonData.get("pitch")).doubleValue();
		}
		if (jsonData.containsKey("maxVolume")) {
			maxVolume = ((Number)jsonData.get("maxVolume")).doubleValue();
		}
		
		if (SoundRegistry.getSoundsFor(assetName) == null || SoundRegistry.getSoundsFor(null).isEmpty()) {
			if (!SoundRegistry.tryRegister(assetName)) {
				ZombieGame.error("SoundData: failed to load sound: " + assetName);
			}
		}
		
		return new SoundData(assetName, trigger, pitch, maxVolume);
	}
	
	private SoundData(String assetName, String trigger, double pitch, double maxVolume) {
		this.assetName = assetName;
		this.trigger = trigger;
		this.pitch = pitch;
		this.maxVolume = maxVolume;
	}

	/**
	 * @return the assetName
	 */
	public String getAssetName() {
		return assetName;
	}

	/**
	 * @param assetName the assetName to set
	 */
	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	/**
	 * @return the trigger
	 */
	public String getTrigger() {
		return trigger;
	}

	/**
	 * @param trigger the trigger to set
	 */
	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}

	/**
	 * @return the pitch
	 */
	public double getPitch() {
		return pitch;
	}

	/**
	 * @param pitch the pitch to set
	 */
	public void setPitch(double pitch) {
		this.pitch = pitch;
	}

	/**
	 * @return the maxVolume
	 */
	public double getMaxVolume() {
		return maxVolume;
	}

	/**
	 * @param maxVolume the maxVolume to set
	 */
	public void setMaxVolume(double maxVolume) {
		this.maxVolume = maxVolume;
	}

}
