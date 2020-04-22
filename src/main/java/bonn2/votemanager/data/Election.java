package bonn2.votemanager.data;

import bonn2.votemanager.Main;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Election {
    Main plugin = Main.plugin;
    private Map<UUID, List<String>> votes;
    private int maxVotes;
    private String name;
    private Map<String, List<Location>> candidateButtons;

    public Election(String name) {
        votes = new HashMap<>();
        candidateButtons = new HashMap<>();
        maxVotes = 1;
        this.name = name;
    }

    public Election(YamlConfiguration yml, String name) {
        this.name = name;
        maxVotes = yml.getInt("MaxVotes");
        System.out.println("Loading maxvotes as " + maxVotes);
        votes = new HashMap<>();
        candidateButtons = new HashMap<>();
        try {
            for (String key : yml.getConfigurationSection("CandidateButtons").getKeys(false)) {
                candidateButtons.put(key, (List<Location>) yml.getList("CandidateButtons." + key));
            }
        }
        catch (ClassCastException e) {
            plugin.getLogger().info("Invalid button in election " + name);
        }
        catch (NullPointerException ignored) {}
        try {
            for (String key : yml.getConfigurationSection("Votes").getKeys(false)) {
                votes.put(UUID.fromString(key), yml.getStringList("Votes." + key));
            }
        }
        catch (NullPointerException ignored) {}
    }

    public void save() throws IOException {
        Main.loadedElections.put(name, this);
        String ext = ".yml";
        File ymlFile = new File(plugin.getDataFolder() + File.separator + "Elections" + File.separator + name + ext);
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(ymlFile);
        yml.set("MaxVotes", maxVotes);
        for (String name : candidateButtons.keySet()) { yml.set("CandidateButtons." + name, candidateButtons.get(name)); }
        for (UUID uuid : votes.keySet()) { yml.set("Votes." + uuid.toString(), votes.get(uuid)); }
        yml.save(ymlFile);
    }

    public void addCandidateButton(String name, Location location) {
        if (!candidateButtons.containsKey(name)) {
            List<Location> locations = new ArrayList<>();
            locations.add(location);
            candidateButtons.put(name, locations);
        } else {
            List<Location> locations = candidateButtons.get(name);
            locations.add(location);
            candidateButtons.put(name, locations);
        }
    }

    public void setMaxVotes(int max) { maxVotes = max; }
    public int getMaxVotes() { return maxVotes; }

    public String getName() { return name; }

    public void hitButtonAt(Location location, Player player) {
        for(String candidate : candidateButtons.keySet()) {
            List<Location> locations = candidateButtons.get(candidate);
            if (locations.contains(location)) {
                addVote(player, candidate);
                try {
                    save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Map<String, Integer> getTotals() {
        Map<String, Integer> totals = new HashMap<>();
        for (String candidate : candidateButtons.keySet()) {
            totals.put(candidate, 0);
        }
        for (UUID uuid : votes.keySet()) {
            for (String vote : votes.get(uuid)) {
                totals.put(vote, totals.getOrDefault(vote, 0) + 1);
            }
        }
        return totals;
    }

    private void addVote(Player voter, String vote) {
        if (!votes.containsKey(voter.getUniqueId())) {
            List<String> list = new ArrayList<>();
            list.add(vote);
            votes.put(voter.getUniqueId(), list);
            voter.sendMessage("You successfully voted for " + vote);
        }
        else if (votes.get(voter.getUniqueId()).size() < maxVotes && !votes.get(voter.getUniqueId()).contains(vote)) {
            List<String> list = votes.get(voter.getUniqueId());
            list.add(vote);
            votes.put(voter.getUniqueId(), list);
            voter.sendMessage("You successfully voted for " + vote);
        }
        else {
            voter.sendMessage("You have already voted " + maxVotes + " time/s");
        }
    }
}
