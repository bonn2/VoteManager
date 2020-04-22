package bonn2.votemanager.listeners;

import bonn2.votemanager.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (Main.openEditors.containsKey(player.getUniqueId())) {
            System.out.println("Removing player from edit session!");
            Main.openEditors.remove(player.getUniqueId());
        }
    }
}
