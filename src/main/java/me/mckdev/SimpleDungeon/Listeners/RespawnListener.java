package me.mckdev.SimpleDungeon.Listeners;

import me.mckdev.SimpleDungeon.DatabaseManager;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.sql.SQLException;

public class RespawnListener implements Listener {
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) throws SQLException, InvalidConfigurationException {
        if (DatabaseManager.instance.hasInventory(event.getPlayer())) {
            event.getPlayer().getInventory().clear();
            DatabaseManager.instance.loadPlayerInventory(event.getPlayer());
            DatabaseManager.instance.loadPlayerLocation(event.getPlayer());
        }
    }
}
