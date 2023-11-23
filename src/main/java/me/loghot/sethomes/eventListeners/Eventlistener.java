package me.loghot.sethomes.eventListeners;

import me.loghot.sethomes.Sethomes;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Set;

public class Eventlistener implements Listener {
    public Eventlistener(Sethomes plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteractBlock(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null && itemMeta.getLocalizedName().equalsIgnoreCase("almighty")) {
            p.getWorld().strikeLightning(p.getTargetBlock((Set)null, 200).getLocation());
        }

    }
}
