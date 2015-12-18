package org.amityregion5.ZombieGame.common.weapon.data;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.asset.SoundRegistry;
import org.json.simple.JSONObject;

/**
 * Data to describe a sound
 * 
 * @author sergeys
 *
 */
public class SoundData {
	private String	assetName; //Path to sound file 
	private String trigger; //Trigger that will cause it to play
	private double	pitch; //Pitch to play at
	private double	maxVolume; //The maximum volume to play at

	public static SoundData getSoundData(JSONObject jsonData) {
		//If both path and string are missing dont make a sound object
		if (!jsonData.containsKey("path") && !jsonData.containsKey("trigger")) { return null; }

		//Get assetname and trigger from json
		String assetName = (String) jsonData.get("path");
		String trigger = (String) jsonData.get("trigger");
		//Default pitch and volume are 1
		double pitch = 1;
		double maxVolume = 1;

		//Set pitch if found
		if (jsonData.containsKey("pitch")) {
			pitch = ((Number) jsonData.get("pitch")).doubleValue();
		}
		//Set max volume if found
		if (jsonData.containsKey("maxVolume")) {
			maxVolume = ((Number) jsonData.get("maxVolume")).doubleValue();
		}
		//If the sound has not been loaded previously
		if (SoundRegistry.getSoundsFor(assetName) == null || SoundRegistry.getSoundsFor(null).isEmpty()) {
			//Load the sound
			if (!SoundRegistry.tryRegister(assetName)) {
				//If failed ouput error
				ZombieGame.error("SoundData: failed to load sound: " + assetName);
			}
		}

		//return the object
		return new SoundData(assetName, trigger, pitch, maxVolume);
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {

		JSONObject obj = new JSONObject();
		obj.put("path", assetName);
		obj.put("trigger", trigger);
		obj.put("pitch", pitch);
		obj.put("maxVolume", maxVolume);

		return obj;
	}

	private SoundData(String assetName, String trigger, double pitch, double maxVolume) {
		this.assetName = assetName;
		this.trigger = trigger;
		this.pitch = pitch;
		this.maxVolume = maxVolume;
	}

	public SoundData(String assetName, double pitch, double maxVolume) {
		this(assetName, "", pitch, maxVolume);
	}

	/**
	 * @return the assetName
	 */
	public String getAssetName() {
		return assetName;
	}

	/**
	 * @param assetName
	 *            the assetName to set
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
	 * @param trigger
	 *            the trigger to set
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
	 * @param pitch
	 *            the pitch to set
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
	 * @param maxVolume
	 *            the maxVolume to set
	 */
	public void setMaxVolume(double maxVolume) {
		this.maxVolume = maxVolume;
	}
}
