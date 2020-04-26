package bonn2.votemanager.listeners;

import bonn2.votemanager.data.Election;
import bonn2.votemanager.data.InputType;
import bonn2.votemanager.Main;
import bonn2.votemanager.inventories.EditElectionInventory;
import bonn2.votemanager.inventories.SetEndTimeInventory;
import bonn2.votemanager.inventories.SetStartTimeInventory;
import bonn2.votemanager.util.NBTEditor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Objects;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryView inventoryView = event.getView();
        ItemStack clickedItem = event.getCurrentItem();
        if (Main.openEditElectionInventories.containsKey(player.getUniqueId())) {
            if (clickedItem != null) {
                switch (Objects.requireNonNull(NBTEditor.getString(clickedItem, "VoteManager"))) {
                    case "maxVotes": {
                        EditElectionInventory editElectionInventory = Main.openEditElectionInventories.get(player.getUniqueId());
                        if (event.isShiftClick()) {
                            if (event.getClick().isLeftClick() && editElectionInventory.getElection().getMaxVotes() - 10 > 0) {
                                editElectionInventory.incrementMaxVotes(-10);
                            } else if (event.getClick().isRightClick()) {
                                editElectionInventory.incrementMaxVotes(10);
                            }
                        } else {
                            if (event.getClick().isLeftClick() && editElectionInventory.getElection().getMaxVotes() - 1 > 0) {
                                editElectionInventory.incrementMaxVotes(-1);
                            } else if (event.getClick().isRightClick()) {
                                editElectionInventory.incrementMaxVotes(1);
                            }
                        }
                        editElectionInventory.saveElection();
                        event.setCancelled(true);
                        return;
                    }
                    case "autoStart": {
                        Main.awaitingPlayersElection.put(player.getUniqueId(), Main.openEditElectionInventories.get(player.getUniqueId()).getElection());
                        if (event.getClick().isLeftClick()) {
                            SetStartTimeInventory newInventory = new SetStartTimeInventory(Main.awaitingPlayersElection.get(player.getUniqueId()));
                            inventoryView.close();
                            newInventory.open(player);
                        } else {
                            Main.openEditElectionInventories.get(player.getUniqueId()).getElection().removeStartTime();
                            try {
                                Main.openEditElectionInventories.get(player.getUniqueId()).getElection().save();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Main.openEditElectionInventories.get(player.getUniqueId()).loadItems();
                        }
                        event.setCancelled(true);
                        return;
                    }
                    case "autoEnd": {
                        Main.awaitingPlayersElection.put(player.getUniqueId(), Main.openEditElectionInventories.get(player.getUniqueId()).getElection());
                        if (event.getClick().isLeftClick()) {
                            SetEndTimeInventory newInventory = new SetEndTimeInventory(Main.awaitingPlayersElection.get(player.getUniqueId()));
                            inventoryView.close();
                            newInventory.open(player);
                        } else {
                            Main.openEditElectionInventories.get(player.getUniqueId()).getElection().removeEndTime();
                            try {
                                Main.openEditElectionInventories.get(player.getUniqueId()).getElection().save();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Main.openEditElectionInventories.get(player.getUniqueId()).loadItems();
                        }
                        event.setCancelled(true);
                        return;
                    }
                    case "candidateButton": {
                        if (event.getClick().isLeftClick()) {
                            player.sendMessage("Enter the name of the candidate.");
                            Main.awaitingPlayers.put(player.getUniqueId(), InputType.CANDIDATE_NAME);
                            Main.awaitingPlayersElection.put(player.getUniqueId(), Main.openEditElectionInventories.get(player.getUniqueId()).getElection());
                            inventoryView.close();
                        } else if (event.getClick().isRightClick()) {
                            // TODO: Remove button function
                        }
                        event.setCancelled(true);
                        return;
                    }
                    case "viewButton": {
                        if (event.getClick().isLeftClick()) {
                            player.sendMessage("Click the button that you want to be a view results button.");
                            Main.awaitingPlayers.put(player.getUniqueId(), InputType.VIEW_BUTTON);
                            Main.awaitingPlayersElection.put(player.getUniqueId(), Main.openEditElectionInventories.get(player.getUniqueId()).getElection());
                            inventoryView.close();
                        } else if (event.getClick().isRightClick()) {
                            // TODO: Remove button function
                        }
                        event.setCancelled(true);
                        return;
                    }
                    default: {} // TODO: Print error to console
                }
            } else {
                event.setCancelled(true);
            }
        } else if (Main.openEditStartTimeInventories.containsKey(player.getUniqueId())) {
            if (clickedItem != null) {
                SetStartTimeInventory setStartTimeInventory = Main.openEditStartTimeInventories.get(player.getUniqueId());
                switch (Objects.requireNonNull(NBTEditor.getString(clickedItem, "VoteManager"))) {
                    case "second": {
                        if (event.isShiftClick()) {
                            if (event.getClick().isLeftClick()) {
                                setStartTimeInventory.increment(-10);
                            } else if (event.getClick().isRightClick()) {
                                setStartTimeInventory.increment(10);
                            }
                        } else {
                            if (event.getClick().isLeftClick()) {
                                setStartTimeInventory.increment(-1);
                            } else if (event.getClick().isRightClick()) {
                                setStartTimeInventory.increment(1);
                            }
                        }
                        setStartTimeInventory.saveElection();
                        event.setCancelled(true);
                        return;
                    }
                    case "minute": {
                        if (event.isShiftClick()) {
                            if (event.getClick().isLeftClick()) {
                                setStartTimeInventory.increment(-600);
                            } else if (event.getClick().isRightClick()) {
                                setStartTimeInventory.increment(600);
                            }
                        } else {
                            if (event.getClick().isLeftClick()) {
                                setStartTimeInventory.increment(-60);
                            } else if (event.getClick().isRightClick()) {
                                setStartTimeInventory.increment(60);
                            }
                        }
                        setStartTimeInventory.saveElection();
                        event.setCancelled(true);
                        return;
                    }
                    case "hour": {
                        if (event.isShiftClick()) {
                            if (event.getClick().isLeftClick()) {
                                setStartTimeInventory.increment(-36000);
                            } else if (event.getClick().isRightClick()) {
                                setStartTimeInventory.increment(36000);
                            }
                        } else {
                            if (event.getClick().isLeftClick()) {
                                setStartTimeInventory.increment(-3600);
                            } else if (event.getClick().isRightClick()) {
                                setStartTimeInventory.increment(3600);
                            }
                        }
                        setStartTimeInventory.saveElection();
                        event.setCancelled(true);
                        return;
                    }
                    case "day": {
                        if (event.isShiftClick()) {
                            if (event.getClick().isLeftClick()) {
                                setStartTimeInventory.increment(-864000);
                            } else if (event.getClick().isRightClick()) {
                                setStartTimeInventory.increment(864000);
                            }
                        } else {
                            if (event.getClick().isLeftClick()) {
                                setStartTimeInventory.increment(-86400);
                            } else if (event.getClick().isRightClick()) {
                                setStartTimeInventory.increment(86400);
                            }
                        }
                        setStartTimeInventory.saveElection();
                        event.setCancelled(true);
                        return;
                    }
                    case "week": {
                        if (event.isShiftClick()) {
                            if (event.getClick().isLeftClick()) {
                                setStartTimeInventory.increment(-6048000);
                            } else if (event.getClick().isRightClick()) {
                                setStartTimeInventory.increment(6048000);
                            }
                        } else {
                            if (event.getClick().isLeftClick()) {
                                setStartTimeInventory.increment(-604800);
                            } else if (event.getClick().isRightClick()) {
                                setStartTimeInventory.increment(604800);
                            }
                        }
                        setStartTimeInventory.saveElection();
                        event.setCancelled(true);
                        return;
                    }
                    case "year": {
                        if (event.isShiftClick()) {
                            if (event.getClick().isLeftClick()) {
                                setStartTimeInventory.incrementYear(-10);
                            } else if (event.getClick().isRightClick()) {
                                setStartTimeInventory.incrementYear(10);
                            }
                        } else {
                            if (event.getClick().isLeftClick()) {
                                setStartTimeInventory.incrementYear(-1);
                            } else if (event.getClick().isRightClick()) {
                                setStartTimeInventory.incrementYear(1);
                            }
                        }
                        setStartTimeInventory.saveElection();
                        event.setCancelled(true);
                        return;
                    }
                    case "disable": {
                        setStartTimeInventory.disableTime();
                        setStartTimeInventory.saveElection();
                        event.setCancelled(true);
                        EditElectionInventory editElectionInventory = new EditElectionInventory(setStartTimeInventory.getElection());
                        inventoryView.close();
                        editElectionInventory.open(player);
                        return;
                    }
                }
            }
        } else if (Main.openEditEndTimeInventories.containsKey(player.getUniqueId())) {
            if (clickedItem != null) {
                SetEndTimeInventory setEndTimeInventory = Main.openEditEndTimeInventories.get(player.getUniqueId());
                switch (Objects.requireNonNull(NBTEditor.getString(clickedItem, "VoteManager"))) {
                    case "second": {
                        if (event.isShiftClick()) {
                            if (event.getClick().isLeftClick()) {
                                setEndTimeInventory.increment(-10);
                            } else if (event.getClick().isRightClick()) {
                                setEndTimeInventory.increment(10);
                            }
                        } else {
                            if (event.getClick().isLeftClick()) {
                                setEndTimeInventory.increment(-1);
                            } else if (event.getClick().isRightClick()) {
                                setEndTimeInventory.increment(1);
                            }
                        }
                        setEndTimeInventory.saveElection();
                        event.setCancelled(true);
                        return;
                    }
                    case "minute": {
                        if (event.isShiftClick()) {
                            if (event.getClick().isLeftClick()) {
                                setEndTimeInventory.increment(-600);
                            } else if (event.getClick().isRightClick()) {
                                setEndTimeInventory.increment(600);
                            }
                        } else {
                            if (event.getClick().isLeftClick()) {
                                setEndTimeInventory.increment(-60);
                            } else if (event.getClick().isRightClick()) {
                                setEndTimeInventory.increment(60);
                            }
                        }
                        setEndTimeInventory.saveElection();
                        event.setCancelled(true);
                        return;
                    }
                    case "hour": {
                        if (event.isShiftClick()) {
                            if (event.getClick().isLeftClick()) {
                                setEndTimeInventory.increment(-36000);
                            } else if (event.getClick().isRightClick()) {
                                setEndTimeInventory.increment(36000);
                            }
                        } else {
                            if (event.getClick().isLeftClick()) {
                                setEndTimeInventory.increment(-3600);
                            } else if (event.getClick().isRightClick()) {
                                setEndTimeInventory.increment(3600);
                            }
                        }
                        setEndTimeInventory.saveElection();
                        event.setCancelled(true);
                        return;
                    }
                    case "day": {
                        if (event.isShiftClick()) {
                            if (event.getClick().isLeftClick()) {
                                setEndTimeInventory.increment(-864000);
                            } else if (event.getClick().isRightClick()) {
                                setEndTimeInventory.increment(864000);
                            }
                        } else {
                            if (event.getClick().isLeftClick()) {
                                setEndTimeInventory.increment(-86400);
                            } else if (event.getClick().isRightClick()) {
                                setEndTimeInventory.increment(86400);
                            }
                        }
                        setEndTimeInventory.saveElection();
                        event.setCancelled(true);
                        return;
                    }
                    case "week": {
                        if (event.isShiftClick()) {
                            if (event.getClick().isLeftClick()) {
                                setEndTimeInventory.increment(-6048000);
                            } else if (event.getClick().isRightClick()) {
                                setEndTimeInventory.increment(6048000);
                            }
                        } else {
                            if (event.getClick().isLeftClick()) {
                                setEndTimeInventory.increment(-604800);
                            } else if (event.getClick().isRightClick()) {
                                setEndTimeInventory.increment(604800);
                            }
                        }
                        setEndTimeInventory.saveElection();
                        event.setCancelled(true);
                        return;
                    }
                    case "year": {
                        if (event.isShiftClick()) {
                            if (event.getClick().isLeftClick()) {
                                setEndTimeInventory.incrementYear(-10);
                            } else if (event.getClick().isRightClick()) {
                                setEndTimeInventory.incrementYear(10);
                            }
                        } else {
                            if (event.getClick().isLeftClick()) {
                                setEndTimeInventory.incrementYear(-1);
                            } else if (event.getClick().isRightClick()) {
                                setEndTimeInventory.incrementYear(1);
                            }
                        }
                        setEndTimeInventory.saveElection();
                        event.setCancelled(true);
                        return;
                    }
                    case "disable": {
                        setEndTimeInventory.disableTime();
                        setEndTimeInventory.saveElection();
                        event.setCancelled(true);
                        EditElectionInventory editElectionInventory = new EditElectionInventory(setEndTimeInventory.getElection());
                        inventoryView.close();
                        editElectionInventory.open(player);
                        return;
                    }
                }
            }
        }
    }

}
