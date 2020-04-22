package bonn2.votemanager.commands;

import bonn2.votemanager.Main;
import bonn2.votemanager.data.Election;
import bonn2.votemanager.inventories.EditElectionInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class MainCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1: {
                return false;
            }
            case 2: {
                switch (args[0].toLowerCase()) {
                    case "create": {
                        Election election = new Election(args[1]);
                        EditElectionInventory editElectionInventory = new EditElectionInventory(election);
                        try { election.save(); } catch (IOException e) { e.printStackTrace(); }
                        Main.loadElections();
                        editElectionInventory.open((Player) sender);
                        return true;
                    }
                    case "edit": {
                        EditElectionInventory editElectionInventory = new EditElectionInventory(Main.loadedElections.get(args[1]));
                        editElectionInventory.open((Player) sender);
                        return true;
                    }
                    case "delete": {
                        return true;
                    }
                }
            }
            case 3: {

            }
            default: {
                return false;
            }
        }
    }
}
