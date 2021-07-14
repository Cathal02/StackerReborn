package org.cathal02.stackerreborn.data;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;

public class SpawnerDeserializer implements JsonDeserializer<SpawnerData> {
    @Override
    public SpawnerData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        JsonObject location = obj.get("location").getAsJsonObject();
        World world = Bukkit.getWorld(location.get("world").getAsString());

        return new SpawnerData(new Location(world, location.get("x").getAsInt(),location.get("y").getAsInt(),location.get("z").getAsInt()), obj.get("entityCount").getAsInt(), obj.get("mobName").getAsString());
    }
}
