package org.amityregion5.onslaught.client.settings;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.amityregion5.onslaught.Onslaught;
import org.amityregion5.onslaught.common.game.difficulty.Difficulty;

import java.util.Set;

import com.badlogic.gdx.files.FileHandle;
import com.google.gson.stream.JsonWriter;

/**
 * The settings handler
 * @author sergeys
 *
 */
public class Settings {

	//A hashmap for input settings values
	private LinkedHashMap<String, InputData>	inputSettings;
	private HashMap<String, ArrayList<Double>> top10scores;
	//The master volume
	private double								masterVolume;
	//The master volume
	private double								uiScale;
	//The ammo circle radius
	private double								aRadius;
	//The ammo circle alpha
	private double								aAlpha;
	//Should ammo be automaticcally be bough
	private boolean								autoBuy;

	public Settings() {
		//Create hashmap
		inputSettings = new LinkedHashMap<String, InputData>();
		top10scores = new HashMap<String, ArrayList<Double>>();
		masterVolume = 1;
		uiScale = 1;
		aRadius = 50;
		aAlpha = 0.2;
		autoBuy = false;
	}

	/**
	 * Called to load settings from file
	 */
	public static synchronized Settings load() {
		Onslaught.debug("Settings: Loading");
		//Get the file
		FileHandle settings = Onslaught.instance.settingsFile;
		
		//If it doesnt exist make a new one
		if (!settings.exists()) {
			Onslaught.log("Settings: file does not exist. Making new");
			Settings sett = new Settings();
			save(sett);
			return sett;
		}
		
		Settings sett = Onslaught.instance.gson.fromJson(settings.reader(), Settings.class);
		
		if (sett == null) {
			Onslaught.log("Settings: failed to load settings.");
			sett = new Settings();
			save(sett);
			return sett;			
		}

		Onslaught.log("Settings: loaded");
		
		return sett;
	}

	/**
	 * Save the settings to the file
	 */
	public static synchronized void save(Settings sett) {
		Onslaught.debug("Settings: saving");
		//Get the file
		FileHandle settings = Onslaught.instance.settingsFile;
		
		BufferedWriter writer = new BufferedWriter(settings.writer(false));
		
		Onslaught.instance.gson.toJson(sett, Settings.class, new JsonWriter(writer));
		
		try {
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Onslaught.log("Settings: saved");
	}

	/**
	 * Register an input
	 * 
	 * @param key the name of the input
	 * @param def the Input Data
	 * @return is the current value of the key the input data that was passed in
	 */
	public boolean registerInput(String key, InputData def) {
		return getInputOrDefault(key, def) == def;
	}

	/**
	 * Get the input for a certain key or a default value
	 * 
	 * @param key the key
	 * @param def the default value
	 * @return the input data
	 */
	public synchronized InputData getInputOrDefault(String key, InputData def) {
		if (!inputSettings.containsKey(key)) {
			inputSettings.put(key, def);
		}
		return inputSettings.get(key);
	}

	/**
	 * Get an input data for a key (or null)
	 * 
	 * @param key the key
	 * @return the input data for key (or null)
	 */
	public synchronized InputData getInput(String key) {
		return inputSettings.get(key);
	}

	/**
	 * Set an input
	 * 
	 * @param key the key
	 * @param val the value
	 */
	public void setInput(String key, InputData val) {
		inputSettings.put(key, val);
	}

	/**
	 * Get a set of all entries
	 * 
	 * @return the set of entries
	 */
	public Set<Entry<String, InputData>> getEntries() {
		return inputSettings.entrySet();
	}

	/**
	 * Get the number of values identical to the parameter
	 * 
	 * @param data the data to check
	 * @return the number of identical values
	 */
	public long getSameValues(InputData data) {
		return inputSettings.values().parallelStream().filter((v) -> v.equals(data)).count();
	}

	/**
	 * Get the master volume
	 * 
	 * @return the master volume
	 */
	public double getMasterVolume() {
		return masterVolume;
	}

	/**
	 * Set the master volume
	 * 
	 * @param masterVolume the new master volume
	 */
	public void setMasterVolume(double masterVolume) {
		this.masterVolume = masterVolume;
	}

	/**
	 * @return the uiScale
	 */
	public double getUiScale() {
		return uiScale;
	}

	/**
	 * @param uiScale the uiScale to set
	 */
	public void setUiScale(double uiScale) {
		this.uiScale = uiScale;
	}
	
	public void addScore(Difficulty diff, double score) {
		if (!top10scores.containsKey(diff.getUniqueID())) {
			top10scores.put(diff.getUniqueID(), new ArrayList<Double>());
		}
		ArrayList<Double> arr = top10scores.get(diff.getUniqueID());
		
		arr.add(score);
		
		arr.sort((d1, d2)->(int)(d2*10-d1*10));
		
		for (int i = arr.size()-1; i>10; i--) {
			arr.remove(i);
		}
	}
	
	public List<Double> getTop10ScoresForDiff(Difficulty diff) {
		return top10scores.getOrDefault(diff.getUniqueID(), new ArrayList<Double>());
	}

	public double getARadius() {
		return aRadius;
	}
	
	public void setARadius(double rad) {
		aRadius = rad;
	}

	public double getAAlpha() {
		return aAlpha;
	}
	
	public void setAAlpha(double a) {
		aAlpha = a;
	}
	
	public void setAutoBuy(boolean autoBuy) {
		this.autoBuy = autoBuy;
	}
	
	public boolean isAutoBuy() {
		return autoBuy;
	}
}
