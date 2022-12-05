package me.mckdev.SimpleDungeon.Commands;

import me.mckdev.SimpleDungeon.DungeonSession;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class StartCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You must be a player to use this command.");
            return true;
        }

        // make sure the player is not already in a dungeon
        Player player = (Player) commandSender;
        if (DungeonSession.isPlayerInDungeon(player)) {
            player.sendMessage("You are already in a dungeon.");
            return true;
        }


        DungeonSession session = new DungeonSession((Player) commandSender, ((Player) commandSender).getLocation());
        try {
            session.begin();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return true;
    }
}
