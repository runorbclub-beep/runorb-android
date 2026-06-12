package com.cloud.runball.basecomm.service.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.List;

public class RemoveNullListDeserializer<T> implements JsonDeserializer<List<T>> {
    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray jsonArray = new JsonArray();
        for (final JsonElement jsonElement : json.getAsJsonArray()) {
            if (jsonElement.isJsonNull()) {
                continue;
            }
            jsonArray.add(jsonElement);
        }

        Gson gson = new GsonBuilder().create();
        List<?> list = gson.fromJson(jsonArray, typeOfT);
        return (List<T>) list;
    }
}