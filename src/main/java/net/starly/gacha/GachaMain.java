package net.starly.gacha;

import net.starly.gacha.addon.registry.AddonRegistry;
import net.starly.gacha.command.GachaExecutor;
import net.starly.gacha.context.MessageContent;
import net.starly.gacha.preset.service.SimpleGachaService;
import net.starly.gacha.repo.GachaRepository;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class GachaMain extends JavaPlugin {

    private static GachaMain instance;
    public static GachaMain getInstance() {
        return instance;
    }


    @Override
    public void onEnable() {

        /* DEPENDENCY
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        if (!isPluginEnabled("ST-Core")) {
            getServer().getLogger().warning("[" + getName() + "] ST-Core 플러그인이 적용되지 않았습니다! 플러그인을 비활성화합니다.");
            getServer().getLogger().warning("[" + getName() + "] 다운로드 링크 : §fhttp://starly.kr/");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        /* SETUP
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        instance = this;
        //new Metrics(this, 12345); // TODO: 수정

        AddonRegistry.registerPreset("StarlyGacha", new SimpleGachaService());

        /* CONFIG
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        saveDefaultConfig();
        MessageContent.getInstance().initialize(getConfig());

        /* DATA
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        GachaRepository.getInstance().loadData();

        /* COMMAND
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        getCommand("가챠").setExecutor(new GachaExecutor());

    }

    @Override
    public void onDisable() {
        GachaRepository.getInstance().saveData();
    }

    private boolean isPluginEnabled(String name) {
        Plugin plugin = getServer().getPluginManager().getPlugin(name);
        return plugin != null && plugin.isEnabled();
    }
}
