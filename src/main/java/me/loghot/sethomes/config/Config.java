package me.loghot.sethomes.config;

import me.loghot.sethomes.Sethomes;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class Config {
    Sethomes pl;

    public Config(Sethomes plugin) {
        this.pl = plugin;
    }

    public abstract void reloadConfig();

    public abstract FileConfiguration getConfig();

    public abstract void save();
}
