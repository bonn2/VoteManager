package bonn2.votemanager;

import bonn2.votemanager.commands.MainCommand;
import bonn2.votemanager.commands.MainCommandTabComplete;
import bonn2.votemanager.data.Candidate;
import bonn2.votemanager.data.Election;
import bonn2.votemanager.data.InputType;
import bonn2.votemanager.inventories.EditElectionInventory;
import bonn2.votemanager.listeners.AsyncChatListener;
import bonn2.votemanager.listeners.InventoryClickListener;
import bonn2.votemanager.listeners.InventoryCloseListener;
import bonn2.votemanager.listeners.PlayerInteractListener;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public final class Main extends JavaPlugin {

    public static Main plugin;
    public static Map<UUID, EditElectionInventory> openEditors;
    public static Map<UUID, InputType> awaitingPlayers;
    public static Map<UUID, Election> awaitingPlayersElection;
    public static Map<UUID, Candidate> awaitingCandidateButtonSelection;
    public static Map<String, Election> loadedElections;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        initGlobalVars();

        getServer().getPluginManager().registerEvents(new AsyncChatListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryCloseListener(), this);
        Objects.requireNonNull(this.getCommand("votemanager")).setExecutor(new MainCommand());
        Objects.requireNonNull(this.getCommand("votemanager")).setTabCompleter(new MainCommandTabComplete());

        loadElections();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static void initGlobalVars() {
        openEditors = new HashMap<>();
        awaitingPlayers = new HashMap<>();
        awaitingPlayersElection = new HashMap<>();
        awaitingCandidateButtonSelection = new HashMap<>();
        loadedElections = new HashMap<>();
    }

    public static void loadElections() {
        File folder = new File(plugin.getDataFolder() + File.separator + "Elections" + File.separator);
        File[] listOfFiles = folder.listFiles();

        loadedElections = new HashMap<>(); // Empties loadedVotes

        if (listOfFiles == null) { return; }

        for (File file : listOfFiles) {
            if (file.isFile()) {
                String filename = file.getName().substring(0, file.getName().lastIndexOf('.'));
                String ext = file.getName().substring(file.getName().lastIndexOf('.') + 1);
                if (ext.equals("yml")) {
                    plugin.getLogger().info("Loading " + filename + "." + ext);
                    loadedElections.put(filename, new Election(YamlConfiguration.loadConfiguration(file), filename));
                }
            }
        }
    }
}
