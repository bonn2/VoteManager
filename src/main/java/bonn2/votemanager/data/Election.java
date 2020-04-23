package bonn2.votemanager.data;

import bonn2.votemanager.Main;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Election {
    Main plugin = Main.plugin;
    private Map<UUID, List<String>> votes;
    private int maxVotes;
    private String name;
    private Map<String, List<Location>> candidateButtons;
    private Date startTime;
    private boolean useStartTime;
    private Date endTime;
    private boolean useEndTime;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public Election(String name) {
        votes = new HashMap<>();
        candidateButtons = new HashMap<>();
        maxVotes = 1;
        this.name = name;
        useStartTime = false;
        useEndTime = false;
    }

    public Election(YamlConfiguration yml, String name) {
        this.name = name;
        maxVotes = yml.getInt("MaxVotes");
        System.out.println("Loading maxvotes as " + maxVotes);
        votes = new HashMap<>();
        candidateButtons = new HashMap<>();
        useStartTime = yml.getBoolean("UseStartTime");
        useEndTime = yml.getBoolean("UseEndTime");
        if (useStartTime) { startTime = (Date) yml.get("StartTime"); }
        if (useEndTime) { endTime = (Date) yml.get("EndTime"); }
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
        yml.set("UseStartTime", useStartTime);
        yml.set("UseEndTime", useEndTime);
        if (useStartTime) { yml.set("StartTime", startTime); }
        if (useEndTime) { yml.set("EndTime", endTime); }
        for (String name : candidateButtons.keySet()) { yml.set("CandidateButtons." + name, candidateButtons.get(name)); }
        for (UUID uuid : votes.keySet()) { yml.set("Votes." + uuid.toString(), votes.get(uuid)); }
        yml.save(ymlFile);
    }

    public void delete() {
        Main.loadedElections.remove(name);
        String ext = ".yml";
        File ymlFile = new File(plugin.getDataFolder() + File.separator + "Elections" + File.separator + name + ext);
        ymlFile.delete();
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
        if (!isActive()) {
            voter.sendMessage("Election is not currently active");
            return;
        }
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

    public void setStartTime(String time) throws ParseException {
        useStartTime = true;
        startTime = timeFormat.parse(time);
    }
    public Date getStartTime() { return startTime; }
    public void removeStartTime() { useStartTime = false; }

    public void setEndTime(String time) throws ParseException {
        useEndTime = true;
        endTime = timeFormat.parse(time);
    }
    public Date getEndTime() { return endTime; }
    public void removeEndTime() { useEndTime = false; }

    public boolean isActive() {  //TODO: Fix time system!!!!
        if (useStartTime && useEndTime) {
            if (startTime.before(new Date()) && endTime.after(new Date())) {
                return true;
            } else {
                return false;
            }
        } else if (useStartTime) {
            if (startTime.before(new Date())) {
                return true;
            } else {
                return false;
            }
        } else if (useEndTime) {
            if (endTime.after(new Date())) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
}
