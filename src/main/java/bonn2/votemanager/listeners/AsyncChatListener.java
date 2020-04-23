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
        Player player = event.getPlayer();
        if (Main.awaitingPlayers.containsKey(player.getUniqueId())) {
            Election election = Main.awaitingPlayersElection.get(player.getUniqueId());
            InputType eventType = Main.awaitingPlayers.get(player.getUniqueId());
            Main.awaitingPlayers.remove(player.getUniqueId());
            Main.awaitingPlayersElection.remove(player.getUniqueId());
            switch (eventType) {
                case AUTO_END: {
                    try {
                        Main.openEditors.remove(player.getUniqueId());
                        election.setEndTime(event.getMessage());
                        election.save();
                        player.sendMessage("Set date to: " + election.getEndTime().toInstant().toString());
                    } catch (ParseException e) {
                        player.sendMessage("That is not a valid date format!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    event.setCancelled(true);
                    return;
                }
                case MAX_VOTES: {
                    try {
                        Main.openEditors.remove(player.getUniqueId());
                        election.setMaxVotes(Integer.parseInt(event.getMessage()));
                        election.save();
                        player.sendMessage("Set max votes to: " + Integer.parseInt(event.getMessage()));
                    } catch (NumberFormatException e) {
                        player.sendMessage("Max votes must be a whole number!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    event.setCancelled(true);
                    return;
                }
                case CANDIDATE_NAME: {
                    Main.awaitingPlayers.put(player.getUniqueId(), InputType.CANDIDATE_BUTTON);
                    Main.awaitingCandidateButtonSelection.put(player.getUniqueId(),
                            new Candidate(event.getMessage(), election));
                    player.sendMessage("Click the button that you want to be associated with the candidate.");
                    event.setCancelled(true);
                }
            }
        }
    }
}
