package org.firestorm.deathpro.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.firestorm.deathpro.Commands.SubCommand.Reload;
import org.firestorm.deathpro.Commands.SubCommand.spawncorpse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final Map<String, CommandBase> commands = new HashMap<>();

    public CommandManager() {
        registerCommand("reload", new Reload());
        registerCommand("spawncorpse", new spawncorpse());
    }

    private void registerCommand(String commandName, CommandBase commandExecutor) {
        commands.put(commandName, commandExecutor);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 0) {
                player.sendMessage("Usage: /deathpro <command>");
                return true;
            }

            String subCommand = args[0].toLowerCase();

            if (commands.containsKey(subCommand)) {
                CommandBase commandExecutor = commands.get(subCommand);
                commandExecutor.execute(player, args);
            } else {
                player.sendMessage("Unknown command. Type /deathpro for help.");
            }
        } else {
            // Handle console command logic here
            if (args.length == 0) {
                sender.sendMessage("Usage: /deathpro <command>");
                return true;
            }

            String subCommand = args[0].toLowerCase();

            if (commands.containsKey(subCommand)) {
                CommandBase commandExecutor = commands.get(subCommand);
                commandExecutor.execute(sender, args);
            } else {
                sender.sendMessage("Unknown command. Type /deathpro for help.");
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Tab completion for the first argument (sub-command)
            String partialCommand = args[0].toLowerCase();

            for (String commandName : commands.keySet()) {
                if (commandName.startsWith(partialCommand)) {
                    completions.add(commandName);
                }
            }
        }

        return completions;
    }
}