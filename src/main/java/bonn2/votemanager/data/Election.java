package bonn2.votemanager.data;

import bonn2.votemanager.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Election {
    Main plugin = Main.plugin;
    private final Map<UUID, List<String>> votes;
    private int maxVotes;
    private final String name;
    private final Map<String, List<Location>> candidateButtons;
    private List<Location> resultsButtons;
    private Date startTime;
    private boolean useStartTime;
    private Date endTime;
    private boolean useEndTime;
    private boolean forceOpen;
    private boolean forceClose;

    private String invalidVoteButton = "Invalid button in election %election%";
    private String invalidViewButton = "Invalid view button in election %election%";
    private String outputHeader = "&2Votes:";
    private String totalVotesDescriptor = "&2Total Votes: &r%num%";
    private String totalVotersDescriptor = "&2Total Voters: &r%num%";
    private String electionNotActive = "Election is not currently active";
    private String successfullyVoted = "You successfully voted for %candidate%";
    private String alreadyVoted = "&eYou have already voted for ";
    private String tooManyVotes = "You have already voted %num% time/s";

    public Election(String name) {
        votes = new HashMap<>();
        candidateButtons = new HashMap<>();
        resultsButtons = new ArrayList<>();
        maxVotes = 1;
        this.name = name;
        useStartTime = false;
        useEndTime = false;
        forceOpen = false;
        forceClose = false;
    }

    public Election(YamlConfiguration yml, String name) {
        this.name = name;
        maxVotes = yml.getInt("MaxVotes");
        System.out.println("Loading maxvotes as " + maxVotes);
        votes = new HashMap<>();
        candidateButtons = new HashMap<>();
        resultsButtons = new ArrayList<>();
        useStartTime = yml.getBoolean("UseStartTime");
        useEndTime = yml.getBoolean("UseEndTime");
        forceOpen = yml.getBoolean("ForceOpen");
        forceClose = yml.getBoolean("ForceClose");
        if (forceOpen && forceClose) { disableForceOpenClose(); }
        if (useStartTime) { startTime = (Date) yml.get("StartTime"); }
        if (useEndTime) { endTime = (Date) yml.get("EndTime"); }
        try {
            for (String key : yml.getConfigurationSection("CandidateButtons").getKeys(false)) {
                candidateButtons.put(key, (List<Location>) yml.getList("CandidateButtons." + key));
            }
        }
        catch (ClassCastException e) {
            plugin.getLogger().warning(invalidVoteButton.replaceAll("%election%", name));
        }
        catch (NullPointerException ignored) {}
        try {
            for (String key : yml.getConfigurationSection("Votes").getKeys(false)) {
                votes.put(UUID.fromString(key), yml.getStringList("Votes." + key));
            }
        }
        catch (NullPointerException ignored) {}
        if (yml.get("ResultsButtons") != null) {
            try {
                resultsButtons = (List<Location>) yml.get("ResultsButtons");
            } catch (ClassCastException e) {
                plugin.getLogger().warning(invalidViewButton.replaceAll("%election%", name));
            }
        }
    }

    public void save() throws IOException {
        Main.loadedElections.put(name, this);
        String ext = ".yml";
        File ymlFile = new File(plugin.getDataFolder() + File.separator + "Elections" + File.separator + name + ext);
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(ymlFile);
        yml.set("MaxVotes", maxVotes);
        yml.set("UseStartTime", useStartTime);
        yml.set("UseEndTime", useEndTime);
        yml.set("ForceOpen", forceOpen);
        yml.set("ForceClose", forceClose);
        if (useStartTime) { yml.set("StartTime", startTime); }
        if (useEndTime) { yml.set("EndTime", endTime); }
        yml.set("ResultsButtons", resultsButtons);
        for (String name : candidateButtons.keySet()) { yml.set("CandidateButtons." + name, candidateButtons.get(name)); }
        for (UUID uuid : votes.keySet()) { yml.set("Votes." + uuid.toString(), votes.get(uuid)); }
        yml.save(ymlFile);
    }

    public void delete() {
        Main.loadedElections.remove(name);
        String ext = ".yml";
        new File(plugin.getDataFolder() + File.separator + "Elections" + File.separator + name + ext).delete();
    }

    public void addCandidateButton(String name, Location location) {
        List<Location> locations;
        if (!candidateButtons.containsKey(name)) {
            locations = new ArrayList<>();
        } else {
            locations = candidateButtons.get(name);
        }
        locations.add(location);
        candidateButtons.put(name, locations);
    }

    public Map<String, List<Location>> getCandidateButtons() {
        return candidateButtons;
    }

    public List<Location> getResultsButtons() {
        return resultsButtons;
    }

    public int numCandidateButtons() {
        int total = 0;
        for (String key : candidateButtons.keySet()) {
            for (Location location : candidateButtons.get(key)) {
                total++;
            }
        }
        return total;
    }

    public void removeCandidateButton(String candidate, Location location) {
        List<Location> locations = candidateButtons.get(candidate);
        locations.remove(location);
    }

    public void addResultsButton(Location location) {
        if (!resultsButtons.contains(location)) {
            resultsButtons.add(location);
        }
    }

    public void removeResultsButton(Location location) {
        resultsButtons.remove(location);
    }

    public void incrementMaxVotes(int num) { maxVotes += num; }
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
        if (resultsButtons.contains(location)) {
            List<String> messages = getTotals();
            for (String message : messages) {
                player.sendMessage(colorize(message));
            }
        }
    }

    public List<String> getTotals() {
        Map<String, Integer> totals = new HashMap<>();
        List<String> output = new ArrayList<>();
        int totalVotes = 0;
        int totalVoters = 0;
        output.add(outputHeader);
        for (String candidate : candidateButtons.keySet()) {
            totals.put(candidate, 0);
        }
        for (UUID uuid : votes.keySet()) {
            totalVoters++;
            for (String vote : votes.get(uuid)) {
                totals.put(vote, totals.getOrDefault(vote, 0) + 1);
                totalVotes++;
            }
        }
        for (String candidate : totals.keySet()) {
            output.add("&e" + candidate + ": " + totals.get(candidate));
        }
        output.add(colorize(totalVotesDescriptor.replaceAll("%num%", totalVoters + "")));
        output.add(colorize(totalVotersDescriptor.replaceAll("%num%", totalVoters + "")));
        return output;
    }

    private void addVote(Player voter, String vote) {
        if (!isActive()) {
            voter.sendMessage(electionNotActive);
            return;
        }
        if (!votes.containsKey(voter.getUniqueId())) {
            List<String> list = new ArrayList<>();
            list.add(vote);
            votes.put(voter.getUniqueId(), list);
            voter.sendMessage(colorize(successfullyVoted.replaceAll("%candidate%", vote)));
        }
        else if (votes.get(voter.getUniqueId()).size() < maxVotes && !votes.get(voter.getUniqueId()).contains(vote)) {
            List<String> list = votes.get(voter.getUniqueId());
            list.add(vote);
            votes.put(voter.getUniqueId(), list);
            voter.sendMessage(colorize(successfullyVoted.replaceAll("%candidate%", vote)));
        }
        else if (votes.get(voter.getUniqueId()).contains(vote)) {
            String message = colorize(alreadyVoted);
            for (String string : votes.get(voter.getUniqueId())) {
                message = message + string + " ";
            }
            voter.sendMessage(message);
        }
        else {
            voter.sendMessage(colorize(tooManyVotes.replaceAll("%num%", maxVotes + "")));
        }
    }

    public void setStartTime(Date time) {
        useStartTime = true;
        startTime = time;
    }
    public Date getStartTime() { return startTime; }
    public void removeStartTime() { useStartTime = false; }
    public boolean usingStartTime() { return useStartTime; }

    public void setEndTime(Date time) {
        useEndTime = true;
        endTime = time;
    }
    public Date getEndTime() { return endTime; }
    public void removeEndTime() { useEndTime = false; }
    public boolean usingEndTime() { return useEndTime; }

    public void incrementStartTime(int seconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startTime);
        cal.add(Calendar.SECOND, seconds);
        startTime = cal.getTime();
    }

    public void incrementStartTimeYear(int years) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startTime);
        cal.add(Calendar.YEAR, years);
        startTime = cal.getTime();
    }

    public void incrementEndTime(int seconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(endTime);
        cal.add(Calendar.SECOND, seconds);
        endTime = cal.getTime();
    }

    public void incrementEndTimeYear(int years) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(endTime);
        cal.add(Calendar.YEAR, years);
        endTime = cal.getTime();
    }

    public boolean isActive() {
        if (forceClose) {
            return false;
        } else if (forceOpen) {
            return true;
        }
        if (useStartTime && useEndTime) {
            return startTime.before(new Date()) && endTime.after(new Date());
        } else if (useStartTime) {
            return startTime.before(new Date());
        } else if (useEndTime) {
            return endTime.after(new Date());
        } else {
            return true;
        }
    }

    public void forceOpen() {
        forceClose = false;
        forceOpen = true;
    }

    public boolean isForceOpened() {
        return forceOpen;
    }

    public void forceClose() {
        forceOpen = false;
        forceClose = true;
    }

    public boolean isForceClosed() {
        return forceClose;
    }

    public void disableForceOpenClose() {
        forceOpen = false;
        forceClose = false;
    }

    public String colorize(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
