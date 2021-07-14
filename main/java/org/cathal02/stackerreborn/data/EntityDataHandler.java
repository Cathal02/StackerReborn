package org.cathal02.stackerreborn.data;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import org.cathal02.stackerreborn.StackerReborn;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EntityDataHandler {
    StackerReborn plugin;

    File spawnerFile;
    File mobFile;
    public EntityDataHandler(StackerReborn plugin)
    {
        this.plugin = plugin;
        spawnerFile = new File(plugin.getDataFolder().getPath() + File.separator+"spawners.json");
        mobFile = new File(plugin.getDataFolder().getPath() + File.separator+"mobs.json");
        if (!spawnerFile.exists()) plugin.saveResource(spawnerFile.getName(), false);
        if (!mobFile.exists()) plugin.saveResource(mobFile.getName(), false);
    }

    public void saveEntities(Collection<SpawnerData> spawnerData, Collection<MobData> mobData)
    {
        Gson gson = new GsonBuilder().registerTypeAdapter(SpawnerData.class, new SpawnerSerializer()).setPrettyPrinting().create();
        List<SpawnerData> data = new ArrayList<>(spawnerData);
        data.toArray();
        try
        {
            FileWriter fileWriter = new FileWriter(spawnerFile, false);
            fileWriter.flush();
            gson.toJson(data, fileWriter);
            fileWriter.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        Gson mobGson = new GsonBuilder().setPrettyPrinting().create();
        List<MobData> mData = new ArrayList<>(mobData);
        mData.toArray();
        try
        {
            FileWriter writer = new FileWriter(mobFile, false);
            writer.flush();
            mobGson.toJson(mData, writer);
            writer.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }


    public SpawnerData[] getSpawners() {
        try
        {
            Gson gson = null;
            JsonArray arr = new JsonArray();

            JsonReader reader = new JsonReader( new FileReader(spawnerFile));
            JsonParser parser = new JsonParser();
            JsonElement parsedElement = parser.parse(reader);
            if(parsedElement.isJsonArray())
            {
                arr = parsedElement.getAsJsonArray();
            }

            gson = new GsonBuilder().registerTypeAdapter(SpawnerData.class, new SpawnerDeserializer()).create();
            reader.close();

            return gson.fromJson(arr, SpawnerData[].class);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public MobData[] getMobs() {
        try
        {
            Gson gson = null;
            JsonArray arr = new JsonArray();

            JsonReader reader = new JsonReader( new FileReader(mobFile));
            JsonParser parser = new JsonParser();
            JsonElement parsedElement = parser.parse(reader);
            if(parsedElement.isJsonArray())
            {
                arr = parsedElement.getAsJsonArray();
            }

            gson = new Gson();
            reader.close();

            return gson.fromJson(arr, MobData[].class);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
