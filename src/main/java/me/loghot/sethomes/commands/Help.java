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
                    return this.doHelpOp(sender);
                } else {
                    return this.doHelp(sender);
                }
            }
        }
        return false;
    }

    private boolean doHelpOp(CommandSender sender) {
        Chatutils.sendInfo(sender, "========== [/Sethomes HELP] ==========");
        Chatutils.sendInfo(sender, "/sethome : Sets a players home at the standing location.");
        Chatutils.sendInfo(sender, "usage: /sethome [HomeName] [Description of Home] ");
        Chatutils.sendInfo(sender, "example usage: /sethome MyHome this is my beach house");

        Chatutils.sendInfo(sender, "/homes : Lists all of a players homes.");
        Chatutils.sendInfo(sender, "usage: /homes [PlayerName]");
        Chatutils.sendInfo(sender, "example usage: /homes Loghot_Gamerz");

        Chatutils.sendInfo(sender, "/delhome : Deletes a home from the list of players homes.");
        Chatutils.sendInfo(sender, "usage: /delhome [HomeName]");
        Chatutils.sendInfo(sender, "example usage: /delhome MyHome");

        Chatutils.sendInfo(sender, "/home : Teleports a player to their home.");
        Chatutils.sendInfo(sender, "usage: /home [HomeName]");
        Chatutils.sendInfo(sender, "example usage: /home MyHome");

        Chatutils.sendInfo(sender, "/strike : Strike fury into your enemies with your wand.");
        Chatutils.sendInfo(sender, "usage: /strike");
        Chatutils.sendInfo(sender, "example usage: /strike");

        Chatutils.sendInfo(sender, "/blacklist : Blacklist of worlds to disallow home setting in.");
        Chatutils.sendInfo(sender, "usage: /blacklist [Add/Remove] [WorldName]");
        Chatutils.sendInfo(sender, "example usage: /blacklist add Nether");

        Chatutils.sendInfo(sender, "/home-of : Teleports the player to any player's home.");
        Chatutils.sendInfo(sender, "usage: /home-of [PlayerName] [HomeName]");
        Chatutils.sendInfo(sender, "example usage: /home-of Loghot_Gamerz MyHome");

        Chatutils.sendInfo(sender, "/delhome-of : Gives a player the ability to delete a home from the list of any players home.");
        Chatutils.sendInfo(sender, "usage: /delhome-of [PlayerName] [HomeName]");
        Chatutils.sendInfo(sender, "example usage: /delhome-of Loghot_Gamerz MyHome");

        Chatutils.sendInfo(sender, "/uhome : Allows a player to update their home by home name.");
        Chatutils.sendInfo(sender, "usage: /uhome [HomeName] [Home Description]");
        Chatutils.sendInfo(sender, "example usage: /uhome MyHome this is my main house");

        Chatutils.sendInfo(sender, "/uhome-of : Gives a player the ability to update any players home.");
        Chatutils.sendInfo(sender, "usage: /uhome-of [PlayerName] [HomeName]");
        Chatutils.sendInfo(sender, "example usage: /uhome-of Loghot_Gamerz MyHome");

        Chatutils.sendInfo(sender, "/setmax : Set the max number of homes allowed for a group.");
        Chatutils.sendInfo(sender, "usage: /setmax [GroupName] [Amount]");
        Chatutils.sendInfo(sender, "example usage: /setmax Default 2");
        Chatutils.sendInfo(sender, "example usage: /setmax VIP 5");
        return true;
    }

    private boolean doHelp(CommandSender sender) {
        Chatutils.sendInfo(sender, "========== [/Sethomes HELP] ==========");
        Chatutils.sendInfo(sender, "/sethome : Sets a players home at the standing location.");
        Chatutils.sendInfo(sender, "usage: /sethome [HomeName] [Description of Home] ");
        Chatutils.sendInfo(sender, "example usage: /sethome MyHome this is my beach house");

        Chatutils.sendInfo(sender, "/homes : Lists all of a players homes.");
        Chatutils.sendInfo(sender, "usage: /homes [PlayerName]");
        Chatutils.sendInfo(sender, "example usage: /homes Loghot_Gamerz");

        Chatutils.sendInfo(sender, "/delhome : Deletes a home from the list of players homes.");
        Chatutils.sendInfo(sender, "usage: /delhome [HomeName]");
        Chatutils.sendInfo(sender, "example usage: /delhome MyHome");

        Chatutils.sendInfo(sender, "/home : Teleports a player to their home.");
        Chatutils.sendInfo(sender, "usage: /home [HomeName]");
        Chatutils.sendInfo(sender, "example usage: /home MyHome");

        Chatutils.sendInfo(sender, "/strike : Strike fury into your enemies with your wand.");
        Chatutils.sendInfo(sender, "usage: /strike");
        Chatutils.sendInfo(sender, "example usage: /strike");

        Chatutils.sendInfo(sender, "/home-of : Teleports the player to any player's home.");
        Chatutils.sendInfo(sender, "usage: /home-of [PlayerName] [HomeName]");
        Chatutils.sendInfo(sender, "example usage: /home-of Loghot_Gamerz MyHome");

        Chatutils.sendInfo(sender, "/uhome : Allows a player to update their home by home name.");
        Chatutils.sendInfo(sender, "usage: /uhome [HomeName] [Home Description]");
        Chatutils.sendInfo(sender, "example usage: /uhome MyHome this is my main house");
        return true;
    }
}
