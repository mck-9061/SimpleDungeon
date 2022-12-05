package me.mckdev.SimpleDungeon.Commands;

import me.mckdev.SimpleDungeon.DungeonSession;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class LeaveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You must be a player to use this command.");
            return true;
        }

        Player player = (Player) commandSender;

        if (DungeonSession.isPlayerInDungeon(player)) {
            DungeonSession session = DungeonSession.getSession(player);
            try {
                session.end();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            player.sendMessage("You have left the dungeon.");
            return true;
        } else {
            player.sendMessage("You are not in a dungeon.");
            return true;
        }
    }
}
