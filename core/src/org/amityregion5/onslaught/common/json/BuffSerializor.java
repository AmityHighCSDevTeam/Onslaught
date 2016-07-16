package org.amityregion5.onslaught.common.json;

import java.lang.reflect.Type;

import org.amityregion5.onslaught.common.buff.Buff;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class BuffSerializor implements JsonSerializer<Buff>, JsonDeserializer<Buff> {
	@Override
	public Buff deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		Buff buff = new Buff();
		
		JsonObject mainObj = json.getAsJsonObject();
		
		if (mainObj.has("add")) {
			JsonObject addObj = mainObj.get("add").getAsJsonObject();
			addObj.entrySet().forEach((e)->{
				buff.addAdd(e.getKey(), e.getValue().getAsDouble());
			});
		}
		if (mainObj.has("mult")) {
			JsonObject addObj = mainObj.get("mult").getAsJsonObject();
			addObj.entrySet().forEach((e)->{
				buff.addMult(e.getKey(), e.getValue().getAsDouble());
			});
		}
		
		return buff;
	}

	@Override
	public JsonElement serialize(Buff src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject mainObj = new JsonObject();{
			JsonObject addObj = new JsonObject();{
				src.getAdditive().entrySet().forEach((e)->{
					addObj.addProperty(e.getKey(), e.getValue());
				});
			}mainObj.add("add", addObj);
			JsonObject multObj = new JsonObject();{
				src.getMultiplicative().entrySet().forEach((e)->{
					multObj.addProperty(e.getKey(), e.getValue());
				});
			}mainObj.add("mult", multObj);
		}return mainObj;
	}
}
