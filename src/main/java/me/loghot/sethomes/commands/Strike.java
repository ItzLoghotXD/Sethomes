package me.loghot.sethomes.commands;

import me.loghot.sethomes.Sethomes;
import me.loghot.sethomes.utils.Chatutils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Strike implements CommandExecutor {
    public Strike(Sethomes plugin) {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Chatutils.notPlayerError(sender);
            return false;
        } else if (cmd.getName().equalsIgnoreCase("strike")) {
            Player p = (Player)sender;
            if (p.hasPermission("home.strike")) {
                PlayerInventory pInventory = p.getInventory();
                ItemStack fishingRod = new ItemStack(Material.FISHING_ROD, 1);
                ItemMeta rodMeta = fishingRod.getItemMeta();
                List<String> rodLore = new ArrayList();
                rodLore.add(ChatColor.DARK_RED + "The power of the almighty is in your hands now!");
                rodMeta.setLore(rodLore);
                rodMeta.setDisplayName(ChatColor.BLUE + "The Almighty!");
                rodMeta.setLocalizedName("almighty");
                fishingRod.setItemMeta(rodMeta);
                pInventory.addItem(new ItemStack[]{fishingRod});
            } else {
                Chatutils.permissionError(p);
            }

            return true;
        } else {
            return false;
        }
    }
}
