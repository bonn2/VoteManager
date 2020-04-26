package bonn2.votemanager.commands;

import bonn2.votemanager.Main;
import bonn2.votemanager.data.Election;
import bonn2.votemanager.inventories.EditElectionInventory;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;

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
                        Main.loadedElections.get(args[1]).delete();
                        sender.sendMessage("Successfully deleted " + args[1]);
                        return true;
                    }
                    case "getvotes": {
                        try {
                            Election election = Main.loadedElections.get(args[1]);
                            List<String> messages = election.getTotals();
                            for (String message : messages) {
                                sender.sendMessage(colorize(message));
                            }
                        } catch (NullPointerException e) {
                            sender.sendMessage("No election by the name " + args[1]);
                        }
                        return true;
                    }
                }
            }
            default: {
                return false;
            }
        }
    }

    public String colorize(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
