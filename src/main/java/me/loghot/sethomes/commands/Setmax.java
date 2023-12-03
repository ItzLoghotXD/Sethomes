package me.loghot.sethomes.commands;

import me.loghot.sethomes.Sethomes;
import me.loghot.sethomes.utils.Chatutils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Setmax implements CommandExecutor {
    private final Sethomes pl;

    public Setmax(Sethomes plugin) {
        this.pl = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getLabel().equalsIgnoreCase("setmax")) {
            if (!(sender instanceof Player)) {
                Chatutils.notPlayerError(sender);
                Player p = (Player)sender;
                if (!p.hasPermission("homes.setmax")) {
                    Chatutils.permissionError(p);
                    return true;
                }
            }

            return this.doSetMaxHomes(sender, args);
        } else {
            return false;
        }
    }

    private void setmaxHomes(String group, int num) {
        this.pl.getConfig().set("max-homes." + group, num);
        this.pl.saveConfig();
    }

    private boolean doSetMaxHomes(CommandSender sender, String[] args) {
        int l = args.length;
        boolean arg_count_check = l != 2;
        if (arg_count_check) {
            Chatutils.sendError(sender, "Wrong number of arguments passed!");
            return false;
        } else {
            String group = args[0];

            try {
                int homeNum = Integer.parseInt(args[1]);
                this.setmaxHomes(group, homeNum);
                Chatutils.sendSuccess(sender, "You have set the max homes to be '" + homeNum + "' for the group '" + group + "'!");
                return true;
            } catch (NumberFormatException var8) {
                Chatutils.sendError(sender, "The second argument must be a number!");
                return false;
            }
        }
    }
}
