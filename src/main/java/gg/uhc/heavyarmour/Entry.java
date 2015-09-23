package gg.uhc.heavyarmour;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Entry extends JavaPlugin {

    @Override
    public void onEnable() {
        FileConfiguration configuration = getConfig();
        configuration.options().copyDefaults(true);
        saveConfig();

        try {
            AttributeModifierApplier applier = new AttributeModifierApplier(this);
            getServer().getPluginManager().registerEvents(new ArmourCraftListener(applier, configuration.getDouble("percent per armour point")), this);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
            setEnabled(false);
            getLogger().severe("This version of Bukkit/Spigot appears to be unsupported by this version of the plugin");
        }
    }
}
