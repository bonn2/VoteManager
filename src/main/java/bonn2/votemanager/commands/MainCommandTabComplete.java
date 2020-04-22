package bonn2.votemanager.commands;

import bonn2.votemanager.Main;
import bonn2.votemanager.data.Election;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class MainCommandTabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> output = new ArrayList<>();
        switch (args.length) {
            case 0: {
                output.add("create");
                output.add("edit");
                output.add("delete");
                return output;
            }
            case 1: {
                if ("create".startsWith(args[0].toLowerCase())) { output.add("create"); }
                if ("edit".startsWith(args[0].toLowerCase())) { output.add("edit"); }
                if ("delete".startsWith(args[0].toLowerCase())) { output.add("delete"); }
                if ("getvotes".startsWith(args[0].toLowerCase())) { output.add("getvotes"); }
                return output;
            }
            case 2: {
                switch (args[0]) {
                    case "getvotes":
                    case "edit":
                    case "delete": {
                        for (String electionName : Main.loadedElections.keySet()) {
                            if (electionName.startsWith(args[1])) { output.add(electionName); }
                        }
                    }
                }
                return output;
            }
        }
        return null;
    }
}
