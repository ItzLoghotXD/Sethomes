package me.loghot.sethomes.commands;

import me.loghot.sethomes.Sethomes;
import me.loghot.sethomes.utils.Chatutils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Deletehome implements CommandExecutor {
    private final Sethomes pl;

    public Deletehome(Sethomes plugin) {
        this.pl = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Chatutils.notPlayerError(sender);
            return false;
        } else {
            Player p = (Player)sender;
            if (cmd.getName().equalsIgnoreCase("delhome")) {
                if (!p.hasPermission("homes.delhome")) {
                    Chatutils.permissionError(p);
                    return false;
                } else {
                    String uuid = p.getUniqueId().toString();
                    if (args.length < 1) {
                        if (!this.pl.hasUnknownHomes(uuid)) {
                            Chatutils.sendError(p, "No Default Home is currently set!");
                            return true;
                        } else {
                            this.pl.deleteUnknownHome(uuid);
                            Chatutils.sendSuccess(p, "Default Home has been removed!");
                            return true;
                        }
                    } else if (args.length > 1) {
                        Chatutils.tooManyArgs(p);
                        return false;
                    } else if (this.pl.hasNamedHomes(uuid) && this.pl.getPlayersNamedHomes(uuid).containsKey(args[0])) {
                        this.pl.deleteNamedHome(uuid, args[0]);
                        Chatutils.sendSuccess(p, "You have deleted the home: " + args[0]);
                        return true;
                    } else {
                        Chatutils.sendError(p, "You have no homes by that name!");
                        return true;
                    }
                }
            } else if (cmd.getName().equalsIgnoreCase("delhome-of")) {
                if (!p.hasPermission("homes.delhome-of")) {
                    Chatutils.permissionError(p);
                    return false;
                } else if (args.length >= 1 && args.length <= 2) {
                    OfflinePlayer targetP = Bukkit.getServer().getOfflinePlayer(args[0]);
                    if (!targetP.hasPlayedBefore()) {
                        Chatutils.sendError(p, "The player " + ChatColor.WHITE + ChatColor.BOLD + args[0] + ChatColor.DARK_RED + " has never played here before!");
                        return false;
                    } else {
                        String uuid = targetP.getUniqueId().toString();
                        if (args.length == 1) {
                            if (!this.pl.hasUnknownHomes(uuid)) {
                                Chatutils.sendError(p, "The player " + ChatColor.WHITE + ChatColor.BOLD + args[0] + ChatColor.DARK_RED + " has no default home!");
                                return false;
                            } else {
                                this.pl.deleteUnknownHome(uuid);
                                Chatutils.sendSuccess(p, "You have deleted the default home for player " + ChatColor.WHITE + ChatColor.BOLD + args[0] + ChatColor.GOLD + "!");
                                return true;
                            }
                        } else {
                            String homeName = args[1];
                            if (this.pl.hasNamedHomes(uuid) && this.pl.getPlayersNamedHomes(uuid).containsKey(homeName)) {
                                this.pl.deleteNamedHome(uuid, homeName);
                                Chatutils.sendSuccess(p, "You have deleted the '" + homeName + "' home for player " + ChatColor.WHITE + ChatColor.BOLD + args[0] + ChatColor.GOLD + "!");
                                return true;
                            } else {
                                Chatutils.sendError(p, "The player " + ChatColor.WHITE + ChatColor.BOLD + args[0] + ChatColor.DARK_RED + " has no homes by that name!");
                                return false;
                            }
                        }
                    }
                } else {
                    Chatutils.sendError(p, "ERROR: Incorrect number of arguments!");
                    return false;
                }
            } else {
                return false;
            }
        }
    }
}
