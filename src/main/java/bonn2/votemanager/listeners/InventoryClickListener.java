package bonn2.votemanager.listeners;

import bonn2.votemanager.data.InputType;
import bonn2.votemanager.Main;
import bonn2.votemanager.util.NBTEditor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (Main.openEditors.containsKey(player.getUniqueId())) {
            InventoryView inventoryView = event.getView();
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null) {
                switch (NBTEditor.getString(clickedItem, "VoteManager")) {
                    case "maxVotes": {
                        clickedMaxVotes(player, inventoryView);
                        event.setCancelled(true);
                        return;
                    }
                    case "autoEnd": {
                        clickedAutoEnd(player, inventoryView);
                        event.setCancelled(true);
                        return;
                    }
                    case "addButton": {
                        clickedAddButton(player, inventoryView);
                        event.setCancelled(true);
                        return;
                    }
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    private void clickedAddButton(Player player, InventoryView inventoryView) {
        player.setItemOnCursor(new ItemStack(Material.AIR));
        player.sendMessage("Enter the name of the candidate.");
        Main.awaitingPlayers.put(player.getUniqueId(), InputType.CANDIDATE_NAME);
        Main.awaitingPlayersElection.put(player.getUniqueId(), Main.openEditors.get(player.getUniqueId()).getElection());
        inventoryView.close();
    }

    private void clickedMaxVotes(Player player, InventoryView inventoryView) {
        player.setItemOnCursor(new ItemStack(Material.AIR));
        player.sendMessage("Enter the number of votes each player is allowed.");
        Main.awaitingPlayers.put(player.getUniqueId(), InputType.MAX_VOTES);
        Main.awaitingPlayersElection.put(player.getUniqueId(), Main.openEditors.get(player.getUniqueId()).getElection());
        inventoryView.close();
    }

    private void clickedAutoEnd(Player player, InventoryView inventoryView) {
        player.setItemOnCursor(new ItemStack(Material.AIR));
        player.sendMessage("Enter the date and time for the election to end");
        player.sendMessage("in the format: dd-MM-yyyy HH:mm:ss");
        Main.awaitingPlayers.put(player.getUniqueId(), InputType.AUTO_END);
        Main.awaitingPlayersElection.put(player.getUniqueId(), Main.openEditors.get(player.getUniqueId()).getElection());
        inventoryView.close();
    }
}
