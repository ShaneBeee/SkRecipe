package tk.shanebee.skrecipe;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class SkRecipe extends JavaPlugin {

    private static SkRecipe instance;

    @Override
    public void onEnable() {
        instance = this;
        PluginDescriptionFile desc = getDescription();

        if ((Bukkit.getPluginManager().getPlugin("Skript") != null) && (Skript.isAcceptRegistrations())) {
            SkriptAddon addon = Skript.registerAddon(this);
            if (!Skript.isRunningMinecraft(1, 13)) {
                log("&cThis addon is not supported on your version.");
                log("&7SkRecipe only supports 1.13+");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
            try {
                addon.loadClasses("tk.shanebee.skrecipe", "elements");
            } catch (IOException e) {
                e.printStackTrace();
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
            log("&aSuccessfully enabled v" + desc.getVersion());
            if (desc.getVersion().contains("Beta")) {
                log("&eThis is a BETA build, things may not work as expected, please report any bugs on GitHub");
                log("&ehttps://github.com/ShaneBeee/SkRecipe/issues");
            }
        } else {
            log("&cDependency Skript was not found, plugin disabling");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
    }

    public static SkRecipe getInstance() {
        return instance;
    }

    public void log(String message) {
        String prefix = "&7[&bSkRecipe&7] ";
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    }

}
