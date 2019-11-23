package tk.shanebee.skrecipe;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Config {

    private SkRecipe plugin;
    private FileConfiguration config;
    private File configFile;

    // Config stuff
    public boolean DEBUG;

    public Config(SkRecipe plugin) {
        this.plugin = plugin;
        loadConfigFile();
    }

    private void loadConfigFile() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "config.yml");
        }
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        loadConfigs();
    }

    private void loadConfigs() {
        this.DEBUG = this.config.getBoolean("debug");
    }

}
