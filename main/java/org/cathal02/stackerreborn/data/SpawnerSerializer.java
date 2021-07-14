package org.cathal02.stackerreborn.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.cathal02.stackerreborn.data.SpawnerData;

import java.lang.reflect.Type;

public class SpawnerSerializer implements JsonSerializer<SpawnerData> {
    @Override
    public JsonElement serialize(SpawnerData src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("entityCount", src.getEntityCount());
        obj.add("location", new JsonObject());
        obj.addProperty("mobName", src.mobName);
        JsonObject location = obj.get("location").getAsJsonObject();
        location.addProperty("x", src.location.getBlockX());
        location.addProperty("y", src.location.getBlockY());
        location.addProperty("z", src.location.getBlockZ());
        location.addProperty("world", src.location.getWorld().getName());

        return obj;
    }
}
