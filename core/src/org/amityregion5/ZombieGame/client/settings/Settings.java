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

import com.badlogic.gdx.files.FileHandle;

public class Settings {
	
	private static JSONParser	parser	= new JSONParser();
	private LinkedHashMap<String,InputData> inputSettings;
	private double masterVolume;
	
	public Settings() {
		inputSettings = new LinkedHashMap<String, InputData>();
		masterVolume = 1;
	}
	
	public synchronized void load() {
		ZombieGame.debug("Settings: Loading");
		FileHandle settings = ZombieGame.instance.settingsFile;
		if (!settings.exists()) {
			ZombieGame.debug("Settings: file does not exist. Making new");
			save();
			return;
		}
		Reader reader = settings.reader();
		try {
			JSONObject settingsFile = (JSONObject)parser.parse(reader);
			
			if (settingsFile.containsKey("masterVolume")) {
				masterVolume = (Double)settingsFile.get("masterVolume");
			}
			
			if (settingsFile.containsKey("inputSettings")) {
				JSONArray inputSetting = (JSONArray)settingsFile.get("inputSettings");
				
				for (Object o : inputSetting) {
					JSONObject inputSett = (JSONObject)o;
					
					if (inputSett.containsKey("key")) {
						String key = (String)inputSett.get("key");
						
						InputData data = new InputData();
						boolean valid = false;
						if (inputSett.containsKey("keyboard")) {
							data.setKeyboard(((Number)inputSett.get("keyboard")).intValue());
							valid = true;
						} else if (inputSett.containsKey("mouse")) {
							data.setMouseButton(((Number)inputSett.get("mouse")).intValue());
							valid = true;
						}
						
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
	
	@SuppressWarnings("unchecked")
	public synchronized void save() {
		ZombieGame.debug("Settings: saving");
		FileHandle settings = ZombieGame.instance.settingsFile;
		
		JSONObject settingsData = new JSONObject();
		
		settingsData.put("masterVolume", masterVolume);
		
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
	
	public boolean registerInput(String key, InputData def) {
		return getInputOrDefault(key, def) == def;
	}
	
	public synchronized InputData getInputOrDefault(String key, InputData def) {
		if (!inputSettings.containsKey(key)) {
			inputSettings.put(key, def);
		}
		return inputSettings.get(key);
	}
	
	public synchronized InputData getInput(String key) {
		return inputSettings.get(key);
	}
	
 	public void setInput(String key, InputData val) {
		inputSettings.put(key, val);
	}

	public Set<Entry<String, InputData>> getEntries() {
		return inputSettings.entrySet();
	}
	
	public double getSameValues(InputData data) {
		return inputSettings.values().parallelStream().filter((v)->v.equals(data)).count();
	}
	
	public double getMasterVolume() {
		return masterVolume;
	}
	
	public void setMasterVolume(double masterVolume) {
		this.masterVolume = masterVolume;
	}
}
