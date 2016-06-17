package org.amityregion5.onslaught.client.settings;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.amityregion5.onslaught.Onslaught;
import org.amityregion5.onslaught.common.game.difficulty.Difficulty;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * The settings handler
 * @author sergeys
 *
 */
public class Settings {

	//The JSON parser
	private static JSONParser					parser	= new JSONParser();
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
	//Should the game automatically pause in single player
	private boolean								autoPauseInSP;
	//The hotbar scrolling sensitivity
	private double 								hotbarScrollSensitivity;

	public Settings() {
		//Create hashmap
		inputSettings = new LinkedHashMap<String, InputData>();
		top10scores = new HashMap<String, ArrayList<Double>>();
		masterVolume = 1;
		uiScale = 1;
		aRadius = 50;
		aAlpha = 0.2;
		autoBuy = false;
		autoPauseInSP = true;
		hotbarScrollSensitivity = 0.25;
	}

	/**
	 * Called to load settings from file
	 */
	public synchronized void load() {
		Onslaught.debug("Settings: Loading");
		//Get the file
		FileHandle settings = Onslaught.instance.settingsFile;
		
		//If it doesnt exist make a new one
		if (!settings.exists()) {
			Onslaught.debug("Settings: file does not exist. Making new");
			save();
			return;
		}
		
		//Get a reader for the file
		Reader reader = settings.reader();
		try {
			//Parse into JSON
			JSONObject settingsFile = (JSONObject) parser.parse(reader);

			//Get volume
			if (settingsFile.containsKey("masterVolume")) {
				masterVolume = (Double) settingsFile.get("masterVolume");
			}

			//Get Scale
			if (settingsFile.containsKey("uiScale")) {
				uiScale = (Double) settingsFile.get("uiScale");
				Onslaught.instance.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			}

			//Get ARadius
			if (settingsFile.containsKey("aRadius")) {
				aRadius = (Double) settingsFile.get("aRadius");
			}

			//Get AAlpha
			if (settingsFile.containsKey("aAlpha")) {
				aAlpha = (Double) settingsFile.get("aAlpha");
			}

			//Get AAlpha
			if (settingsFile.containsKey("autoBuy")) {
				autoBuy = (Boolean) settingsFile.get("autoBuy");
			}

			//Get input mappings
			if (settingsFile.containsKey("inputSettings")) {
				JSONArray inputSetting = (JSONArray) settingsFile.get("inputSettings");

				for (Object o : inputSetting) {
					JSONObject inputSett = (JSONObject) o;

					if (inputSett.containsKey("key")) {
						//Get the key
						String key = (String) inputSett.get("key");

						InputData data = new InputData();
						boolean valid = false;
						//Get the type
						if (inputSett.containsKey("keyboard")) {
							data.setKeyboard(((Number) inputSett.get("keyboard")).intValue());
							valid = true;
						} else if (inputSett.containsKey("mouse")) {
							data.setMouseButton(((Number) inputSett.get("mouse")).intValue());
							valid = true;
						}

						//Add it if it is valid
						if (valid) {
							inputSettings.put(key, data);
						}
					}
				}
			}
			//Get score mappings
			if (settingsFile.containsKey("scores")) {
				JSONArray scores = (JSONArray) settingsFile.get("scores");

				for (Object o : scores) {
					JSONObject score = (JSONObject) o;

					if (score.containsKey("type")) {
						//Get the key
						String type = (String) score.get("type");

						double val = 0;
						boolean valid = false;
						//Get the type
						if (score.containsKey("val")) {
							val = ((Number)score.get("val")).doubleValue();
							valid = true;
						}

						//Add it if it is valid
						if (valid) {
							addScore(type, val);
						}
					}
				}
			}

		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}

		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Onslaught.log("Settings: loaded");
	}

	/**
	 * Save the settings to the file
	 */
	@SuppressWarnings("unchecked")
	public synchronized void save() {
		Onslaught.debug("Settings: saving");
		//Get the file
		FileHandle settings = Onslaught.instance.settingsFile;

		//Create JSON data
		JSONObject settingsData = new JSONObject();

		//Put volume in there
		settingsData.put("masterVolume", masterVolume);

		//Put volume in there
		settingsData.put("uiScale", uiScale);

		//Put volume in there
		settingsData.put("aRadius", aRadius);

		//Put alpha in there
		settingsData.put("aAlpha", aAlpha);

		//Put alpha in there
		settingsData.put("autoBuy", autoBuy);

		//Put key data in there
		JSONArray inputSettData = new JSONArray();
		for (Entry<String, InputData> e : inputSettings.entrySet()) {
			JSONObject inputData = new JSONObject();

			inputData.put("key", e.getKey());

			if (e.getValue().isKeyboard()) {
				inputData.put("keyboard", e.getValue().getKeyboard());
			} else if (e.getValue().isMouseButton()) {
				inputData.put("mouse", e.getValue().getMouseButton());
			}

			inputSettData.add(inputData);
		}

		settingsData.put("inputSettings", inputSettData);

		//Put key data in there
		JSONArray scores = new JSONArray();
		for (Entry<String, ArrayList<Double>> e : top10scores.entrySet()) {
			String type = e.getKey();
			
			for (Double val : e.getValue()) {
				JSONObject jsonObj = new JSONObject();
				
				jsonObj.put("type", type);
				jsonObj.put("val", val);
				
				scores.add(jsonObj);
			}
		}

		settingsData.put("scores", scores);

		//Write it to file
		Writer writer = settings.writer(false);

		try {
			settingsData.writeJSONString(writer);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
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
	
	private void addScore(String uid, double val) {
		if (!top10scores.containsKey(uid)) {
			top10scores.put(uid, new ArrayList<Double>());
		}
		ArrayList<Double> arr = top10scores.get(uid);
		
		arr.add(val);
		
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
	
	public void setAutoPauseInSP(boolean autoPauseInSP) {
		this.autoPauseInSP = autoPauseInSP;
	}
	
	public boolean isAutoPauseInSP() {
		return autoPauseInSP;
	}
	
	public double getHotbarScrollSensitivity() {
		return hotbarScrollSensitivity;
	}
	
	public void setHotbarScrollSensitivity(double hotbarScrollSensitivity) {
		this.hotbarScrollSensitivity = hotbarScrollSensitivity;
	}
}
