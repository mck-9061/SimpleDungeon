package me.mckdev.SimpleDungeon.Commands;

import me.mckdev.SimpleDungeon.DungeonSession;
import me.mckdev.SimpleDungeon.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SetKitCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        // Sets the executor's inventory to the dungeon kit
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You must be a player to use this command.");
            return true;
        }

        Player player = (Player) commandSender;
        Inventory inventory = player.getInventory();

        DungeonSession.setKit(inventory);

        // Serialize the inventory to a string and save to config
        Map<Integer, ItemStack> serializedInventory = Main.serializeInventory(inventory);
        Main.config.set("kit", serializedInventory);
        Main.saveDungeonConfig();

        player.sendMessage("You have set the dungeon kit to your current inventory.");



        return true;
    }
}
