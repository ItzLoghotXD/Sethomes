package me.loghot.sethomes;

import me.loghot.sethomes.commands.*;
import me.loghot.sethomes.config.Homes;
import me.loghot.sethomes.config.WorldBlacklist;
import me.loghot.sethomes.eventListeners.Eventlistener;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Level;

public final class Sethomes extends JavaPlugin {
    public FileConfiguration config;
    private FileConfiguration homesCfg;
    private Permission vaultPerms = null;
    private LuckPerms luckPermsApi = null;
    private final WorldBlacklist blacklist = new WorldBlacklist(this);
    private final Homes homes = new Homes(this);
    private final String LOG_PREFIX = "[/Sethomes]";
    private final String configHeader = StringUtils.repeat("-", 26) + "\n\tSethomes config\t\n" + StringUtils.repeat("-", 26) + "\nMessages: \n\tYou can use chat colors in messages with this symbol §.\n\tI.E: §b will change any text after it to an aqua blue color.\n\tColor codes may be found here https://www.digminecraft.com/lists/color_list_pc.php\nTime: \n\tAny time value is based in seconds.\nThings to Note: \n\tSet any integer option to 0 for it to be ignored.\n\tThe max-homes does not include the default un-named home.\n\tUse %s as the seconds variable in the cool down message.\n";

    public Sethomes() {
    }

    public void onEnable() {
        if (!this.setupLuckPerms() && !this.setupVaultPermissions()) {
            Bukkit.getServer().getLogger().log(Level.WARNING, "[/Sethomes] Could not connect to a permissions plugin! Config setting \"max-homes\" will be ignored!");
        }

        this.getLogger().info("Enabling the plugin");
        this.loadConfigurationFiles();
        this.registerCommands();
        new Eventlistener(this);
    }

    public void onDisable() {
        this.getLogger().info("Disabling the Plugin");
    }

    private void loadConfigurationFiles() {
        this.homesCfg = this.getHomes().getConfig();
        FileConfiguration blacklistCfg = this.getBlacklist().getConfig();
        if (!blacklistCfg.isSet("blacklisted_worlds")) {
            blacklistCfg.addDefault("blacklisted_worlds", new ArrayList());
        }

        blacklistCfg.options().copyDefaults(true);
        this.getBlacklist().save();
        if (!this.homesCfg.isSet("allNamedHomes") && !this.homesCfg.isSet("unknownHomes")) {
            this.homesCfg.addDefault("allNamedHomes", new HashMap());
            this.homesCfg.addDefault("unknownHomes", new HashMap());
        }

        this.homesCfg.options().copyDefaults(true);
        this.getHomes().save();
        this.config = this.getConfig();
        this.copyHomes(this.config, this.getHomes());
        if (!this.config.isSet("max-homes") || !this.config.isSet("max-homes-msg") || !this.config.isSet("tp-delay") || !this.config.isSet("tp-cooldown") || !this.config.isSet("tp-cancelOnMove") || !this.config.isSet("tp-cancelOnMove-msg") || !this.config.isSet("tp-cooldown-msg") || !this.config.isSet("auto-update")) {
            if (!this.config.isSet("max-homes")) {
                this.config.set("max-homes.default", 0);
            }

            if (!this.config.isSet("max-homes-msg")) {
                this.config.set("max-homes-msg", "§4You have reached the maximum amount of saved homes!");
            }

            if (!this.config.isSet("tp-delay")) {
                this.config.set("tp-delay", 3);
            }

            if (!this.config.isSet("tp-cooldown")) {
                this.config.set("tp-cooldown", 0);
            }

            if (!this.config.isSet("tp-cancelOnMove")) {
                this.config.set("tp-cancelOnMove", false);
            }

            if (!this.config.isSet("tp-cancelOnMove-msg")) {
                this.config.set("tp-cancelOnMove-msg", "§4Movement detected! Teleporting has been cancelled!");
            }

            if (!this.config.isSet("tp-cooldown-msg")) {
                this.config.set("tp-cooldown-msg", "§4You must wait another %s second(s) before teleporting!");
            }
        }

        if (this.config.isSet("max-homes") && this.config.getInt("max-homes") != 0) {
            int maxHomes = this.config.getInt("max-homes");
            Bukkit.getServer().getLogger().log(Level.WARNING, "[/Sethomes] We've detected you previously set the max homes within config.yml. We have updated the config and suggest \nyou read how to properly setup the config for your permission groups on the plugin page: https://github.com/LoghotGamerz/Sethomes");
            this.config.set("max-homes.default", maxHomes);
        }

        this.config.options().header(this.configHeader);
        this.config.options().copyDefaults(true);
        this.saveConfig();
        this.getHomes().reloadConfig();
        this.getBlacklist().reloadConfig();
    }

