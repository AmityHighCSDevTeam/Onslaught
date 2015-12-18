package org.amityregion5.ZombieGame.client.settings;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.amityregion5.ZombieGame.ZombieGame;
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
	//The master volume
	private double								masterVolume;
	//The master volume
	private double								uiScale;

	public Settings() {
		//Create hashmap
		inputSettings = new LinkedHashMap<String, InputData>();
		masterVolume = 1;
		uiScale = 1;
	}

	/**
	 * Called to load settings from file
	 */
	public synchronized void load() {
		ZombieGame.debug("Settings: Loading");
		//Get the file
		FileHandle settings = ZombieGame.instance.settingsFile;
		
		//If it doesnt exist make a new one
		if (!settings.exists()) {
			ZombieGame.debug("Settings: file does not exist. Making new");
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

			//Get volume
			if (settingsFile.containsKey("uiScale")) {
				uiScale = (Double) settingsFile.get("uiScale");
				ZombieGame.instance.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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

		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}

		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ZombieGame.log("Settings: loaded");
	}

	/**
	 * Save the settings to the file
	 */
	@SuppressWarnings("unchecked")
	public synchronized void save() {
		ZombieGame.debug("Settings: saving");
		//Get the file
		FileHandle settings = ZombieGame.instance.settingsFile;

		//Create JSON data
		JSONObject settingsData = new JSONObject();

		//Put volume in there
		settingsData.put("masterVolume", masterVolume);

		//Put volume in there
		settingsData.put("uiScale", uiScale);

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
		ZombieGame.log("Settings: saved");
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
}
