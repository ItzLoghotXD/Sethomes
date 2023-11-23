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

public class Homes extends Config {
    private final Sethomes pl;
    private FileConfiguration homes = null;
    private File homesFile = null;

    public Homes(Sethomes plugin) {
        super(plugin);
        this.pl = plugin;
    }

    public void reloadConfig() {
        if (this.homesFile == null) {
            this.homesFile = new File(this.pl.getDataFolder().getPath(), "homes.yml");
        }

        this.homes = YamlConfiguration.loadConfiguration(this.homesFile);
        this.save();

        try {
            Reader defConfigStream = new FileReader(this.homesFile);
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            this.homes.setDefaults(defConfig);
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    public FileConfiguration getConfig() {
        if (this.homes == null) {
            this.reloadConfig();
        }

        return this.homes;
    }

    public void save() {
        if (this.homes != null && this.homesFile != null) {
            try {
                this.getConfig().save(this.homesFile);
            } catch (Exception var2) {
                this.pl.getLogger().log(Level.SEVERE, ChatColor.DARK_RED + "Could not save config!", var2);
            }

        }
    }
}