    private void registerCommands() {
        ((PluginCommand) Objects.requireNonNull(this.getCommand("sethome"))).setExecutor(new Sethome(this));
        ((PluginCommand)Objects.requireNonNull(this.getCommand("homes"))).setExecutor(new Listhomes(this));
        ((PluginCommand)Objects.requireNonNull(this.getCommand("delhome"))).setExecutor(new Deletehome(this));
        ((PluginCommand)Objects.requireNonNull(this.getCommand("home"))).setExecutor(new Gohome(this));
        ((PluginCommand)Objects.requireNonNull(this.getCommand("strike"))).setExecutor(new Strike(this));
        ((PluginCommand)Objects.requireNonNull(this.getCommand("blacklist"))).setExecutor(new Blacklist(this));
        ((PluginCommand)Objects.requireNonNull(this.getCommand("home-of"))).setExecutor(new Gohome(this));
        ((PluginCommand)Objects.requireNonNull(this.getCommand("delhome-of"))).setExecutor(new Deletehome(this));
        ((PluginCommand)Objects.requireNonNull(this.getCommand("uhome"))).setExecutor(new Updatehome(this));
        ((PluginCommand)Objects.requireNonNull(this.getCommand("uhome-of"))).setExecutor(new Updatehome(this));
        ((PluginCommand)Objects.requireNonNull(this.getCommand("setmax"))).setExecutor(new Setmax(this));
    }

    private boolean setupVaultPermissions() {
        try {
            RegisteredServiceProvider<Permission> rsp = this.getServer().getServicesManager().getRegistration(Permission.class);
            if (rsp != null) {
                this.vaultPerms = (Permission)rsp.getProvider();
                Bukkit.getServer().getLogger().info("[/Sethomes] Hooked into Vault!");
                return true;
            }
        } catch (NoClassDefFoundError var2) {
            Bukkit.getServer().getLogger().info("[/Sethomes] Vault was not found.");
        }

        return false;
    }

    private boolean setupLuckPerms() {
        try {
            RegisteredServiceProvider<LuckPerms> rsp = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
            if (rsp != null) {
                this.luckPermsApi = (LuckPerms)rsp.getProvider();
                Bukkit.getServer().getLogger().info("[/Sethomes] Hooked into LuckPerms!");
                return true;
            }
        } catch (NoClassDefFoundError var2) {
            Bukkit.getServer().getLogger().info("[/Sethomes] Luck perms was not found! Reverting to vault...");
        }

        return false;
    }

    public HashMap<String, Home> getPlayersNamedHomes(String uuid) {
        HashMap<String, Home> playersNamedHomes = new HashMap();
        String homesPath = "allNamedHomes." + uuid;
        this.homesCfg = this.getHomes().getConfig();

        String id;
        Home h;
        for(Iterator var4 = ((ConfigurationSection)Objects.requireNonNull(this.homesCfg.getConfigurationSection(homesPath))).getKeys(false).iterator(); var4.hasNext(); playersNamedHomes.put(id, h)) {
            id = (String)var4.next();
            String path = homesPath + "." + id + ".";
            Location home = this.getHomeLocaleFromConfig(path);
            h = new Home(home);
            if (this.homesCfg.isSet(path + ".desc")) {
                h.setDesc(this.homesCfg.getString(path + ".desc"));
            }
        }

        return playersNamedHomes;
    }

    public Location getNamedHomeLocal(String uuid, String homeName) {
        Home h = (Home)this.getPlayersNamedHomes(uuid).get(homeName);
        return h.toLocation();
    }

