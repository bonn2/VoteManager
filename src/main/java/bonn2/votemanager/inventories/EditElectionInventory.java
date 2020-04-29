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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditElectionInventory {

    Main plugin = Main.plugin;
    private final Inventory inventory;
    private final Election election;

    public EditElectionInventory(Election election) { // TODO: Make pretty
        this.election = election;
        String title = ChatColor.DARK_GREEN + election.getName();
        inventory = plugin.getServer().createInventory(null, 9, title);

        loadItems();
    }

    public void loadItems() {
        ItemStack maxVotes = new ItemStack(Material.REPEATER);
        ItemMeta maxVotesMeta = maxVotes.getItemMeta();
        maxVotesMeta.setDisplayName(colorize("&9Max Votes: &2") + election.getMaxVotes());
        List<String> maxVotesLore = new ArrayList<>();
        maxVotesLore.add(colorize("&3Left click to decrease"));
        maxVotesLore.add(colorize("&3Right click to increase"));
        maxVotesLore.add(colorize("&1Shift to increment by 10"));
        maxVotesMeta.setLore(maxVotesLore);
        maxVotes.setItemMeta(maxVotesMeta);
        maxVotes = NBTEditor.set(maxVotes, "maxVotes", "VoteManager");

        ItemStack autoStart = new ItemStack(Material.CLOCK);
        ItemMeta autoStartMeta = autoStart.getItemMeta();
        if (!election.usingStartTime()) { autoStartMeta.setDisplayName(colorize("&9Auto Start Election: &cDisabled")); }
        else { autoStartMeta.setDisplayName(colorize("&9Auto Start Election: &2") + election.getStartTime().toString()); }
        List<String> autoStartLore = new ArrayList<>();
        autoStartLore.add(colorize("&bLeft click to set"));
        autoStartLore.add(colorize("&bRight click to disable"));
        autoStartMeta.setLore(autoStartLore);
        autoStart.setItemMeta(autoStartMeta);
        autoStart = NBTEditor.set(autoStart, "autoStart", "VoteManager");

        ItemStack autoEnd = new ItemStack(Material.CLOCK);
        ItemMeta autoEndMeta = autoEnd.getItemMeta();
        if (!election.usingEndTime()) { autoEndMeta.setDisplayName(colorize("&9Auto End Election: &cDisabled")); }
        else { autoEndMeta.setDisplayName(colorize("&9Auto End Election: &2") + election.getEndTime().toString()); }
        List<String> autoEndLore = new ArrayList<>();
        autoEndLore.add(colorize("&bLeft click to set"));
        autoEndLore.add(colorize("&bRight click to disable"));
        autoEndMeta.setLore(autoEndLore);
        autoEnd.setItemMeta(autoEndMeta);
        autoEnd = NBTEditor.set(autoEnd, "autoEnd", "VoteManager");

        ItemStack addButton = new ItemStack(Material.STONE_BUTTON);
        ItemMeta addButtonMeta = addButton.getItemMeta();
        addButtonMeta.setDisplayName(colorize("&9Candidate Buttons"));
        List<String> addButtonLore = new ArrayList<>();
        addButtonLore.add(colorize("&bLeft click to add"));
        addButtonLore.add(colorize("&bRight click to remove"));
        addButtonMeta.setLore(addButtonLore);
        addButton.setItemMeta(addButtonMeta);
        addButton = NBTEditor.set(addButton, "candidateButton", "VoteManager");

        ItemStack addViewButton = new ItemStack(Material.OAK_BUTTON);
        ItemMeta addViewButtonMeta = addViewButton.getItemMeta();
        addViewButtonMeta.setDisplayName(colorize("&9View Results Buttons"));
        List<String> addViewButtonLore = new ArrayList<>();
        addViewButtonLore.add(colorize("&bLeft click to add"));
        addViewButtonLore.add(colorize("&bRight click to remove"));
        addViewButtonMeta.setLore(addViewButtonLore);
        addViewButton.setItemMeta(addViewButtonMeta);
        addViewButton = NBTEditor.set(addViewButton, "viewButton", "VoteManager");

        ItemStack toggleForceOpen = new ItemStack(Material.GREEN_DYE);
        ItemMeta toggleForceOpenMeta = toggleForceOpen.getItemMeta();
        if (election.isForceOpened()) {
            toggleForceOpenMeta.setDisplayName(colorize("&9Force Open Voting: &aEnabled"));
        } else {
            toggleForceOpenMeta.setDisplayName(colorize("&9Force Open Voting: &cDisabled"));
        }
        List<String> toggleForceOpenLore = new ArrayList<>();
        toggleForceOpenLore.add(colorize("&bClick to toggle force open"));
        toggleForceOpenMeta.setLore(toggleForceOpenLore);
        toggleForceOpen.setItemMeta(toggleForceOpenMeta);
        toggleForceOpen = NBTEditor.set(toggleForceOpen, "forceOpen", "VoteManager");

        ItemStack toggleForceClose = new ItemStack(Material.RED_DYE);
        ItemMeta toggleForceCloseMeta = toggleForceClose.getItemMeta();
        if (election.isForceClosed()) {
            toggleForceCloseMeta.setDisplayName(colorize("&9Force Close Voting: &aEnabled"));
        } else {
            toggleForceCloseMeta.setDisplayName(colorize("&9Force Close Voting: &cDisabled"));
        }
        List<String> toggleForceCloseLore = new ArrayList<>();
        toggleForceCloseLore.add(colorize("&bClick to toggle force close"));
        toggleForceCloseMeta.setLore(toggleForceCloseLore);
        toggleForceClose.setItemMeta(toggleForceCloseMeta);
        toggleForceClose = NBTEditor.set(toggleForceClose, "forceClose", "VoteManager");

        inventory.setItem(0, maxVotes);
        inventory.setItem(1, autoStart);
        inventory.setItem(2, autoEnd);
        inventory.setItem(3, addButton);
        inventory.setItem(4, addViewButton);
        inventory.setItem(5, toggleForceOpen);
        inventory.setItem(6, toggleForceClose);
    }

    public void open(Player player) {
        Main.openEditElectionInventories.put(player.getUniqueId(), this);
        player.openInventory(inventory);
    }

    public void incrementMaxVotes(int num) {
        election.incrementMaxVotes(num);
        loadItems();
    }

    public void saveElection() {
        try {
            election.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toggleForceOpen() {
        if (election.isForceOpened()) {
            election.disableForceOpenClose();
        } else {
            election.forceOpen();
        }
    }

    public void toggleForceClose() {
        if (election.isForceClosed()) {
            election.disableForceOpenClose();
        } else {
            election.forceClose();
        }
    }

    public Election getElection() { return election; }

    public String colorize(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
