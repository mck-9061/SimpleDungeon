package me.mckdev.SimpleDungeon.Commands;

import me.mckdev.SimpleDungeon.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public class StatsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You must be a player to use this command.");
            return true;
        }

        // Display a gui with the player's stats
        Inventory inventory = Bukkit.createInventory(null, 9, "Stats");

        try {
            int kills = DatabaseManager.instance.getKills((Player) commandSender);
            int deaths = DatabaseManager.instance.getDeaths((Player) commandSender);
            int sessions = DatabaseManager.instance.getSessions((Player) commandSender);
            float average = DatabaseManager.instance.getAverageKillsPerSession((Player) commandSender);

            if (String.valueOf(average) == "NaN") average = 0;

            inventory.setItem(1, createItemWithNameAndLore(Material.DIAMOND_SWORD, "Kills", String.valueOf(kills)));
            inventory.setItem(3, createItemWithNameAndLore(Material.BONE, "Deaths", String.valueOf(deaths)));
            inventory.setItem(5, createItemWithNameAndLore(Material.CHEST, "Sessions", String.valueOf(sessions)));
            inventory.setItem(7, createItemWithNameAndLore(Material.GOLDEN_APPLE, "Average Kills Per Session", String.valueOf(average)));

            ((Player) commandSender).openInventory(inventory);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    private ItemStack createItemWithNameAndLore(Material material, String name, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(List.of(lore));
        item.setItemMeta(meta);
        return item;
    }
}
