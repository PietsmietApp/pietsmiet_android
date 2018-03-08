package de.pscom.pietsmiet.data.facebook;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.pscom.pietsmiet.data.facebook.model.FacebookEntity;

public class FacebookResponseDeserializer implements JsonDeserializer<List<FacebookEntity>> {
    @Override
    public List<FacebookEntity> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<FacebookEntity> toReturn = new ArrayList<>();
        JsonArray array = json.getAsJsonArray();
        for (JsonElement e : array) {
            String body = e.getAsJsonObject().get("body").getAsString();
            //body = body.replaceAll("\\\\\"(?<!\\\\\\\\\")", "\"");
            toReturn.add(new Gson().fromJson(body, FacebookEntity.class));
        }
        return toReturn;
    }
}
