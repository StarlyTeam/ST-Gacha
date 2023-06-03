package net.starly.gacha.repo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.starly.gacha.GachaMain;
import net.starly.gacha.gacha.Gacha;
import net.starly.gacha.manager.GachaManager;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class GachaRepository {
    private static GachaRepository instance;
    public static GachaRepository getInstance() {
        if (instance == null) instance = new GachaRepository();
        return instance;
    }

    private final File dataFile;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private GachaRepository() {
        dataFile = new File(GachaMain.getInstance().getDataFolder(),"data.json");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
                saveData();
            } catch (IOException exception) { exception.printStackTrace(); }
        }
    }

    public void saveData() {
        try (Writer writer = Files.newBufferedWriter(dataFile.toPath(), StandardCharsets.UTF_8)) {
            JsonObject json = new JsonObject();
            JsonArray gachaTypeArray = new JsonArray();

            GachaManager.getInstance().getGachas().forEach(gacha -> {
                gachaTypeArray.add(gson.toJson(gacha));
            });
            json.add("gachaTypes", gachaTypeArray);

            gson.toJson(json, writer);

        } catch (IOException exception) { exception.printStackTrace(); }
    }

    public void loadData() {
        try (Reader reader = Files.newBufferedReader(dataFile.toPath())) {
            JsonObject json = gson.fromJson(reader, JsonObject.class);

            if (json == null) return;

            if (json.has("gachaTypes")) {
                JsonArray gachaTypeArray = json.getAsJsonArray("gachaTypes");
                gachaTypeArray.forEach(jsonElement -> GachaManager.getInstance().addGacha(gson.fromJson(jsonElement, Gacha.class)));
            }
        } catch (IOException exception) { exception.printStackTrace(); }
    }
}
