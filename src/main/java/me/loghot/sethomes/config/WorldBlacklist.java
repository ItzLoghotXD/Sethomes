package me.loghot.sethomes.config;

import me.loghot.sethomes.Sethomes;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;

public class WorldBlacklist extends Config {
    private Sethomes pl;
    private FileConfiguration worldBlacklistConfig = null;
    private File worldBlacklistFile = null;

    public WorldBlacklist(Sethomes plugin) {
        super(plugin);
        this.pl = plugin;
    }

    public void reloadConfig() {
        if (this.worldBlacklistFile == null) {
            this.worldBlacklistFile = new File(this.pl.getDataFolder().getPath(), "world_blacklist.yml");
        }

        this.worldBlacklistConfig = YamlConfiguration.loadConfiguration(this.worldBlacklistFile);
        this.save();

        try {
            Reader defConfigStream = new FileReader(this.worldBlacklistFile);
            if (defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                this.worldBlacklistConfig.setDefaults(defConfig);
            }
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    public FileConfiguration getConfig() {
        if (this.worldBlacklistConfig == null) {
            this.reloadConfig();
        }

        return this.worldBlacklistConfig;
    }

    public void save() {
        if (this.worldBlacklistConfig != null && this.worldBlacklistFile != null) {
            try {
                this.getConfig().save(this.worldBlacklistFile);
            } catch (Exception var2) {
                this.pl.getLogger().log(Level.SEVERE, ChatColor.DARK_RED + "Could not save config!", var2);
            }

        }
    }
}
