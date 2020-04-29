package bonn2.votemanager.listeners;

import bonn2.votemanager.Main;
import bonn2.votemanager.inventories.EditElectionInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.Plugin;

public class InventoryCloseListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Main plugin = Main.plugin;
        Player player = (Player) event.getPlayer();
        if (Main.openEditElectionInventories.containsKey(player.getUniqueId())) {
            Main.openEditElectionInventories.remove(player.getUniqueId());
        }
        if (Main.openEditStartTimeInventories.containsKey(player.getUniqueId())) {
            EditElectionInventory editElectionInventory = new EditElectionInventory(Main.openEditStartTimeInventories.get(player.getUniqueId()).getElection());
            Main.openEditStartTimeInventories.remove(player.getUniqueId());
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> editElectionInventory.open(player), 4L);
        }
    }
}