package org.amityregion5.onslaught.common.json;

import java.lang.reflect.Type;

import org.amityregion5.onslaught.common.game.model.IEntityModel;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public class EntityModelSerializor implements JsonDeserializer<IEntityModel<?>>, JsonSerializer<IEntityModel<?>> {

	@Override
	public JsonElement serialize(IEntityModel<?> src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject data = new JsonObject();
		src.write(data);
		return data;
	}

	@Override
	public IEntityModel<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		try {
			JsonObject container = json.getAsJsonObject();
			Class<?> type = TypeToken.get(typeOfT).getRawType();
			IEntityModel<?> o = (IEntityModel<?>) type.newInstance();
			o.read(container);
			return o;
		} catch (IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}
		return null;
	}

}
