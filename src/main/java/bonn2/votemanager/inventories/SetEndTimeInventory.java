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
import java.util.Date;
import java.util.List;

public class SetEndTimeInventory {

    Main plugin = Main.plugin;
    private final Inventory inventory;
    private final Election election;

    public SetEndTimeInventory(Election election) {
        this.election = election;
        String title = colorize("&6Set end time");
        inventory = plugin.getServer().createInventory(null, 9, title);

        if (!election.usingEndTime()) { election.setEndTime(new Date()); }

        loadItems();
    }

    public void loadItems() {
        ItemStack second = new ItemStack(Material.CLOCK);
        ItemMeta secondMeta = second.getItemMeta();
        secondMeta.setDisplayName(colorize("&9Increment seconds"));
        List<String> secondLore = new ArrayList<>();
        secondLore.add(colorize("&3Left click to decrease"));
        secondLore.add(colorize("&3Right click to increase"));
        secondLore.add(colorize("&1Shift to increment by 10"));
        secondLore.add(colorize("&a") + election.getEndTime().toString());
        secondMeta.setLore(secondLore);
        second.setItemMeta(secondMeta);
        second = NBTEditor.set(second, "second", "VoteManager");

        ItemStack minute = new ItemStack(Material.CLOCK);
        ItemMeta minuteMeta = minute.getItemMeta();
        minuteMeta.setDisplayName(colorize("&9Increment minutes"));
        List<String> minuteLore = new ArrayList<>();
        minuteLore.add(colorize("&3Left click to decrease"));
        minuteLore.add(colorize("&3Right click to increase"));
        minuteLore.add(colorize("&1Shift to increment by 10"));
        minuteLore.add(colorize("&a") + election.getEndTime().toString());
        minuteMeta.setLore(minuteLore);
        minute.setItemMeta(minuteMeta);
        minute = NBTEditor.set(minute, "minute", "VoteManager");

        ItemStack hour = new ItemStack(Material.CLOCK);
        ItemMeta hourMeta = hour.getItemMeta();
        hourMeta.setDisplayName(colorize("&9Increment hours"));
        List<String> hourLore = new ArrayList<>();
        hourLore.add(colorize("&3Left click to decrease"));
        hourLore.add(colorize("&3Right click to increase"));
        hourLore.add(colorize("&1Shift to increment by 10"));
        hourLore.add(colorize("&a") + election.getEndTime().toString());
        hourMeta.setLore(hourLore);
        hour.setItemMeta(hourMeta);
        hour = NBTEditor.set(hour, "hour", "VoteManager");

        ItemStack day = new ItemStack(Material.CLOCK);
        ItemMeta dayMeta = day.getItemMeta();
        dayMeta.setDisplayName(colorize("&9Increment days"));
        List<String> dayLore = new ArrayList<>();
        dayLore.add(colorize("&3Left click to decrease"));
        dayLore.add(colorize("&3Right click to increase"));
        dayLore.add(colorize("&1Shift to increment by 10"));
        dayLore.add(colorize("&a") + election.getEndTime().toString());
        dayMeta.setLore(dayLore);
        day.setItemMeta(dayMeta);
        day = NBTEditor.set(day, "day", "VoteManager");

        ItemStack week = new ItemStack(Material.CLOCK);
        ItemMeta weekMeta = week.getItemMeta();
        weekMeta.setDisplayName(colorize("&9Increment weeks"));
        List<String> weekLore = new ArrayList<>();
        weekLore.add(colorize("&3Left click to decrease"));
        weekLore.add(colorize("&3Right click to increase"));
        weekLore.add(colorize("&1Shift to increment by 10"));
        weekLore.add(colorize("&a") + election.getEndTime().toString());
        weekMeta.setLore(weekLore);
        week.setItemMeta(weekMeta);
        week = NBTEditor.set(week, "week", "VoteManager");

        ItemStack year = new ItemStack(Material.CLOCK);
        ItemMeta yearMeta = year.getItemMeta();
        yearMeta.setDisplayName(colorize("&9Increment year"));
        List<String> yearLore = new ArrayList<>();
        yearLore.add(colorize("&3Left clock to decrease"));
        yearLore.add(colorize("&3Right click to increase"));
        yearLore.add(colorize("&1Shift to increment by 10"));
        yearLore.add(colorize("&a") + election.getEndTime().toString());
        yearMeta.setLore(yearLore);
        year.setItemMeta(yearMeta);
        year = NBTEditor.set(year, "year", "VoteManager");

        ItemStack disable = new ItemStack(Material.BARRIER);
        ItemMeta disableMeta = disable.getItemMeta();
        disableMeta.setDisplayName(colorize("&4Disable End Time"));
        List<String> disableLore = new ArrayList<>();
        disableLore.add(colorize("&cClick to disable End Time"));
        disableMeta.setLore(disableLore);
        disable.setItemMeta(disableMeta);
        disable = NBTEditor.set(disable, "disable", "VoteManager");

        inventory.setItem(1, second);
        inventory.setItem(2, minute);
        inventory.setItem(3, hour);
        inventory.setItem(4, day);
        inventory.setItem(5, week);
        inventory.setItem(6, year);
        inventory.setItem(7, disable);
    }

    public void open(Player player) {
        Main.openEditEndTimeInventories.put(player.getUniqueId(), this);
        player.openInventory(inventory);
    }

    public void increment(int seconds) {
        election.incrementEndTime(seconds);
        loadItems();
    }

    public void incrementYear(int year) {
        election.incrementEndTimeYear(year);
        loadItems();
    }

    public void disableTime() {
        election.removeEndTime();
    }

    public Election getElection() {
        return election;
    }

    public void saveElection() {
        try {
            election.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String colorize(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
