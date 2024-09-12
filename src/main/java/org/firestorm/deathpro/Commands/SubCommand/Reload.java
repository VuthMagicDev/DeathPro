package org.firestorm.deathpro.Commands.SubCommand;


import org.bukkit.command.CommandSender;
import org.firestorm.deathpro.Commands.CommandBase;
import org.firestorm.deathpro.DeathPro;
import org.firestorm.deathpro.Utils.Color;

public class Reload implements CommandBase {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission("deathpro.reload")) {
            long time = System.currentTimeMillis();
            DeathPro.getInstance().getConfigManager().reloadConfig();
            long durations = System.currentTimeMillis() - time;
            sender.sendMessage(Color.colorize(DeathPro.getInstance().getConfigManager().getPrefix() + "&aConfig.yml is reloaded successfully took " + durations + "ms"));
        } else {
            sender.sendMessage(Color.colorize("&cYou don't have permission to use that command!"));
        }
    }
}