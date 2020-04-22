package bonn2.votemanager.inventories;

import bonn2.votemanager.Main;
import bonn2.votemanager.data.Election;
import bonn2.votemanager.util.NBTEditor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EditElectionInventory {

    Main plugin = Main.plugin;
    private Inventory inventory;
    private String title;
    private Election election;

    public EditElectionInventory(Election election) { // TODO: Make pretty
        this.election = election;
        title = ChatColor.DARK_GREEN + election.getName();
        inventory = plugin.getServer().createInventory(null, 27, title);

        ItemStack maxVotes = new ItemStack(Material.PURPLE_DYE);
        ItemMeta maxVotesMeta = maxVotes.getItemMeta();
        maxVotesMeta.setDisplayName("Max Votes: " + election.getMaxVotes());
        List<String> maxVotesLore = new ArrayList<>();
        maxVotesLore.add("Default = 1");
        maxVotesMeta.setLore(maxVotesLore);
        maxVotes.setItemMeta(maxVotesMeta);
        maxVotes = NBTEditor.set(maxVotes, "maxVotes", "VoteManager");

        ItemStack autoEnd = new ItemStack(Material.RED_DYE);
        ItemMeta autoEndMeta = autoEnd.getItemMeta();
        autoEndMeta.setDisplayName("Auto End Election: Disabled");
        List<String> autoEndLore = new ArrayList<>();
        autoEndLore.add("Default = Disabled");
        autoEndMeta.setLore(autoEndLore);
        autoEnd.setItemMeta(autoEndMeta);
        autoEnd = NBTEditor.set(autoEnd, "autoEnd", "VoteManager");

        ItemStack addButton = new ItemStack(Material.BLUE_DYE);
        ItemMeta addButtonMeta = addButton.getItemMeta();
        addButtonMeta.setDisplayName("Add Candidate Button");
        addButton.setItemMeta(addButtonMeta);
        addButton = NBTEditor.set(autoEnd, "addButton", "VoteManager");

        inventory.setItem(11, maxVotes);
        inventory.setItem(13, autoEnd);
        inventory.setItem(15, addButton);
    }

    public void open(Player player) {
        Main.openEditors.put(player.getUniqueId(), this);
        player.openInventory(inventory);
    }

    public Election getElection() { return election; }
}
