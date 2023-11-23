package me.loghot.sethomes.commands;

import me.loghot.sethomes.Home;
import me.loghot.sethomes.Sethomes;
import me.loghot.sethomes.utils.Chatutils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Updatehome implements CommandExecutor {
    private final Sethomes pl;

    public Updatehome(Sethomes plugin) {
        this.pl = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Chatutils.notPlayerError(sender);
            return false;
        } else {
            Player p = (Player)sender;
            Home newHome;
            HashMap playersHomes;
            Home oldHome;
            String desc;
            if (cmd.getName().equalsIgnoreCase("uhome")) {
                if (!p.hasPermission("homes.uhome")) {
                    Chatutils.permissionError(p);
                    return false;
                }

                String uuid = p.getPlayer().getUniqueId().toString();
                Location home = p.getLocation();
                if (this.pl.getBlacklistedWorlds().contains(home.getWorld().getName()) && !p.hasPermission("homes.config_bypass")) {
                    Chatutils.sendError(p, "This world does not allow the usage of homes!");
                    return true;
                }

                newHome = new Home(home);
                if (args.length < 1) {
                    if (!this.pl.hasUnknownHomes(uuid)) {
                        Chatutils.sendError(p, "No Default Home is currently set!");
                        return true;
                    }

                    this.pl.saveUnknownHome(uuid, newHome);
                    Chatutils.sendSuccess(p, "You have successfully updated your default home!");
                    return true;
                }

                if (this.pl.hasNamedHomes(uuid)) {
                    if (!this.pl.getPlayersNamedHomes(uuid).containsKey(args[0])) {
                        Chatutils.sendError(p, "You do not have a home with that name, try a different one!");
                        return true;
                    }

                    newHome.setHomeName(args[0]);
                    if (args.length > 1) {
                        desc = "";

                        for(int i = 1; i <= args.length - 1; ++i) {
                            desc = desc + args[i] + " ";
                        }

                        if (!desc.equals("")) {
                            newHome.setDesc(desc.substring(0, desc.length() - 1));
                        }
                    } else {
                        playersHomes = this.pl.getPlayersNamedHomes(uuid);
                        oldHome = (Home)playersHomes.get(args[0]);
                        desc = oldHome.getDesc();
                        newHome.setDesc(desc);
                    }

                    this.pl.saveNamedHome(uuid, newHome);
                    Chatutils.sendSuccess(p, "Your home '" + newHome.getHomeName() + "' has been updated!");
                    return true;
                }
            }

            if (cmd.getName().equalsIgnoreCase("uhome-of")) {
                if (!p.hasPermission("homes.uhome-of")) {
                    Chatutils.permissionError(p);
                    return false;
                }

                if (args.length < 1 || args.length > 2) {
                    Chatutils.sendError(p, "ERROR: Incorrect number of arguments!");
                    return false;
                }

                OfflinePlayer targetP = Bukkit.getServer().getOfflinePlayer(args[0]);
                if (!targetP.hasPlayedBefore()) {
                    Chatutils.sendError(p, "The player " + ChatColor.WHITE + ChatColor.BOLD + args[0] + ChatColor.DARK_RED + " has never played before!");
                    return true;
                }

                String uuid = targetP.getUniqueId().toString();
                newHome = new Home(p.getLocation());
                if (args.length == 1) {
                    if (!this.pl.hasUnknownHomes(uuid)) {
                        Chatutils.sendError(p, "No Default Home is currently set for the player " + ChatColor.WHITE + ChatColor.BOLD + args[0] + ChatColor.DARK_RED + "!");
                        return true;
                    }

                    this.pl.saveUnknownHome(uuid, newHome);
                    Chatutils.sendSuccess(p, "You have successfully updated the default home for the player " + ChatColor.WHITE + ChatColor.BOLD + args[0] + ChatColor.DARK_RED + "!");
                    return true;
                }

                if (args.length == 2) {
                    if (!this.pl.getPlayersNamedHomes(uuid).containsKey(args[1])) {
                        Chatutils.sendError(p, "The player " + ChatColor.WHITE + ChatColor.BOLD + args[0] + ChatColor.DARK_RED + " does not have a home with that name!");
                        return true;
                    }

                    newHome.setHomeName(args[1]);
                    playersHomes = this.pl.getPlayersNamedHomes(uuid);
                    oldHome = (Home)playersHomes.get(args[1]);
                    desc = oldHome.getDesc();
                    newHome.setDesc(desc);
                    this.pl.saveNamedHome(uuid, newHome);
                    Chatutils.sendSuccess(p, "You have updated the home '" + newHome.getHomeName() + "' for the player named " + ChatColor.WHITE + ChatColor.BOLD + args[0] + ChatColor.DARK_RED + "!");
                    return true;
                }
            }

            return false;
        }
    }
}
