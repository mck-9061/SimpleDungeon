package me.mckdev.SimpleDungeon.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class InventoryClickListener implements Listener {
    // Cancel inventory clicks in stats window
    @EventHandler
    public void onInventoryClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Stats")) {
            event.setCancelled(true);
        }
    }
}
