package bonn2.votemanager.listeners;

import bonn2.votemanager.data.Candidate;
import bonn2.votemanager.data.InputType;
import bonn2.votemanager.Main;
import bonn2.votemanager.data.Election;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.IOException;
import java.text.ParseException;

public class AsyncChatListener implements Listener {

    @EventHandler
    public void playerChatEvent(AsyncPlayerChatEvent event) {
        String clickButton = "Click the button that you want to be associated with the candidate.";

        Player player = event.getPlayer();
        if (Main.awaitingPlayers.containsKey(player.getUniqueId())) {
            Election election = Main.awaitingPlayersElection.get(player.getUniqueId());
            InputType eventType = Main.awaitingPlayers.get(player.getUniqueId());
            Main.awaitingPlayers.remove(player.getUniqueId());
            Main.awaitingPlayersElection.remove(player.getUniqueId());
            switch (eventType) {
                case CANDIDATE_NAME: {
                    Main.awaitingPlayers.put(player.getUniqueId(), InputType.CANDIDATE_BUTTON);
                    Main.awaitingCandidateButtonSelection.put(player.getUniqueId(),
                            new Candidate(event.getMessage(), election));
                    player.sendMessage(clickButton);
                    event.setCancelled(true);
                }
            }
        }
    }
}
