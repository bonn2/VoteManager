package bonn2.votemanager.inventories;

import bonn2.votemanager.Main;
import bonn2.votemanager.data.Election;
import bonn2.votemanager.util.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RemoveResultsButtonInventory {

    private final Inventory inventory;
    private final Election election;

    public RemoveResultsButtonInventory(Election election) {
        this.election = election;
        String title = "Remove buttons";
        int numButtons = election.numCandidateButtons();
        int numRows = 0;
        int numButtonsTemp = numButtons;
        while (numButtonsTemp > 0) {
            numRows++;
            numButtonsTemp -= 9;
        }
        if (numRows == 0) {
            numRows = 1;
        }
        inventory = Bukkit.getServer().createInventory(null, numRows * 9, title);

        loadItems();
    }

    public void loadItems() {
        List<Location> resultsButtons = election.getResultsButtons();
        List<ItemStack> contents = new ArrayList<>();
        for (Location location : resultsButtons) {
            ItemStack temp = new ItemStack(Material.STONE_BUTTON);
            ItemMeta tempMeta = temp.getItemMeta();
            assert tempMeta != null;
            tempMeta.setDisplayName(location.getX() + " " + location.getY() + " " + location.getZ());
            temp.setItemMeta(tempMeta);
            temp = NBTEditor.set(temp, location.getBlockX() + "/" + location.getBlockY() + "/" +
                    location.getBlockZ() + "/" + location.getWorld().getName(), "VoteManager");
            contents.add(temp);
        }
        ItemStack[] contentsArray = new ItemStack[contents.size()];
        for (int i = 0; i < contents.size(); i++) {
            contentsArray[i] = contents.get(i);
        }
        inventory.setContents(contentsArray);
    }

    public void open(Player player) {
        Main.openRemoveResultsButtonInventories.put(player.getUniqueId(), this);
        player.openInventory(inventory);
    }

    public void remove(Location location) {
        election.removeResultsButton(location);
    }

    public void saveElection() {
        try {
            election.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
