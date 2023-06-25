package net.starly.gacha.repo;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import net.starly.gacha.GachaMain;
import net.starly.gacha.addon.AddonManager;
import net.starly.gacha.gacha.Gacha;
import net.starly.gacha.manager.GachaManager;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                if (gacha == null) return;
                JsonElement gachaElement = gson.toJsonTree(gacha.serialize());
                gachaTypeArray.add(gachaElement);
            });
            json.add("gachaTypes", gachaTypeArray);

            gson.toJson(json, writer);

        } catch (IOException exception) { exception.printStackTrace(); }
    }

    public void loadData() {
        List<Gacha> gachaList = new ArrayList<>(GachaManager.getInstance().getGachas());
        gachaList.stream()
            .map(Gacha::getName)
            .forEach(GachaManager.getInstance()::removeGacha);

        try (Reader reader = Files.newBufferedReader(dataFile.toPath())) {
            JsonObject json = gson.fromJson(reader, JsonObject.class);

            if (json == null) return;

            if (json.has("gachaTypes")) {
                JsonArray gachaTypeArray = json.getAsJsonArray("gachaTypes");
                gachaTypeArray.forEach(jsonElement -> {
                    Gacha gacha = Gacha.deserialize(gson.fromJson(jsonElement,new TypeToken<Map<String, Object>>() {}.getType()));
                    if (gacha == null) return;

                    GachaManager.getInstance().addGacha(gacha);
                });
            }
        } catch (IOException exception) { exception.printStackTrace(); }
    }
}
