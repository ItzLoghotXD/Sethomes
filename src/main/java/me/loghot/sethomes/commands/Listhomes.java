package me.loghot.sethomes.commands;

import me.loghot.sethomes.Home;
import me.loghot.sethomes.Sethomes;
import me.loghot.sethomes.utils.Chatutils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.UUID;

public class Listhomes implements CommandExecutor {
    private final Sethomes pl;
    private final String filler = StringUtils.repeat("-", 53);

    public Listhomes(Sethomes plugin) {
        this.pl = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Chatutils.notPlayerError(sender);
            return false;
        } else if (cmd.getName().equalsIgnoreCase("homes")) {
            Player p = (Player)sender;
            if (args.length == 1) {
                if (p.hasPermission("homes.gethomes")) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                    if (!offlinePlayer.hasPlayedBefore()) {
                        Chatutils.sendError(p, "That user has never player here before!");
                        return true;
                    }

                    UUID uuid = offlinePlayer.getUniqueId();
                    this.listHomes(uuid, p);
                } else {
                    Chatutils.permissionError(p);
                }

                return true;
            } else if (args.length == 0) {
                this.listHomes(p);
                return true;
            } else {
                Chatutils.tooManyArgs(p);
                return false;
            }
        } else {
            return false;
        }
    }

    private void checkForNamedHomes(Player p, String uuid) {
        if (this.pl.hasNamedHomes(uuid)) {
            Iterator var3 = this.pl.getPlayersNamedHomes(uuid).keySet().iterator();

            while(var3.hasNext()) {
                String id = (String)var3.next();
                String world = ((Home)this.pl.getPlayersNamedHomes(uuid).get(id)).getWorld();
                String desc = ((Home)this.pl.getPlayersNamedHomes(uuid).get(id)).getDesc();
                if (desc != null) {
                    p.sendMessage(ChatColor.DARK_AQUA + "Name: " + ChatColor.WHITE + id + ChatColor.DARK_GRAY + " | " + ChatColor.DARK_AQUA + "World: " + ChatColor.WHITE + world + ChatColor.DARK_GRAY + " | " + ChatColor.DARK_AQUA + "Desc: " + ChatColor.WHITE + desc);
                } else {
                    p.sendMessage(ChatColor.DARK_AQUA + "Name: " + ChatColor.WHITE + id + ChatColor.DARK_GRAY + " | " + ChatColor.DARK_AQUA + "World: " + ChatColor.WHITE + world);
                }
            }
        }

        p.sendMessage(this.filler);
    }

    private void listHomes(Player p) {
        String uuid = p.getUniqueId().toString();
        if (this.pl.hasUnknownHomes(uuid)) {
            p.sendMessage(ChatColor.BOLD + "Your Currently Set Homes");
            p.sendMessage(this.filler);
            String world = this.pl.getPlayersUnnamedHome(uuid).getWorld().getName();
            p.sendMessage(ChatColor.GOLD + "Default Home" + ChatColor.DARK_GRAY + " | " + ChatColor.DARK_AQUA + "World: " + ChatColor.WHITE + world);
        }

        this.checkForNamedHomes(p, uuid);
    }

    private void listHomes(UUID playerUUID, Player sender) {
        String uuid = playerUUID.toString();
        sender.sendMessage(ChatColor.BOLD + "Homes currently set for the player - " + Bukkit.getOfflinePlayer(playerUUID).getName());
        sender.sendMessage(this.filler);
        if (this.pl.hasUnknownHomes(uuid)) {
            String world = this.pl.getPlayersUnnamedHome(uuid).getWorld().getName();
            sender.sendMessage(ChatColor.GOLD + "Default Home - World: " + world);
        }

        this.checkForNamedHomes(sender, uuid);
    }
}
