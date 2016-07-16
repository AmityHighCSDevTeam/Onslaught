package org.amityregion5.onslaught.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MapUtil {
	public static <T, U> HashMap<T, U> convertToHashMap(Set<Entry<T, U>> entries) {
		return (HashMap<T, U>) entries.parallelStream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}
	
	public static void addMapToJson(JsonObject obj, Map<String, JsonElement> map) {
		map.entrySet().parallelStream().forEach((e)->{
			obj.add(e.getKey(), e.getValue());
		});
	}
}
