package me.loghot.sethomes.commands;

import me.loghot.sethomes.Sethomes;
import me.loghot.sethomes.utils.Chatutils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Help implements CommandExecutor {
    private final Sethomes pl;

    public Help(Sethomes plugin) {
        this.pl = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getLabel().equalsIgnoreCase("help")) {
            if (!(sender instanceof Player)) {
                Chatutils.notPlayerError(sender);
                Player p = (Player)sender;
                if (p.hasPermission("homes.*")) {
                    return this.doHelpOp(sender, args);
                }
            }

            return this.doHelp(sender, args);
        } else {
            return false;
        }
    }

    private boolean doHelpOp(CommandSender sender, String[] args) {
        Chatutils.sendInfo(sender, "===================================== [/Sethomes] HELP =====================================");
        Chatutils.sendInfo(sender, "/sethome : Sets a players home at the standing location.");
        Chatutils.sendInfo(sender, "usage: /<command>");
        Chatutils.sendInfo(sender, "/homes : Lists all of a players homes.");
        Chatutils.sendInfo(sender, "usage: /<command>");
        Chatutils.sendInfo(sender, "/delhome : Deletes a home from the list of players homes.");
        Chatutils.sendInfo(sender, "usage: /<command>");
        Chatutils.sendInfo(sender, "/home : Teleports a player to their home.");
        Chatutils.sendInfo(sender, "usage: /<command>");
        Chatutils.sendInfo(sender, "/strike : Strike fury into your enemies with your wand.");
        Chatutils.sendInfo(sender, "usage: /<command>");
        Chatutils.sendInfo(sender, "/blacklist : Blacklist of worlds to disallow home setting in.");
        Chatutils.sendInfo(sender, "usage: /<command>");
        Chatutils.sendInfo(sender, "/home-of : Teleports the player sending the command to any players home.");
        Chatutils.sendInfo(sender, "usage: /<command>");
        Chatutils.sendInfo(sender, "/delhome-of : Gives a player the ability to delete a home from the list of any players home.");
        Chatutils.sendInfo(sender, "usage: /<command>");
        Chatutils.sendInfo(sender, "/uhome : Allows a player to update their home by home name.");
        Chatutils.sendInfo(sender, "usage: /<command>");
        Chatutils.sendInfo(sender, "/uhome-of : Gives a player the ability to update any players home.");
        Chatutils.sendInfo(sender, "usage: /<command>");
        Chatutils.sendInfo(sender, "/setmax : Set the max number of homes allowed for a group.");
        Chatutils.sendInfo(sender, "usage: /<command>");
        return true;
    }

    private boolean doHelp(CommandSender sender, String[] args) {
        Chatutils.sendInfo(sender, "==================== [/Sethomes] HELP ====================");
        Chatutils.sendInfo(sender, "/sethome : Sets a players home at the standing location.");
        Chatutils.sendInfo(sender, "  usage: /<command>");
        Chatutils.sendInfo(sender, "/delhome : Deletes a home from the list of players homes.");
        Chatutils.sendInfo(sender, "  usage: /<command>");
        Chatutils.sendInfo(sender, "/home : Teleports a player to their home.");
        Chatutils.sendInfo(sender, "  usage: /<command>");
        Chatutils.sendInfo(sender, "/uhome : Allows a player to update their home by home name");
        Chatutils.sendInfo(sender, "  usage: /<command>");
        return true;
    }
}
