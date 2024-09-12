package org.firestorm.deathpro.Commands;

import org.bukkit.command.CommandSender;

public interface CommandBase {
    void execute(CommandSender player, String[] args);
}