    public HashMap<String, Integer> getMaxHomes() {
        HashMap<String, Integer> maxHomes = new HashMap();
        String maxHomesPath = "max-homes";
        Iterator var3 = ((ConfigurationSection)Objects.requireNonNull(this.config.getConfigurationSection(maxHomesPath))).getKeys(false).iterator();

        while(var3.hasNext()) {
            String id = (String)var3.next();
            maxHomes.put(id, this.config.getInt(maxHomesPath + "." + id));
        }

        return maxHomes;
    }

    public boolean hasNamedHomes(String uuid) {
        this.homesCfg = this.getHomes().getConfig();
        return this.homesCfg.contains("allNamedHomes." + uuid) && this.homesCfg.isSet("allNamedHomes." + uuid);
    }

    public void saveNamedHome(String uuid, Home home) {
        String path = "allNamedHomes." + uuid + "." + home.getHomeName();
        this.saveHomeToConfig(home, path);
        this.homesCfg.set(path + ".desc", home.getDesc());
        this.getHomes().save();
    }

    public void deleteNamedHome(String uuid, String homeName) {
        String path = "allNamedHomes." + uuid + "." + homeName;
        this.getHomes().getConfig().set(path, (Object)null);
        this.getHomes().save();
        this.getHomes().reloadConfig();
    }

    public Location getPlayersUnnamedHome(String uuid) {
        String path = "unknownHomes." + uuid;
        this.homesCfg = this.getHomes().getConfig();
        return this.getHomeLocaleFromConfig(path);
    }

    public boolean hasUnknownHomes(String uuid) {
        this.homesCfg = this.getHomes().getConfig();
        return this.homesCfg.contains("unknownHomes." + uuid);
    }

    public void saveUnknownHome(String uuid, Home home) {
        String path = "unknownHomes." + uuid;
        this.saveHomeToConfig(home, path);
        this.getHomes().save();
    }

    private void saveHomeToConfig(Home home, String path) {
        this.homesCfg = this.getHomes().getConfig();
        this.homesCfg.set(path + ".world", home.getWorld());
        this.homesCfg.set(path + ".x", home.getX());
        this.homesCfg.set(path + ".y", home.getY());
        this.homesCfg.set(path + ".z", home.getZ());
        this.homesCfg.set(path + ".pitch", home.getPitch());
        this.homesCfg.set(path + ".yaw", home.getYaw());
    }

    private Location getHomeLocaleFromConfig(String path) {
        World world = this.getServer().getWorld((String)Objects.requireNonNull(this.homesCfg.getString(path + ".world")));
        double x = this.homesCfg.getDouble(path + ".x");
        double y = this.homesCfg.getDouble(path + ".y");
        double z = this.homesCfg.getDouble(path + ".z");
        float pitch = Float.parseFloat((String)Objects.requireNonNull(this.homesCfg.getString(path + ".pitch")));
        float yaw = Float.parseFloat((String)Objects.requireNonNull(this.homesCfg.getString(path + ".yaw")));
        return new Location(world, x, y, z, pitch, yaw);
    }

    public void deleteUnknownHome(String uuid) {
        String path = "unknownHomes." + uuid;
        this.getHomes().getConfig().set(path, (Object)null);
        this.getHomes().save();
        this.getHomes().reloadConfig();
    }

    public WorldBlacklist getBlacklist() {
        return this.blacklist;
    }

    public List<String> getBlacklistedWorlds() {
        return this.getBlacklist().getConfig().getStringList("blacklisted_worlds");
    }

    public Homes getHomes() {
        return this.homes;
    }

    private void copyHomes(FileConfiguration config, Homes homeConfig) {
        if (config.contains("allNamedHomes") && config.isSet("allNamedHomes")) {
            homeConfig.getConfig().set("allNamedHomes", config.get("allNamedHomes"));
            config.set("allNamedHomes", (Object)null);
            homeConfig.save();
            this.saveConfig();
        }

        if (config.contains("unknownHomes") && config.isSet("unknownHomes")) {
            homeConfig.getConfig().set("unknownHomes", config.get("unknownHomes"));
            config.set("unknownHomes", (Object)null);
            homeConfig.save();
            this.saveConfig();
        }

    }

    public void cancelTask(int taskId) {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    public Permission getVaultPermissions() {
        return this.vaultPerms;
    }

    public LuckPerms getLuckPermsApi() {
        return this.luckPermsApi;
    }

    public static Sethomes getInstance() {
        return getPlugin(Sethomes.class);
    }
}
