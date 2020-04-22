package bonn2.votemanager.listeners;

import bonn2.votemanager.Main;
import bonn2.votemanager.data.Candidate;
import bonn2.votemanager.data.Election;
import bonn2.votemanager.data.InputType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (Main.awaitingPlayers.containsKey(player.getUniqueId())) {
            if (Main.awaitingPlayers.get(player.getUniqueId()).equals(InputType.CANDIDATE_BUTTON)) {
                Main.awaitingPlayers.remove(player.getUniqueId());
                Candidate candidate = Main.awaitingCandidateButtonSelection.get(player.getUniqueId());
                Election election = candidate.getElection();
                Main.awaitingCandidateButtonSelection.remove(player.getUniqueId());
                Location buttonLocation = event.getClickedBlock().getLocation();
                election.addCandidateButton(candidate.getName(), buttonLocation);
                try {
                    election.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                event.setCancelled(true);
            }
        } else {
            System.out.println("IM HERE");
            Location buttonLocation = event.getClickedBlock().getLocation();
            for (String electionName : Main.loadedElections.keySet()) {
                System.out.println("TESTING: " + electionName);
                Main.loadedElections.get(electionName).hitButtonAt(buttonLocation, player);
            }
        }
    }
}
