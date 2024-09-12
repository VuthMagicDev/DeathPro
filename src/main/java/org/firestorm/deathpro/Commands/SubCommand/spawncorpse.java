package org.firestorm.deathpro.Commands.SubCommand;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.firestorm.deathpro.Commands.CommandBase;
import org.firestorm.deathpro.DeathPro;
import org.firestorm.deathpro.Utils.Color;

public class spawncorpse implements CommandBase {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission("deathpro.spawncorpse")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                DeathPro.getInstance().getNpc().sendPlayerPacket(player);
            } else {
                sender.sendMessage(Color.colorize("&cOnly player can execute this command!"));
            }
        } else {
            sender.sendMessage(Color.colorize("&cYou don't have permission to use that command!"));
        }
    }
}
