package org.amityregion5.onslaught.common.weapon.data;

import java.lang.reflect.Type;

import org.amityregion5.onslaught.Onslaught;
import org.amityregion5.onslaught.client.asset.SoundRegistry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

/**
 * Data to describe a sound
 * 
 * @author sergeys
 *
 */
public class SoundData {
	@SerializedName(value="path") private String	assetName; //Path to sound file 
	private String trigger; //Trigger that will cause it to play
	private double	pitch = 1; //Pitch to play at
	private double	maxVolume = 1; //The maximum volume to play at

	public void postRead() {
		if (!SoundRegistry.tryRegister(assetName)) {
			//If failed ouput error
			Onslaught.error("SoundData: failed to load sound: " + assetName);
		}
	}

	public SoundData(String assetName, double pitch, double maxVolume) {
		this(assetName, "", pitch, maxVolume);
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
	public static class Deserializor implements JsonDeserializer<SoundData> {
		@Override
		public SoundData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject o = json.getAsJsonObject();
			String path = o.get("path").getAsString();
			String trigger = o.get("trigger").getAsString();
			double pitch = o.get("pitch").getAsDouble();
			double max = o.get("maxVolume").getAsDouble();
			
			SoundData data = new SoundData(path, trigger, pitch, max);
			
			data.postRead();
			
			return data;
		}
	}
}
