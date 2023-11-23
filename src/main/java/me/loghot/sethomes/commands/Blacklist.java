package me.loghot.sethomes.commands;

import me.loghot.sethomes.Sethomes;
import me.loghot.sethomes.utils.Chatutils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Blacklist implements CommandExecutor {
    private final Sethomes pl;

    public Blacklist(Sethomes plugin){
        this.pl = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Chatutils.notPlayerError(sender);
            return false;
        } else if (!cmd.getName().equalsIgnoreCase("blacklist")) {
            return false;
        } else {
            Player p = (Player)sender;
            String filler = StringUtils.repeat("-", 53);
            if (args.length != 0) {
                List temp;
                if (args[0].equalsIgnoreCase("add")) {
                    if (p.hasPermission("homes.blacklist_add")) {
                        if (args.length == 2) {
                            if (this.getAllWorlds().contains(args[1]) && !this.pl.getBlacklistedWorlds().contains(args[1])) {
                                temp = this.pl.getBlacklistedWorlds();
                                temp.add(args[1]);
                                this.pl.getBlacklist().getConfig().set("blacklisted_worlds", temp);
                                this.pl.getBlacklist().save();
                                Chatutils.sendSuccess(p, "You have added the world '" + args[1] + "' to the blacklist!");
                                return true;
                            } else {
                                Chatutils.sendError(p, "There was no world found by that name!");
                                return true;
                            }
                        } else {
                            Chatutils.sendError(p, "You must specify a world name to add to the blacklist!");
                            return true;
                        }
                    } else {
                        Chatutils.permissionError(p);
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("remove")) {
                    if (p.hasPermission("homes.blacklist_remove")) {
                        if (args.length == 2) {
                            if (this.pl.getBlacklistedWorlds().contains(args[1])) {
                                temp = this.pl.getBlacklistedWorlds();
                                temp.remove(args[1]);
                                this.pl.getBlacklist().getConfig().set("blacklisted_worlds", temp);
                                this.pl.getBlacklist().save();
                                Chatutils.sendSuccess(p, "You have removed the world '" + args[1] + "' from the blacklist!");
                                return true;
                            } else {
                                Chatutils.sendError(p, "There was no world by that name found in the blacklist!");
                                return true;
                            }
                        } else {
                            Chatutils.sendError(p, "You must specify a world name to remove from the blacklist!");
                            return true;
                        }
                    } else {
                        Chatutils.permissionError(p);
                        return true;
                    }
                } else {
                    Chatutils.sendError(p, "There is no '" + args[0] + "' blacklist action!");
                    return false;
                }
            } else if (!p.hasPermission("homes.blacklist_list")) {
                Chatutils.permissionError(p);
                return true;
            } else if (this.pl.getBlacklistedWorlds().size() <= 0) {
                Chatutils.sendInfo(p, "There are no worlds in the blacklist currently!");
                return true;
            } else {
                p.sendMessage(ChatColor.DARK_RED + "All blacklisted worlds:");
                p.sendMessage(filler);
                Iterator var7 = this.pl.getBlacklistedWorlds().iterator();

                while(var7.hasNext()) {
                    String w = (String)var7.next();
                    p.sendMessage(ChatColor.LIGHT_PURPLE + " - " + w);
                }

                return true;
            }
        }
    }

    private List<String> getAllWorlds() {
        List<String> worldNames = new ArrayList();
        Iterator var2 = Bukkit.getWorlds().iterator();

        while(var2.hasNext()) {
            World w = (World)var2.next();
            worldNames.add(w.getName());
        }

        return worldNames;
    }
}
