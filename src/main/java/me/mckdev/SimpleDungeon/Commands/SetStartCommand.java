package me.mckdev.SimpleDungeon.Commands;

import me.mckdev.SimpleDungeon.DungeonSession;
import me.mckdev.SimpleDungeon.Main;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetStartCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You must be a player to use this command.");
            return true;
        }

        Player player = (Player) commandSender;
        Location location = player.getLocation();

        Main.config.set("startPosition", location);
        Main.saveDungeonConfig();

        DungeonSession.setSpawnLocation(location);


        player.sendMessage("You have set the dungeon position to your current location.");


        return true;
    }
}
