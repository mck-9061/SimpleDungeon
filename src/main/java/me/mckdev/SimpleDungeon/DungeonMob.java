package me.mckdev.SimpleDungeon;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class DungeonMob {
    private EntityType type;
    private String customName;
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack mainHand;
    private ItemStack offHand;
    private Entity spawned;

    public DungeonMob(ConfigurationSection config) {
        type = EntityType.valueOf(config.getString("type"));
        customName = config.getString("customName");

        ConfigurationSection equipment = config.getConfigurationSection("equipment");

        if (equipment == null) return;

        helmet = equipment.getItemStack("helmet");
        chestplate = equipment.getItemStack("chestplate");
        leggings = equipment.getItemStack("leggings");
        boots = equipment.getItemStack("boots");
        mainHand = equipment.getItemStack("hand");
        offHand = equipment.getItemStack("offHand");
    }

    public DungeonMob(DungeonMob mob) {
        type = mob.getType();
        customName = mob.getCustomName();
        helmet = mob.getHelmet();
        chestplate = mob.getChestplate();
        leggings = mob.getLeggings();
        boots = mob.getBoots();
        mainHand = mob.getMainHand();
        offHand = mob.getOffHand();
    }

    public Entity spawn(World world, Location location) {
        LivingEntity e = (LivingEntity) world.spawnEntity(location, type);
        e.setCustomName(customName);

        EntityEquipment equipment = e.getEquipment();

        if (equipment == null) {
            spawned = e;
            return e;
        }

        if (helmet != null) equipment.setHelmet(helmet);
        if (chestplate != null) equipment.setChestplate(chestplate);
        if (leggings != null) equipment.setLeggings(leggings);
        if (boots != null) equipment.setBoots(boots);
        if (mainHand != null) equipment.setItemInMainHand(mainHand);
        if (offHand != null) equipment.setItemInOffHand(offHand);

        spawned = e;

        return e;
    }

    public Entity getSpawned() {
        return spawned;
    }

    public EntityType getType() {
        return type;
    }

    public String getCustomName() {
        return customName;
    }

    public ItemStack getHelmet() {
        return helmet;
    }

    public ItemStack getChestplate() {
        return chestplate;
    }

    public ItemStack getLeggings() {
        return leggings;
    }

    public ItemStack getBoots() {
        return boots;
    }

    public ItemStack getMainHand() {
        return mainHand;
    }

    public ItemStack getOffHand() {
        return offHand;
    }
}
