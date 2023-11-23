package me.loghot.sethomes.commands;

import me.loghot.sethomes.Home;
import me.loghot.sethomes.Sethomes;
import me.loghot.sethomes.utils.Chatutils;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class Sethome implements CommandExecutor {
    private final Sethomes pl;
    private final HashMap<String, Integer> maxHomesList;
    private final Permission vaultPerms;
    private final LuckPerms luckPerms;
    private final boolean permissions;

    public Sethome(Sethomes plugin) {
        this.pl = plugin;
        this.maxHomesList = this.pl.getMaxHomes();
        this.luckPerms = this.pl.getLuckPermsApi();
        this.vaultPerms = this.pl.getVaultPermissions();
        this.permissions = this.luckPerms != null || this.vaultPerms != null;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Chatutils.notPlayerError(sender);
            return false;
        } else if (cmd.getName().equalsIgnoreCase("sethome")) {
            Player p = (Player)sender;
            String uuid = p.getUniqueId().toString();
            Location home = p.getLocation();
            if (this.pl.getBlacklistedWorlds().contains(((World)Objects.requireNonNull(home.getWorld())).getName()) && !p.hasPermission("homes.config_bypass")) {
                Chatutils.sendError(p, "This world does not allow the usage og homes!");
                return true;
            } else {
                Home playersHome = new Home(home);
                if (args.length < 1) {
                    this.pl.saveUnknownHome(uuid, playersHome);
                    Chatutils.sendSuccess(p, "You have set a default home!");
                } else {
                    if (p.hasPermission("homes.sethome")) {
                        if (this.pl.hasNamedHomes(uuid)) {
                            int maxHomes = this.getMaxHomesAllowed(p);
                            Bukkit.getServer().getLogger().info("Max Homes: " + maxHomes);
                            if (this.pl.getPlayersNamedHomes(uuid).size() >= maxHomes && maxHomes != 0 && !p.hasPermission("homes.config_bypass")) {
                                Chatutils.sendInfo(p, this.pl.config.getString("max-homes-msg"));
                                return true;
                            }

                            if (this.pl.getPlayersNamedHomes(uuid).containsKey(args[0])) {
                                Chatutils.sendError(p, "You already have a home with that name, try a different one!");
                                return true;
                            }
                        }

                        String homeName = args[0].replaceAll("[^a-zA-Z0-9]", "");
                        if (homeName.length() <= 0) {
                            Chatutils.sendError(p, "Please use a valid home name! Only a-z & 0-9 characters are allowed.");
                            return true;
                        }

                        playersHome.setHomeName(homeName);
                        StringBuilder desc = new StringBuilder();

                        for(int i = 1; i <= args.length - 1; ++i) {
                            desc.append(args[i]).append(" ");
                        }

                        if (!desc.toString().equals("")) {
                            playersHome.setDesc(desc.substring(0, desc.length() - 1));
                        }

                        this.pl.saveNamedHome(uuid, playersHome);
                        Chatutils.sendSuccess(p, "Your home '" + playersHome.getHomeName() + "' has been set!");
                        return true;
                    }

                    Chatutils.permissionError(p);
                }

                return true;
            }
        } else {
            return false;
        }
    }

    private int getMaxHomesAllowed(Player p) {
        int maxHomes = 0;
        if (this.permissions) {
            int max_home_val;
            if (this.luckPerms != null) {
                Iterator var3 = this.maxHomesList.keySet().iterator();

                while(var3.hasNext()) {
                    String group = (String)var3.next();
                    if (p.hasPermission("group." + group)) {
                        max_home_val = (Integer)this.maxHomesList.get(group);
                        if (maxHomes < max_home_val) {
                            maxHomes = max_home_val;
                        }
                    }
                }
            } else {
                String[] var9 = this.vaultPerms.getPlayerGroups(p);
                int var10 = var9.length;

                for(max_home_val = 0; max_home_val < var10; ++max_home_val) {
                    String group = var9[max_home_val];
                    Iterator var7 = this.maxHomesList.keySet().iterator();

                    while(var7.hasNext()) {
                        String g = (String)var7.next();
                        if (group.equalsIgnoreCase(g) && (Integer)this.maxHomesList.get(g) > maxHomes) {
                            maxHomes = (Integer)this.maxHomesList.get(g);
                        }
                    }
                }
            }
        }

        return maxHomes;
    }
}
